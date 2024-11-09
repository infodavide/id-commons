package org.infodavid.commons.converter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ZonedDateTimeConverter.
 */
public class ZonedDateTimeConverter extends AbstractConverter implements ConverterWithDefaultType {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZonedDateTimeConverter.class);

    /** The display patterns. */
    private String displayPatterns;

    /** The patterns. */
    private String[] patterns;

    /**
     * Instantiates a new converter.
     */
    public ZonedDateTimeConverter() {
        setPatterns(DateConverter.DATETIME_FORMATS);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T convertToType(final Class<T> targetType, final Object value) throws Throwable {
        final Class<?> sourceType = value.getClass();

        // Handle java.sql.Timestamp
        if (value instanceof java.sql.Timestamp) {
            return (T) ZonedDateTime.ofInstant(((java.sql.Timestamp) value).toInstant(), ZoneId.systemDefault());
        }

        // Handle Date (includes java.sql.Date & java.sql.Time)
        if (value instanceof Date) {
            return (T) ZonedDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault());
        }

        // Handle Calendar
        if (value instanceof Calendar) {
            return (T) ZonedDateTime.ofInstant(((Calendar) value).toInstant(), ZoneId.systemDefault());
        }

        // Handle Long
        if (value instanceof Long) {
            return (T) ZonedDateTime.ofInstant(Instant.ofEpochMilli(((Long) value).longValue()), ZoneId.systemDefault());
        }

        // Convert all other types to String & handle
        final String stringValue = value.toString().trim();

        if (StringUtils.trim(stringValue).isEmpty()) {
            return handleMissing(targetType);
        }

        Exception exception = null;

        if (patterns == null || patterns.length == 0) {
            LOGGER.debug("Trying to parse '{}' using ISO date time pattern", stringValue);

            return (T) ZonedDateTime.parse(stringValue, DateTimeFormatter.ISO_DATE_TIME);
        }

        for (final String pattern : patterns) {
            LOGGER.debug("Trying to parse '{}' using pattern: '{}'", stringValue, pattern);

            try {
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

                return (T) ZonedDateTime.parse(stringValue, formatter);
            } catch (final Exception e) {
                if (exception == null) {
                    exception = e;
                }
            }
        }

        throw new ConversionException("Error converting '" + ConverterUtils.toString(sourceType) + "' to '" + ConverterUtils.toString(targetType) + "' using  patterns '" + displayPatterns + '\'');
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<?> getDefaultType() {
        return ZonedDateTime.class;
    }

    /**
     * Return the date format patterns used to convert dates to/from a <code>java.lang.String</code> (or <code>null</code> if none specified).
     * @return Array of format patterns.
     * @see SimpleDateFormat
     */
    public String[] getPatterns() {
        return patterns;
    }

    /**
     * Set a date format pattern to use to convert dates to/from a <code>java.lang.String</code>.
     * @param pattern The format pattern.
     * @see SimpleDateFormat
     */
    public void setPattern(final String pattern) {
        setPatterns(new String[] { pattern });
    }

    /**
     * Set the date format patterns to use to convert dates to/from a <code>java.lang.String</code>.
     * @param patterns Array of format patterns.
     * @see SimpleDateFormat
     */
    public void setPatterns(final String[] patterns) {
        this.patterns = patterns;

        if (patterns != null && patterns.length > 1) {
            final String buffer = String.join(", ", patterns);

            displayPatterns = buffer;
        }
    }
}
