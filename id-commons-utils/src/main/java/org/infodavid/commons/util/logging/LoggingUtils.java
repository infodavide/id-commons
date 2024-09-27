package org.infodavid.commons.util.logging;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.infodavid.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.github.dtmo.jfiglet.FigFontResources;
import com.github.dtmo.jfiglet.FigletRenderer;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.sift.AppenderTracker;
import ch.qos.logback.core.spi.AbstractComponentTracker;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.util.FileSize;

/**
 * The Class LoggingUtils.
 */
@SuppressWarnings("static-method")
@JsonIgnoreType
public final class LoggingUtils {

    /** The encoder pattern. */
    public static final String ENCODER_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

    /** The Constant ROLLING_SUFFIX. */
    public static final String ROLLING_SUFFIX = "-%d{yyyy-MM-dd}.%i.log.gz";

    /** The singleton. */
    private static WeakReference<LoggingUtils> instance = null;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtils.class);

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized LoggingUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new LoggingUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new utilities.
     */
    private LoggingUtils() {
    }

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
     * Close the appender.
     * @param appender the appender
     */
    public void close(final Appender<ILoggingEvent> appender) {
        if (appender == null) {
            LOGGER.warn("Given appender is null and cannot be closed");

            return;
        }

        LOGGER.debug("Closing appender: {}", appender.getName());

        // Appender can be reused if state machine is halted abnormally and started again
        if ("Finalizer".equalsIgnoreCase(Thread.currentThread().getName())) {
            LOGGER.debug("Resource will not be closed by the finalizer thread: {}", appender.getName());
        } else {
            final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            final ch.qos.logback.classic.Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);

            root.detachAppender(appender);

            if (context.getStatusManager() != null) {
                context.getStatusManager().clear();
            }

            // force stop and removal of sub-appenders
            if (appender instanceof SiftingAppender siftingAppender) {
                final AppenderTracker<ILoggingEvent> tracker = siftingAppender.getAppenderTracker();

                if (tracker != null) {
                    for (final String componentKey : tracker.allKeys()) {
                        final Appender<ILoggingEvent> component = tracker.find(componentKey);
                        tracker.endOfLife(componentKey);
                        close(component);
                    }

                    tracker.removeStaleComponents(System.currentTimeMillis() + AbstractComponentTracker.LINGERING_TIMEOUT + 5000);
                }
            }

            // force stop and removal of sub-appenders
            if (appender instanceof AsyncAppender asyncAppender) {
                final Iterator<Appender<ILoggingEvent>> ite = asyncAppender.iteratorForAppenders();

                if (ite != null) {
                    while (ite.hasNext()) {
                        close(ite.next());
                    }
                }
            }

            // Stop and remove policies
            if (appender instanceof RollingFileAppender) {
                final RollingFileAppender<ILoggingEvent> rollingFileAppender = (RollingFileAppender<ILoggingEvent>) appender;

                if (rollingFileAppender.getRollingPolicy() != null) {
                    rollingFileAppender.getRollingPolicy().stop();
                    rollingFileAppender.getRollingPolicy().setParent(null);
                }

                rollingFileAppender.setRollingPolicy(null);

                if (rollingFileAppender.getTriggeringPolicy() != null) {
                    rollingFileAppender.getTriggeringPolicy().stop();
                }

                rollingFileAppender.setTriggeringPolicy(null);
            }
        }

        LOGGER.debug("Appender: {} is now closed", appender.getName());
    }

    /**
     * Close the logger.
     * @param logger the logger
     */
    @SuppressWarnings("unchecked")
    public void close(final Logger logger) {
        if (logger == null) {
            LOGGER.warn("Given logger is null and cannot be closed");

            return;
        }

        LOGGER.debug("Closing logger: {}", logger.getName());

        try {
            final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            final ch.qos.logback.classic.Logger logbackLogger = context.getLogger(logger.getName());

            if (logbackLogger != null) {
                logbackLogger.detachAndStopAllAppenders();
            }

            final java.util.Map<String, ch.qos.logback.classic.Logger> loggerCache = (Map<String, ch.qos.logback.classic.Logger>) FieldUtils.readField(context, "loggerCache", true);

            if (loggerCache.get(logger.getName()) == logger) {
                loggerCache.remove(logger.getName());
            }
        } catch (final IllegalAccessException e) {
            LOGGER.error("An error occured while removing logger from logback cache", e);
        }

        LOGGER.debug("Logger: {} is now closed", logger.getName());
    }

    /**
     * Gets the appender.
     * @param identifier the identifier
     * @return the appender
     * @throws IllegalAccessException the illegal access exception
     */
    @SuppressWarnings("unchecked")
    public Appender<ILoggingEvent> getAppender(final String identifier) throws IllegalAccessException {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        final ch.qos.logback.classic.Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        final AppenderAttachable<ILoggingEvent> aai = (AppenderAttachable<ILoggingEvent>) FieldUtils.readField(root, "aai", true);

        return aai.getAppender(identifier);
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

            StringUtils.getInstance().toString(value, buffer);
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
     * Creates the appender.
     * @param file         the file
     * @param attachToRoot the attach to root
     * @return the file appender
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public FileAppender<ILoggingEvent> newFileAppender(final Path file, final boolean attachToRoot) throws IOException {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        final FileAppender<ILoggingEvent> result = new FileRecreatingFileAppender<>();
        Files.deleteIfExists(file);
        Files.createDirectories(file.getParent());
        result.setName(file.getFileName().toString());
        result.setFile(file.toString());
        result.setContext(context);
        result.setEncoder(newPatternLayoutEncoder());
        result.start();

        if (attachToRoot) {
            context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(result);
        }

        return result;
    }

    /**
     * New pattern layout encoder.
     * @return the pattern layout encoder
     */
    public PatternLayoutEncoder newPatternLayoutEncoder() {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder result = new PatternLayoutEncoder();
        result.setContext(context);
        result.setPattern(ENCODER_PATTERN);
        result.setCharset(StandardCharsets.UTF_8);
        result.start();

        return result;
    }

    /**
     * New rolling policy.
     * @param archivesDirectory the archives directory
     * @param archiveBaseName   the archive base name
     * @param maxFileSize       the max file size
     * @param maxHistory        the max history
     * @param appender          the appender
     * @return the size and time based rolling policy
     */
    public SizeAndTimeBasedRollingPolicy<ILoggingEvent> newRollingPolicy(final Path archivesDirectory, final String archiveBaseName, final FileSize maxFileSize, final int maxHistory, final FileAppender<ILoggingEvent> appender) {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        final SizeAndTimeBasedRollingPolicy<ILoggingEvent> result = new SizeAndTimeBasedRollingPolicy<>();
        result.setContext(context);
        result.setFileNamePattern(archivesDirectory.resolve(archiveBaseName + ROLLING_SUFFIX).toAbsolutePath().toString());
        result.setMaxFileSize(maxFileSize);
        result.setMaxHistory(maxHistory);

        if (maxHistory > 0) {
            result.setTotalSizeCap(FileSize.valueOf(String.valueOf(maxFileSize.getSize() * maxHistory)));
        } else {
            LOGGER.warn("No max history specified");
        }

        result.setParent(appender);
        result.start();

        return result;
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
