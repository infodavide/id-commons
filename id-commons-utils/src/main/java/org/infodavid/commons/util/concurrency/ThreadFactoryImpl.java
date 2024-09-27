package org.infodavid.commons.util.concurrency;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Class ThreadFactoryImpl.
 */
public class ThreadFactoryImpl implements ThreadFactory {

    /** The base name. */
    private final String basename;

    /** The exception handler. */
    private final UncaughtExceptionHandler exceptionHandler;

    /** The thread number. */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Instantiates a new thread factory.
     * @param name             the name
     * @param exceptionHandler the exception handler
     */
    public ThreadFactoryImpl(final String name, final UncaughtExceptionHandler exceptionHandler) {
        basename = name + "-pool-thread-";
        this.exceptionHandler = exceptionHandler;
    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r) {
        final Thread result = Thread.ofVirtual().name(basename + threadNumber.getAndIncrement()).unstarted(r);
        result.setUncaughtExceptionHandler(exceptionHandler);

        return result;
    }
}
