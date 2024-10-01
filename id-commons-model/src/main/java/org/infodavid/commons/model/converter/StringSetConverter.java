package org.infodavid.commons.model.converter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.AttributeConverter;

/**
 * The Class StringSetConverter.
 */
public class StringSetConverter implements AttributeConverter<Set<String>, String> {

    /** The Constant SEPARATOR. */
    private static final char SEPARATOR = ';';

    /*
     * (non-Javadoc)
     * @see jakarta.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(final Set<String> value) {
        if (value == null) {
            return null;
        }

        return StringUtils.join(value, SEPARATOR);
    }

    /*
     * (non-Javadoc)
     * @see jakarta.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public Set<String> convertToEntityAttribute(final String value) {
        final Set<String> results = new HashSet<>();

        if (StringUtils.isEmpty(value)) {
            return results;
        }

        Collections.addAll(results, StringUtils.split(value, SEPARATOR));

        return results;
    }
}
