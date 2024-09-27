package org.infodavid.commons.converter;

import java.util.Date;

import org.apache.commons.beanutils.converters.DateTimeConverter;

/**
 * The Class DateConverter.
 */
public class DateConverter extends DateTimeConverter implements ConverterWithDefaultType {

    /** The date time format. */
    protected static final String[] DATETIME_FORMATS = {
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXXZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ", // ISO 8601
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyyMMdd HH:mm:ss.SSSXXX",
        "yyyyMMdd HH:mm:ss.SSS",
        "yyyyMMdd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss.SSSZ",
        "yyyy-MM-dd HH:mm:ssZ",
        "yyyy/MM/dd HH:mm:ss.SSSZ",
        "yyyy/MM/dd HH:mm:ssZ",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy/MM/dd HH:mm:ss.SSS",
        "yyyy/MM/dd HH:mm:ss",
        "EEE, d MMM yyyy HH:mm:ss Z"
    };

    /**
     * Instantiates a new converter.
     */
    public DateConverter() {
        setPatterns(DATETIME_FORMATS);
    }

    /**
     * Instantiates a new converter.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public DateConverter(final Object defaultValue) {
        super(defaultValue);
        setPatterns(DATETIME_FORMATS);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<?> getDefaultType() {
        return Date.class;
    }
}
