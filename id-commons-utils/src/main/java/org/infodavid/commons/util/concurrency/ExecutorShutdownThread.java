package org.infodavid.commons.util.concurrency;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExecutorShutdownThread.
 */
public class ExecutorShutdownThread extends Thread { // NOSONAR

    /** The Constant LOGGER. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExecutorShutdownThread.class);

    /**
     * The Class ShutdownRunnable.
     */
    private static class ShutdownRunnable implements Runnable {

        /** The caller. */
        private final Class<?> caller;

        /** The executor. */
        private final ExecutorService executor;

        /**
         * Instantiates a new shutdown runnable.
         * @param executor the executor
         * @param caller   the caller
         */
        public ShutdownRunnable(final ExecutorService executor, final Class<?> caller) {
            this.caller = caller;
            this.executor = executor;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            if (executor != null && !executor.isShutdown()) {
                LOGGER.info("Stopping executor associated to: {}", caller.getSimpleName());

                try {
                    ThreadUtils.getInstance().shutdown(executor);
                } catch (final InterruptedException e) {// NOSONAR Exception handled by utilities
                    ThreadUtils.getInstance().onInterruption(LOGGER, e);
                }
            }
        }
    }

    /**
     * Instantiates a new executor shutdown thread.
     * @param executor the executor
     * @param caller   the caller
     */
    public ExecutorShutdownThread(final ExecutorService executor, final Class<?> caller) {
        super(new ShutdownRunnable(executor, caller));
    }
}
