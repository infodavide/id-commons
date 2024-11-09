package org.infodavid.commons.util.collection;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

/**
 * The Class EterogenicMap.
 * @param <K> the key type
 * @param <V> the value type
 */
public class EterogenicMap<K, V> implements Map<K, V> {

    /** The delegate. */
    @Delegate
    @Getter(value = AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PROTECTED)
    private Map<K, V> delegate;

    /**
     * Instantiates a new map.
     */
    public EterogenicMap() {
        delegate = new HashMap<>();
    }

    /**
     * Instantiates a new map.
     * @param delegate the delegate
     */
    public EterogenicMap(final Map<K, V> delegate) {
        this.delegate = delegate;
    }

    /**
     * For each.
     * @param <T>    the generic type
     * @param action the action
     * @param clazz  the class
     * @see java.util.Map#forEach(java.util.function.BiConsumer)
     */
    public <T> void forEach(final BiConsumer<? super K, ? super V> action, final Class<T> clazz) {
        Objects.requireNonNull(action);

        for (final Entry<K, V> entry : delegate.entrySet()) {
            K k;
            V v;

            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (final IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }

            if (clazz.isInstance(v)) {
                action.accept(k, v);
            }
        }
    }
}
