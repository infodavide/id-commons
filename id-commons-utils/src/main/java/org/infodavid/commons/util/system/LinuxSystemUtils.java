package org.infodavid.commons.util.system;

import static org.apache.commons.lang3.ArrayUtils.add;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class LinuxSystemUtils.
 */
@Slf4j
final class LinuxSystemUtils extends SystemUtils {

    /** The Constant BIN_SUDO. */
    private static final String BIN_SUDO = "sudo";

    /** The Constant BIN_TIMEDATECTL. */
    private static final String BIN_TIMEDATECTL = "timedatectl";

    /** The Constant REBOOT_COMMAND. Note: Full path of sudo command is not always the same. */
    private static final String[] REBOOT_COMMAND = { BIN_SUDO, "shutdown", "-r", "now" };

    /** The Constant SHUTDOWN_COMMAND. Note: Full path of sudo command is not always the same. */
    private static final String[] SHUTDOWN_COMMAND = { BIN_SUDO, "shutdown", "-h", "now" };

    /** The Constant DATE_SET_COMMAND. Note: Full path of sudo command is not always the same. */
    private static final String[] DATE_SET_COMMAND = { BIN_SUDO, "date", "-s" };

    /** The Constant TIMEDATECTL_SET_TZ_COMMAND. Note: Full path of sudo command is not always the same. */
    private static final String[] TIMEDATECTL_SET_TZ_COMMAND = { BIN_SUDO, BIN_TIMEDATECTL, "set-timezone" };

    /** The Constant TIMEDATECTL_SHOW_COMMAND. */
    private static final String[] TIMEDATECTL_SHOW_COMMAND = { BIN_TIMEDATECTL, "show" };

    /*
     * (non-javadoc)
     * @see org.infodavid.util.system.SystemUtils#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.system.SystemUtils#getTimeZone()
     */
    @Override
    public synchronized String getTimeZone() {
        final StringBuilder output = new StringBuilder();
        final StringBuilder error = new StringBuilder();

        if (execute(output, error, TIMEDATECTL_SHOW_COMMAND) == 0) {
            for (final String line : output.toString().split("\n")) {
                if (StringUtils.startsWithIgnoreCase(line, "Timezone")) {
                    return StringUtils.substringAfter(line, "=");
                }
            }
        }

        return super.getTimeZone();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.system.SystemUtils#reboot()
     */
    @Override
    public synchronized int reboot() {
        return execute(REBOOT_COMMAND);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.system.SystemUtils#setDateTime(java.time.ZonedDateTime)
     */
    @Override
    public synchronized void setDateTime(final ZonedDateTime date) throws IOException {
        if (date == null) {
            throw new IllegalArgumentException("No date specified");
        }

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String value = formatter.format(date);
        LOGGER.info("Setting date and time to: {}", value);

        if (execute(add(DATE_SET_COMMAND, value)) != 0) {
            throw new IOException("Error in command when setting date and time");
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.system.SystemUtils#setTimeZone(java.lang.String)
     */
    @Override
    public synchronized void setTimeZone(final String value) {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("No value specified");
        }

        LOGGER.info("Setting time zone to: {}", value);

        if (execute(add(TIMEDATECTL_SET_TZ_COMMAND, value)) != 0) {
            super.setTimeZone(value);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.system.SystemUtils#shutdown()
     */
    @Override
    public synchronized int shutdown() {
        return execute(SHUTDOWN_COMMAND);
    }
}
