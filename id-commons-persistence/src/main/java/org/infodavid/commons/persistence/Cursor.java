package org.infodavid.commons.persistence;

import java.io.Closeable;
import java.util.Iterator;

/**
 * The Interface Cursor.
 * @param <T> the generic type
 */
public interface Cursor<T> extends Iterator<T>, Closeable {
    // noop
}
