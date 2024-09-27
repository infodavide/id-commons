package org.infodavid.commons.converter;

import java.sql.Timestamp;

import org.apache.commons.beanutils.converters.DateTimeConverter;

/**
 * The Class SqlTimestampConverter.
 */
public class SqlTimestampConverter extends DateTimeConverter implements ConverterWithDefaultType {

    /**
     * Instantiates a new converter.
     */
    public SqlTimestampConverter() {
        setPatterns(DateConverter.DATETIME_FORMATS);
    }

    /**
     * Instantiates a new converter.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public SqlTimestampConverter(final Object defaultValue) {
        super(defaultValue);
        setPatterns(DateConverter.DATETIME_FORMATS);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<?> getDefaultType() {
        return Timestamp.class;
    }
}
