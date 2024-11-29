package org.infodavid.commons.checksum;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.util.system.CommandExecutorFactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractChecksumGenerator.
 */
@Slf4j
public abstract class AbstractChecksumGenerator implements ChecksumGenerator {

    /** The Constant ALLOWED_CHARS. */
    private static final String ALLOWED_CHARS = "ABCDEFabcdef0123456789";

    /** True is command is supported. */
    @Getter
    private boolean commandSupported;

    /**
     * Instantiates a new abstract checksum generator.
     */
    protected AbstractChecksumGenerator() {
        final String command = getCommand();

        if (StringUtils.isEmpty(command)) {
            return;
        }

        final String[] commandLine = {
                command,
                "--help"
        };

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Checking if command exists on the system ({})", command);
        }

        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
            processBuilder.redirectErrorStream(true);
            final Process process = processBuilder.start();
            final int code = process.waitFor();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Return code: {}", String.valueOf(code));
            }

            commandSupported = code == 0;
        } catch (final InterruptedException e) {
            LOGGER.warn("Thread interrupted", e);
            Thread.currentThread().interrupt();
        } catch (final Exception e) {
            LOGGER.trace("An error occured while checking the presence of the command", e);
            commandSupported = false;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Command is supported: {}", String.valueOf(commandSupported)); // NOSONAR Always written
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.checksum.ChecksumGenerator#getChecksum(java.nio.file.Path)
     */
    @Override
    public String getChecksum(final Path file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Argument is null");
        }

        if (!Files.exists(file)) { // NOSONAR Using NIO API
            throw new NoSuchFileException("File not found: " + file.toString());
        }

        if (isCommandSupported()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Using command : {} {}", getCommand(), file.toAbsolutePath());
            }

            final StringBuilder output = new StringBuilder();
            final StringBuilder error = new StringBuilder();
            final int code = CommandExecutorFactory.getInstance().executeCommand(output, error, null, null, new String[] {
                    getCommand(),
                    file.toAbsolutePath().toString()
            });

            if (code != 0 && StringUtils.isNotEmpty(error.toString())) {
                throw new IOException(error.toString());
            }

            final int index = StringUtils.indexOfAny(output, ALLOWED_CHARS);

            if (index == -1) {
                return StringUtils.substringBefore(output.toString(), " ");
            }

            return StringUtils.substringBefore(output.substring(index), " ");
        }

        try (InputStream in = Files.newInputStream(file)) {
            return getChecksum(in);
        }
    }

    /**
     * Gets the checksum.
     * @param in the input
     * @return the checksum
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected abstract String getChecksum(InputStream in) throws IOException;

    /**
     * Gets the command.
     * @return the command
     */
    protected abstract String getCommand();
}
