package org.infodavid.commons.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.beanutils.converters.DateTimeConverter;

/**
 * The Class LocalDateTimeConverter.
 */
public class LocalDateTimeConverter extends DateTimeConverter implements ConverterWithDefaultType {

    /**
     * Instantiates a new converter.
     */
    public LocalDateTimeConverter() {
        setPatterns(DateConverter.DATETIME_FORMATS);
    }

    /**
     * Instantiates a new converter.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public LocalDateTimeConverter(final Object defaultValue) {
        super(defaultValue);
        setPatterns(DateConverter.DATETIME_FORMATS);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T convert(final Class<T> targetType, final Object value) {
        if (targetType.isInstance(value)) {
            return targetType.cast(value);
        }

        final Date date = super.convert(Date.class, value);

        return (T) LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<?> getDefaultType() {
        return LocalDateTime.class;
    }
}