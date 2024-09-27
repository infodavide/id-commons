package org.infodavid.commons.converter;

import java.sql.Date;

import org.apache.commons.beanutils.converters.DateTimeConverter;

/**
 * The Class SqlDateConverter.
 */
public class SqlDateConverter extends DateTimeConverter implements ConverterWithDefaultType {

    /**
     * Instantiates a new converter.
     */
    public SqlDateConverter() {
        setPatterns(DateConverter.DATETIME_FORMATS);
    }

    /**
     * Instantiates a new converter.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public SqlDateConverter(final Object defaultValue) {
        super(defaultValue);
        setPatterns(DateConverter.DATETIME_FORMATS);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<?> getDefaultType() {
        return Date.class;
    }
}
