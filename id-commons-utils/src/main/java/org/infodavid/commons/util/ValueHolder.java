package org.infodavid.commons.util;

import lombok.Locked;
import lombok.NoArgsConstructor;

/**
 * The Class ValueHolder.
 * @param <T> the generic type
 */
@NoArgsConstructor
public class ValueHolder<T> {

    /** The value. */
    private T value;

    /**
     * Instantiates a new value holder.
     * @param value the value
     */
    public ValueHolder(final T value) {
        this.value = value;
    }

    /**
     * Gets the object.
     * @return the object or null
     */
    @Locked.Read
    public T get() {
        return value;
    }

    /**
     * Checks if is not present.
     * @return true, if is not present
     */
    @Locked.Read
    public boolean isNotPresent() {
        return value == null;
    }

    /**
     * Checks if is present.
     * @return true, if is present
     */
    @Locked.Read
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Sets the.
     * @param value the value
     */
    @Locked.Write
    public void set(final T value) {
        this.value = value;
    }
}
