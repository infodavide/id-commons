package org.infodavid.commons.util.collection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.NoArgsConstructor;

/**
 * The Class NullSafeConcurrentHashMap.
 * @param <K> the key type
 * @param <V> the value type
 */
@NoArgsConstructor
public class NullSafeConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8980472784494738989L;

    /**
     * Instantiates a new null safe concurrent hash map.
     * @param initialCapacity the initial capacity
     */
    public NullSafeConcurrentHashMap(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Instantiates a new null safe concurrent hash map.
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     */
    public NullSafeConcurrentHashMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Instantiates a new null safe concurrent hash map.
     * @param initialCapacity  the initial capacity
     * @param loadFactor       the load factor
     * @param concurrencyLevel the concurrency level
     */
    public NullSafeConcurrentHashMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * Instantiates a new null safe concurrent hash map.
     * @param m the m
     */
    public NullSafeConcurrentHashMap(final Map<? extends K, ? extends V> m) {
        super(m);
    }

    /*
     * (non-javadoc)
     * @see java.util.concurrent.ConcurrentHashMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(final K key, final V value) {
        if (value == null) {
            if (containsKey(key)) {
                return remove(key);
            }

            return null;
        }

        return super.put(key, value);
    }

    /*
     * (non-javadoc)
     * @see java.util.concurrent.ConcurrentHashMap#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        m.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
    }

    /*
     * (non-javadoc)
     * @see java.util.concurrent.ConcurrentHashMap#putIfAbsent(java.lang.Object, java.lang.Object)
     */
    @Override
    public V putIfAbsent(final K key, final V value) {
        if (value == null) {
            if (containsKey(key)) {
                return remove(key);
            }

            return null;
        }

        return super.putIfAbsent(key, value);
    }
}
