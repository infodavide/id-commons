package org.infodavid.commons.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class CollectionUtils.
 */
@JsonIgnoreType
@UtilityClass
@Slf4j
public final class CollectionUtils {

    /**
     * Returns a mutable set.
     * @param <E>      the element type
     * @param elements the elements
     * @return the sets the
     */
    @SuppressWarnings("unchecked")
    public <E> Set<E> of(final E... elements) {
        return of(new HashSet<>(), elements);
    }

    /**
     * Returns the mutable collection.
     * @param <E>      the element type
     * @param <C>      the generic type
     * @param set      the set
     * @param elements the elements
     * @return the c
     */
    @SuppressWarnings("unchecked")
    public <E, C extends Collection<E>> C of(final C set, final E... elements) {
        if (elements != null) {
            Collections.addAll(set, elements);
        }

        return set;
    }

    /**
     * Returns the mutable map.
     * @param <K>   the key type
     * @param <E>   the element type
     * @param <M>   the generic type
     * @param map   the map
     * @param key   the key
     * @param value the value
     * @return the m
     */
    public <K, E, M extends Map<K, E>> M of(final M map, final K key, final E value) {
        if (key != null) {
            map.put(key, value);
        }

        return map;
    }
}
