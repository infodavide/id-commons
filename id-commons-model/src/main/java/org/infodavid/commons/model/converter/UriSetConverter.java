package org.infodavid.commons.model.converter;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.AttributeConverter;

/**
 * The Class UriSetConverter.
 */
public class UriSetConverter implements AttributeConverter<Set<URI>, String> {

    /** The Constant SEPARATOR. */
    private static final char SEPARATOR = '\n';

    /*
     * (non-Javadoc)
     * @see jakarta.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(final Set<URI> value) {
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
    public Set<URI> convertToEntityAttribute(final String value) {
        final Set<URI> results = new HashSet<>();

        if (StringUtils.isEmpty(value)) {
            return results;
        }

        for (final String line : value.split(String.valueOf(SEPARATOR))) {
            results.add(URI.create(line));
        }

        return results;
    }
}
