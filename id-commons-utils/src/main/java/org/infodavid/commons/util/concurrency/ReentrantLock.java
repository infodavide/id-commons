package org.infodavid.commons.util.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.infodavid.commons.util.exception.CannotAcquireLockException;
import org.slf4j.Logger;

/**
 * The Class ReentrantLock.
 */
public class ReentrantLock extends java.util.concurrent.locks.ReentrantLock { // NOSONAR Keep same name

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5538994257109017051L;

    /** The logger. */
    private final transient Logger logger;

    /**
     * Instantiates a new reentrant lock.
     * @param logger the logger
     */
    public ReentrantLock(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Instantiates a new reentrant lock.
     * @param logger the logger
     * @param fair   the fair
     */
    public ReentrantLock(final Logger logger, final boolean fair) {
        super(fair);
        this.logger = logger;
    }

    /**
     * Try lock.
     * @param <T>      the generic type
     * @param time     the time
     * @param unit     the unit
     * @param callable the callable
     * @return the t
     * @throws InterruptedException the interrupted exception
     */
    public <T> T tryLock(final long time, final TimeUnit unit, final Callable<T> callable) throws InterruptedException {
        return tryLock(time, unit, callable, null);
    }

    /**
     * Try lock.
     * @param <T>            the generic type
     * @param <E>            the element type
     * @param time           the time
     * @param unit           the unit
     * @param callable       the callable
     * @param exceptionClass the exception class
     * @return the result
     * @throws E                    the exception as specified using the generic type
     * @throws InterruptedException the interrupted exception
     */
    public <T, E extends Exception> T tryLock(final long time, final TimeUnit unit, final Callable<T> callable, final Class<E> exceptionClass) throws E, InterruptedException {
        return tryLock(time, unit, callable, exceptionClass, true);
    }

    /**
     * Try lock.
     * @param <T>              the generic type
     * @param <E>              the element type
     * @param time             the time
     * @param unit             the unit
     * @param callable         the callable
     * @param exceptionClass   the exception class
     * @param acquireException true to silence acquire exception
     * @return the result
     * @throws E                    the exception as specified using the generic type
     * @throws InterruptedException the interrupted exception
     */
    @SuppressWarnings("unchecked")
    public <T, E extends Exception> T tryLock(final long time, final TimeUnit unit, final Callable<T> callable, final Class<E> exceptionClass, final boolean acquireException) throws E, InterruptedException {
        final long t;
        final TimeUnit u;

        if (ThreadUtils.debugging) {
            t = 24;
            u = TimeUnit.HOURS;
        } else {
            t = time;
            u = unit;
        }

        if (tryLock(t, u)) {
            logger.trace("Lock acquired");
            InterruptedException interruption = null;

            try {
                return callable.call();
            } catch (final InterruptedException e) { // NOSONAR Exception re-thrown after finally
                interruption = e;
            } catch (final Exception e) {
                if (exceptionClass.isInstance(e)) {
                    throw (E) e;
                }

                logger.error("An unexpected error occurred in synchronized process", e);
            } finally {
                unlock();
                logger.trace("Lock released");
            }

            if (interruption != null) {
                throw interruption;
            }
        } else if (acquireException) {
            final Thread thread = getOwner();
            final StackTraceElement[] stack = thread.getStackTrace();
            String trace = "Unavailable";

            if (stack.length > 0) {
                trace = stack[0].toString();
            }

            throw new CannotAcquireLockException("Cannot acquire lock, owner is: " + thread.getName() + " at: " + trace);
        }

        return null;
    }
}
