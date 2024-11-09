package org.infodavid.commons.util.collection;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class MapUtils.
 */
@JsonIgnoreType
@UtilityClass
@Slf4j
public final class MapUtils {

    /** The Constant DEFAULT_PATH_SEPARATOR. */
    public static final char DEFAULT_PATH_SEPARATOR = '/';

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
