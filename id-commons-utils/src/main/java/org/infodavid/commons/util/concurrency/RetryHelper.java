package org.infodavid.commons.util.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

/**
 * The Class RetryHelper.
 * @param <T> the generic type
 */
public class RetryHelper<T> {

    /** The continue on errors. */
    private Class<? extends Exception>[] continueOnExceptions;

    /** The logger. */
    private final Logger logger;

    /** The max retries. */
    private int maxRetries;

    /** The sleep lock. */
    private final SleepLock sleepLock = new SleepLock();

    /** The sleep time in ms. */
    private final long sleepTimeInMs;

    /**
     * Instantiates a new retry helper.
     * @param logger        the logger
     * @param maxRetries    the max retries
     * @param sleepTimeInMs the sleep time in ms
     */
    public RetryHelper(final Logger logger, final int maxRetries, final long sleepTimeInMs) {
        this.logger = logger;
        this.maxRetries = maxRetries;
        this.sleepTimeInMs = sleepTimeInMs;
    }

    /**
     * Gets the continue on errors.
     * @return the continueOnErrors
     */
    public Class<? extends Exception>[] getContinueOnErrors() {
        return continueOnExceptions;
    }

    /**
     * Run.
     * @param function the function
     * @return the t
     * @throws Exception the exception
     */
    public T run(final Callable<T> function) throws Exception {
        try {
            return function.call();
        } catch (final NullPointerException e) {
            throw e;
        } catch (final Exception e) { // NOSONAR
            if (continueOnErrors(e)) {
                logger.debug("Recovery", e);

                return retry(function);
            }

            throw e;
        }
    }

    /**
     * Sets the continue on errors.
     * @param exceptions the exceptions to set
     */
    @SuppressWarnings("unchecked")
    public void setContinueOnErrors(final Class<? extends Exception>... exceptions) {
        this.continueOnExceptions = exceptions;
    }

    /**
     * Continue on errors.
     * @param e the e
     * @return true, if successful
     * @throws Exception the exception
     */
    private boolean continueOnErrors(final Exception e) throws Exception { // NOSONAR
        if (continueOnExceptions == null) {
            return false;
        }

        for (final Class<? extends Exception> item : continueOnExceptions) {
            if (!item.isInstance(e)) {
                throw e;
            }
        }

        return true;
    }

    /**
     * Retry.
     * @param function the function
     * @return the t
     * @throws Exception the exception
     */
    private T retry(final Callable<T> function) throws Exception {
        Exception exception;
        sleepLock.lock();

        try {
            do {
                logger.warn("Processing failed, {} retrie(s) remaining. ({})", String.valueOf(maxRetries), function); // NOSONAR Always written

                try {
                    return function.call();
                } catch (final Exception e) {
                    if (!continueOnErrors(e)) {
                        throw e;
                    }

                    if (maxRetries > 0) {
                        logger.debug("Recovery", e);
                    }

                    exception = e;
                    maxRetries--;

                    if (sleepTimeInMs > 0) {
                        // wait before next try
                        try {
                            sleepLock.await(sleepTimeInMs, TimeUnit.MILLISECONDS);
                        } catch (final InterruptedException e2) {
                            throw e2;
                        } catch (final Exception e2) {
                            logger.trace("An error occured during sleep", e2);
                        }
                    }
                }
            } while (maxRetries > 0);
        } finally {
            sleepLock.unlock();
        }

        throw exception;
    }
}
