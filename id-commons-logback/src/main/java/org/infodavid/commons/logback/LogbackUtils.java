package org.infodavid.commons.logback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class LogbackUtils.
 */
@UtilityClass
@Slf4j
public final class LogbackUtils {

    /** The encoder pattern. */
    public static final String ENCODER_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

    /** The Constant ROLLING_SUFFIX. */
    public static final String ROLLING_SUFFIX = "-%d{yyyy-MM-dd}.%i.log.gz";

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
            if (appender instanceof final SiftingAppender siftingAppender) {
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
            if (appender instanceof final AsyncAppender asyncAppender) {
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

        if (maxFileSize == null || maxFileSize.getSize() <= 0) {
            LOGGER.warn("No max file size specified");
        } else {
            result.setMaxFileSize(maxFileSize);
        }

        if (maxHistory <= 0) {
            LOGGER.warn("No max history specified");
        } else {
            result.setMaxHistory(maxHistory);
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
    public Level toLevel(final ch.qos.logback.classic.Level value) {
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
        if (ch.qos.logback.classic.Level.TRACE.equals(value)) {
            return Level.FINEST;
        }

        if (ch.qos.logback.classic.Level.DEBUG.equals(value)) {
            return Level.FINE;
        }

        if (ch.qos.logback.classic.Level.INFO.equals(value)) {
            return Level.INFO;
        }

        if (ch.qos.logback.classic.Level.WARN.equals(value)) {
            return Level.WARNING;
        }

        if (ch.qos.logback.classic.Level.ERROR.equals(value)) {
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
    public ch.qos.logback.classic.Level toSlf4JLevel(final Level value) {
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
            return ch.qos.logback.classic.Level.TRACE;
        }

        if (Level.FINER.equals(value) || Level.FINE.equals(value)) {
            return ch.qos.logback.classic.Level.DEBUG;
        }

        if (Level.CONFIG.equals(value) || Level.INFO.equals(value)) {
            return ch.qos.logback.classic.Level.INFO;
        }

        if (Level.WARNING.equals(value)) {
            return ch.qos.logback.classic.Level.WARN;
        }

        if (Level.SEVERE.equals(value) || Level.OFF.equals(value)) {
            return ch.qos.logback.classic.Level.ERROR;
        }

        return null;
    }

    /**
     * Map level.
     * @param value the value
     * @return the string
     */
    public String toString(final ch.qos.logback.classic.Level value) {
        if (value == null) {
            return null;
        }

        // [ALL -> TRACE]
        // DEBUG -> DEBUG
        // INFO -> INFO
        // WARN -> WARN
        // SEVERE -> ERROR
        // [OFF -> ERROR]
        if (ch.qos.logback.classic.Level.ALL.equals(value)) {
            return "TRACE";
        }

        if (ch.qos.logback.classic.Level.DEBUG.equals(value) ) {
            return "DEBUG";
        }

        if (ch.qos.logback.classic.Level.INFO.equals(value)) {
            return "INFO";
        }

        if (ch.qos.logback.classic.Level.WARN.equals(value)) {
            return "WARN";
        }

        if (ch.qos.logback.classic.Level.ERROR.equals(value) || ch.qos.logback.classic.Level.OFF.equals(value)) {
            return "ERROR";
        }

        return value.levelStr;
    }
}