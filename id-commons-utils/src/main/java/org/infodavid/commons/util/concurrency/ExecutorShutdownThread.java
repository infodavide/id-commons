package org.infodavid.commons.util.concurrency;

import java.util.concurrent.ExecutorService;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class ExecutorShutdownThread.
 */
@Slf4j
public class ExecutorShutdownThread extends Thread { // NOSONAR

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
                    ThreadUtils.shutdown(executor);
                } catch (final InterruptedException e) {// NOSONAR Exception handled by utilities
                    LOGGER.warn("Thread interrupted", e);
                    Thread.currentThread().interrupt();
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
