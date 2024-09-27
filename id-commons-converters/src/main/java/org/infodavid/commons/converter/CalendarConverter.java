package org.infodavid.commons.converter;

import java.util.Calendar;

import org.apache.commons.beanutils.converters.DateTimeConverter;

/**
 * The Class CalendarConverter.
 */
public class CalendarConverter extends DateTimeConverter implements ConverterWithDefaultType {

    /**
     * Instantiates a new converter.
     */
    public CalendarConverter() {
        setPatterns(DateConverter.DATETIME_FORMATS);
    }

    /**
     * Instantiates a new converter.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public CalendarConverter(final Object defaultValue) {
        super(defaultValue);
        setPatterns(DateConverter.DATETIME_FORMATS);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<?> getDefaultType() {
        return Calendar.class;
    }
}
