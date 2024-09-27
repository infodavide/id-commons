package org.infodavid.commons.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SerializableFilter.
 */
public class SerializableFilter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SerializableFilter.class);

    /**
     * Filter.
     * @param value the value
     * @return the object
     */
    public Serializable filter(final Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Serializable serializable) {
            return serializable;
        }

        if (value instanceof List<?> list) {
            return (Serializable) filterCollection(list, new ArrayList<>());
        }

        if (value instanceof Map<?, ?> map) {
            return (Serializable) filterMap(map, new HashMap<>());
        }

        if (value instanceof Collection<?> collection) {
            return (Serializable) filterCollection(collection, new LinkedHashSet<>());
        }

        if (value.getClass().isArray()) {
            return filterArray(value);
        }

        LOGGER.warn("Object is not Serializable and will be ignored: {}", value);

        return null;
    }

    /**
     * Filter.
     * @param source the source
     * @param result the mapped
     * @return the collection
     */
    private Collection<Serializable> filterCollection(final Collection<?> source, final Collection<Serializable> result) {
        result.addAll(source.stream().map(this::filter).filter(s -> s != null).toList());

        return result;
    }

    /**
     * Filter.
     * @param source the source
     * @param result the result
     * @return the map
     */
    private <K> Map<K, Serializable> filterMap(final Map<K, ?> source, Map<K, Serializable> result) {
        for (final Entry<K, ?> sourceEntry : source.entrySet()) {
            final Serializable serializable = filter(sourceEntry.getValue());

            if (serializable != null) {
                result.put(sourceEntry.getKey(), serializable);
            }
        }

        return result;
    }

    /**
     * Filter array.
     * @param value the value
     * @return the object
     */
    private Serializable[] filterArray(final Object value) {
        final List<Serializable> mapped = new ArrayList<>();

        for (final Object sourceItem : Arrays.asList(value)) {
            final Serializable serializable = filter(sourceItem);

            if (serializable != null) {
                mapped.add(serializable);
            }
        }

        final Object result = Array.newInstance(Serializable.class, mapped.size());

        for (int i = 0; i < mapped.size(); i++) {
            Array.set(result, i, mapped.get(i));
        }

        return (Serializable[]) result;
    }
}
