package org.infodavid.commons.util.collection;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * The Class MapUtils.
 */
@SuppressWarnings("static-method")
@JsonIgnoreType
public final class MapUtils {

    /** The Constant DEFAULT_PATH_SEPARATOR. */
    public static final char DEFAULT_PATH_SEPARATOR = '/';

    /** The singleton. */
    private static WeakReference<MapUtils> instance = null;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MapUtils.class);

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized MapUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new MapUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new utilities.
     */
    private MapUtils() {
    }

    /**
     * Gets the item.
     * @param map  the map
     * @param path the path
     * @return the item
     */
    public Object getItem(final Map<String, Object> map, final String path) {
        return getItem(map, path, DEFAULT_PATH_SEPARATOR);
    }

    /**
     * Gets the item.
     * @param map       the map
     * @param path      the path
     * @param separator the separator
     * @return the item
     */
    @SuppressWarnings("unchecked")
    public Object getItem(final Map<String, Object> map, final String path, final char separator) {
        LOGGER.debug("Retrieving entry using path: {}", path);

        if (StringUtils.isEmpty(path) || path.length() == 1 && separator == path.charAt(0)) {
            LOGGER.debug("Entry: {}", map);

            return map;
        }

        Object value = map;

        for (final String part : StringUtils.split(path, separator)) {
            if (!(value instanceof Map)) {
                return null;
            }

            value = ((Map<String, Object>) value).get(part);
        }

        LOGGER.debug("Entry: {}", value);

        return value;
    }
}
