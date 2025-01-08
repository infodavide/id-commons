package org.infodavid.commons.model.converter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.AttributeConverter;

/**
 * The Class StringMapConverter.
 */
public class StringMapConverter implements AttributeConverter<Map<String, String>, String> {

    /** The Constant SEPARATOR. */
    private static final char SEPARATOR = '\n';

    /*
     * (non-Javadoc)
     * @see jakarta.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(final Map<String, String> value) {
        if (value == null) {
            return null;
        }

        final StringBuilder buffer = new StringBuilder();

        value.entrySet().forEach(e -> buffer.append(e.getKey()).append('=').append(e.getValue()).append(SEPARATOR));

        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     * @see jakarta.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public Map<String, String> convertToEntityAttribute(final String value) {
        final Map<String, String> results = new HashMap<>();

        if (StringUtils.isEmpty(value)) {
            return results;
        }

        for (final String line : value.split(String.valueOf(SEPARATOR))) {
            final String[] parts = line.split("=");

            if (parts.length == 2) {
                results.put(parts[0], parts[1]);
            }
        }

        return results;
    }
}
