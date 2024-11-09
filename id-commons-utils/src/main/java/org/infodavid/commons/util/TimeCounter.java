package org.infodavid.commons.util;

import java.util.concurrent.Callable;

import lombok.Getter;

/**
 * The Class TimeCounter.
 * @param <V> the value type
 */
public class TimeCounter<V> {

    /** The callable. */
    private final Callable<V> callable;

    /**
     * Instantiates a new time counter.
     * @param callable the callable
     */
    public TimeCounter(final Callable<V> callable) {
        this.callable = callable;
    }

    /** The duration. */
    @Getter
    private long duration = 0;

    /**
     * Run.
     * @return the result
     * @throws Exception the exception
     */
    public V run() throws Exception {
        final long t1 = System.nanoTime();
        final V result;

        try {
            result = callable.call();
        } finally {
            duration = System.nanoTime() - t1;
        }

        return result;
    }
}
