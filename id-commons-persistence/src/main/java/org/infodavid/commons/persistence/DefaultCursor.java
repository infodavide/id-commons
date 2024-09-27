package org.infodavid.commons.persistence;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import org.infodavid.commons.model.PersistentObject;

/**
 * The Class DefaultCursor.
 * @param <T> the generic type
 */
public class DefaultCursor<T extends PersistentObject<? extends Serializable>> implements Cursor<T> {

    /** The iterator. */
    private final Iterator<T> iterator;

    /**
     * Instantiates a new default cursor.
     * @param iterable the iterable
     */
    public DefaultCursor(final Iterable<T> iterable) {
        iterator = iterable.iterator();
    }

    /**
     * Instantiates a new default cursor.
     * @param iterator the iterator
     */
    public DefaultCursor(final Iterator<T> iterator) {
        this.iterator = iterator;

    }

    /*
     * (non-javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        // noop
    }

    /*
     * (non-javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /*
     * (non-javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
        return iterator.next();
    }
}
