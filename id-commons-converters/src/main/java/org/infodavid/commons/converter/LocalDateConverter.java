package org.infodavid.commons.converter;

import java.time.LocalDate;
import java.util.Calendar;

import org.apache.commons.beanutils.converters.DateTimeConverter;

/**
 * The Class LocalDateConverter.
 */
public class LocalDateConverter extends DateTimeConverter implements ConverterWithDefaultType {

    /** The date time format. */
    protected static final String[] DATE_FORMATS = {
        "yyyy-MM-dd",
        "yyyyMMdd",
        "EEE, d MMM yyyy"
    };

    /**
     * Instantiates a new converter.
     */
    public LocalDateConverter() {
        setPatterns(DATE_FORMATS);
    }

    /**
     * Instantiates a new converter.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public LocalDateConverter(final Object defaultValue) {
        super(defaultValue);
        setPatterns(DATE_FORMATS);
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

        final Calendar date = super.convert(Calendar.class, value);

        return (T) LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.util.converter.ConverterWithDefaultType#getDefaultType()
     */
    @Override
    public Class<?> getDefaultType() {
        return LocalDate.class;
    }
}
