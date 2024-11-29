package org.infodavid.commons.rest.v1.mapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.util.logging.LoggingUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * The Interface CommonMapper.
 */
@Mapper
public interface CommonMapper { // NOSONAR See Mapstruct documentation

    /** The instance. */
    CommonMapper INSTANCE = Mappers.getMapper(CommonMapper.class);

    /**
     * Map.
     * @param value the value
     * @return the string
     */
    default String map(final byte[] value) {
        if (value == null) {
            return null;
        }

        return new String(value, StandardCharsets.UTF_8);
    }

    /**
     * Map.
     * @param date the date
     * @return the date
     */
    default Date map(final LocalDate date) {
        return date == null ? null : Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Map.
     * @param date the date
     * @return the date
     */
    default Date map(final LocalDateTime date) {
        return date == null ? null : Date.from(date.toInstant(ZoneOffset.of(ZoneId.systemDefault().getId())));
    }

    /**
     * Map.
     * @param value the value
     * @return the byte[]
     */
    default byte[] map(final String value) {
        if (value == null) {
            return null; // NOSONAR
        }

        return value.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Map.
     * @param date the date
     * @return the local date
     */
    default LocalDate mapDate(final Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Map.
     * @param date the date
     * @return the local date
     */
    default LocalDateTime mapDateTime(final Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Map integer.
     * @param value the value
     * @return the integer
     */
    default Integer mapInteger(final int value) {
        return Integer.valueOf(value);
    }

    /**
     * Map integer.
     * @param value the value
     * @return the integer
     */
    default Integer mapInteger(final String value) {
        return StringUtils.isNumeric(value) ? Integer.valueOf(value) : null;
    }

    /**
     * Map log level.
     * @param value the value
     * @return the string
     */
    default String mapLogLevel(final Level value) {
        return LoggingUtils.toString(value);
    }

    /**
     * Map log level.
     * @param value the value
     * @return the level
     */
    default Level mapLogLevel(final String value) {
        return LoggingUtils.toLevel(value);
    }

    /**
     * Map long.
     * @param value the value
     * @return the long
     */
    default Long mapLong(final long value) {
        return Long.valueOf(value);
    }

    /**
     * Map long.
     * @param value the value
     * @return the long
     */
    default Long mapLong(final String value) {
        return StringUtils.isNumeric(value) ? Long.valueOf(value) : null;
    }

    /**
     * Map path.
     * @param path the path
     * @return the string
     */
    default String mapPath(final Path path) {
        return path == null ? null : path.toString();
    }

    /**
     * Map path.
     * @param path the path
     * @return the path
     */
    default Path mapPath(final String path) {
        return StringUtils.isEmpty(path) ? null : Paths.get(path);
    }

    /**
     * Map pattern.
     * @param pattern the pattern
     * @return the string
     */
    default String mapPattern(final Pattern pattern) {
        return pattern == null ? null : pattern.pattern();
    }

    /**
     * Map pattern.
     * @param pattern the pattern
     * @return the pattern
     */
    default Pattern mapPattern(final String pattern) {
        return StringUtils.isEmpty(pattern) ? null : Pattern.compile(pattern);
    }
}
