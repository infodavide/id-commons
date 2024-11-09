package org.infodavid.commons.util.logging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.lang3.SystemUtils;
import org.infodavid.commons.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.github.dtmo.jfiglet.FigFontResources;
import com.github.dtmo.jfiglet.FigletRenderer;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class LoggingUtils.
 */
@JsonIgnoreType
@UtilityClass
@Slf4j
public final class LoggingUtils {

    /**
     * Append banner.
     * @param applicationName the application name
     * @param buffer          the buffer
     */
    public void appendBanner(final String applicationName, final StringBuilder buffer) {
        buffer.append(StringUtils.CR);

        try {
            final FigletRenderer renderer = new FigletRenderer(FigFontResources.loadFigFontResource(FigFontResources.BANNER_FLF));

            buffer.append(renderer.renderText(applicationName));
        } catch (@SuppressWarnings("unused") final IOException e) { // NOSONAR
            buffer.append(applicationName);
        }

        buffer.append(StringUtils.CR);
    }

    /**
     * Log.
     * @param application the application
     * @param entries     the entries
     * @param full        the full
     * @param buffer      the buffer
     */
    public void log(final String application, final Map<String, ?> entries, final boolean full, final StringBuilder buffer) {
        buffer.append(StringUtils.CR);
        appendBanner(application, buffer);

        for (final Entry<String, ?> entry : entries.entrySet()) {
            buffer.append(StringUtils.CR);
            buffer.append(entry.getKey());
            buffer.append(": ");

            final Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            StringUtils.toString(value, buffer);
        }

        buffer.append(StringUtils.CR);
        buffer.append("Current Java version: ");
        buffer.append(SystemUtils.JAVA_VERSION);
        buffer.append(StringUtils.CR);
        buffer.append("File encoding: ");
        buffer.append(System.getProperty("file.encoding"));
        buffer.append(StringUtils.CR);
        buffer.append("Default charset: ");
        buffer.append(Charset.defaultCharset());
        buffer.append(StringUtils.CR);

        if (full) {
            buffer.append(StringUtils.CR);
            buffer.append("System properties:");
            System.getProperties().entrySet().forEach(e -> {
                buffer.append(StringUtils.CR);
                buffer.append(StringUtils.TAB);
                buffer.append(e.getKey());
                buffer.append(':');
                buffer.append(e.getValue());
            });
            buffer.append(StringUtils.CR);
            buffer.append("Envrionment variables:");
            System.getenv().entrySet().forEach(e -> {
                buffer.append(StringUtils.CR);
                buffer.append(StringUtils.TAB);
                buffer.append(e.getKey());
                buffer.append(':');
                buffer.append(e.getValue());
            });
        }
    }

    /**
     * Log.
     * @param application the application
     * @param entries     the entries
     * @param buffer      the buffer
     */
    public void log(final String application, final Map<String, ?> entries, final StringBuilder buffer) {
        log(application, entries, false, buffer);
    }

    /**
     * Map level.
     * @param value the value
     * @return the level
     */
    public Level toLevel(final org.slf4j.event.Level value) {
        if (value == null) {
            return null;
        }

        // [ALL -> TRACE]
        // FINEST -> TRACE
        // FINER -> DEBUG
        // FINE -> DEBUG
        // [CONFIG -> INFO]
        // INFO -> INFO
        // WARNING -> WARN
        // SEVERE -> ERROR
        // [OFF -> ERROR]
        if (org.slf4j.event.Level.TRACE.equals(value)) {
            return Level.FINEST;
        }

        if (org.slf4j.event.Level.DEBUG.equals(value)) {
            return Level.FINE;
        }

        if (org.slf4j.event.Level.INFO.equals(value)) {
            return Level.INFO;
        }

        if (org.slf4j.event.Level.WARN.equals(value)) {
            return Level.WARNING;
        }

        if (org.slf4j.event.Level.ERROR.equals(value)) {
            return Level.SEVERE;
        }

        return null;
    }

    /**
     * Map level.
     * @param value the value
     * @return the level
     */
    public Level toLevel(final String value) {
        if (value == null) {
            return null;
        }

        Level result = null;

        try {
            result = Level.parse(value);
        } catch (@SuppressWarnings("unused") final Exception e) {
            // [ALL -> TRACE]
            // FINEST -> TRACE
            // FINER -> DEBUG
            // FINE -> DEBUG
            // [CONFIG -> INFO]
            // INFO -> INFO
            // WARNING -> WARN
            // SEVERE -> ERROR
            // [OFF -> ERROR]
            if ("TRACE".equalsIgnoreCase(value)) {
                return Level.FINEST;
            }

            if ("DEBUG".equalsIgnoreCase(value)) {
                return Level.FINE;
            }

            if ("INFO".equalsIgnoreCase(value)) {
                return Level.INFO;
            }

            if ("WARN".equalsIgnoreCase(value)) {
                return Level.WARNING;
            }

            if ("ERROR".equalsIgnoreCase(value)) {
                return Level.SEVERE;
            }
        }

        return result;
    }

    /**
     * Map level.
     * @param value the value
     * @return the string
     */
    public org.slf4j.event.Level toSlf4JLevel(final Level value) {
        if (value == null) {
            return null;
        }

        // [ALL -> TRACE]
        // FINEST -> TRACE
        // FINER -> DEBUG
        // FINE -> DEBUG
        // [CONFIG -> INFO]
        // INFO -> INFO
        // WARNING -> WARN
        // SEVERE -> ERROR
        // [OFF -> ERROR]
        if (Level.ALL.equals(value) || Level.FINEST.equals(value)) {
            return org.slf4j.event.Level.TRACE;
        }

        if (Level.FINER.equals(value) || Level.FINE.equals(value)) {
            return org.slf4j.event.Level.DEBUG;
        }

        if (Level.CONFIG.equals(value) || Level.INFO.equals(value)) {
            return org.slf4j.event.Level.INFO;
        }

        if (Level.WARNING.equals(value)) {
            return org.slf4j.event.Level.WARN;
        }

        if (Level.SEVERE.equals(value) || Level.OFF.equals(value)) {
            return org.slf4j.event.Level.ERROR;
        }

        return null;
    }

    /**
     * Map level.
     * @param value the value
     * @return the string
     */
    public String toString(final Level value) {
        if (value == null) {
            return null;
        }

        // [ALL -> TRACE]
        // FINEST -> TRACE
        // FINER -> DEBUG
        // FINE -> DEBUG
        // [CONFIG -> INFO]
        // INFO -> INFO
        // WARNING -> WARN
        // SEVERE -> ERROR
        // [OFF -> ERROR]
        if (Level.ALL.equals(value) || Level.FINEST.equals(value)) {
            return "TRACE";
        }

        if (Level.FINER.equals(value) || Level.FINE.equals(value)) {
            return "DEBUG";
        }

        if (Level.CONFIG.equals(value) || Level.INFO.equals(value)) {
            return "INFO";
        }

        if (Level.WARNING.equals(value)) {
            return "WARN";
        }

        if (Level.SEVERE.equals(value) || Level.OFF.equals(value)) {
            return "ERROR";
        }

        return value.getName();
    }
}
