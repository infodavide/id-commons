package org.infodavid.commons.util.concurrency;

import java.util.concurrent.Callable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Class NullValueCallable.
 * @param <T> the generic type
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NullValueCallable<T> implements Callable<T> {

    /** The Constant INSTANCE. */
    @SuppressWarnings("rawtypes")
    private static final NullValueCallable INSTANCE = new NullValueCallable<>();

    /**
     * Instance.
     * @param <T> the generic type
     * @return the callable
     */
    @SuppressWarnings("unchecked")
    public static final <T> Callable<T> instance() {
        return INSTANCE;
    }

    /*
     * (non-javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public T call() {
        return null;
    }
}
