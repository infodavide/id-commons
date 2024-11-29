package org.infodavid.commons.util.system;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultCommandExecutor.
 */
@NoArgsConstructor
@Slf4j
final class DefaultCommandExecutor implements CommandExecutor {

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.util.system.CommandExecutor#executeCommand(java.util.Map, java.lang.String[])
     */
    @Override
    public int executeCommand(final Map<String, String> env, final String... command) {
        return executeCommand(new StringBuilder(), new StringBuilder(), null, env, command);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.util.system.CommandExecutor#executeCommand(java.lang.String[])
     */
    /*
     * (non-javadoc)
     * @see org.infodavid.util.system.CommandExecutor#executeCommand(java.lang.String)
     */
    @Override
    public int executeCommand(final String... command) {
        return executeCommand(new StringBuilder(), new StringBuilder(), null, Collections.emptyMap(), command);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.util.system.CommandExecutor#executeCommand(java.lang.StringBuilder, java.lang.StringBuilder, java.nio.file.Path, java.util.Map, java.lang.String[])
     */
    @Override
    public int executeCommand(final StringBuilder output, final StringBuilder error, final Path workingDir, final Map<String, String> env, final String[] command) {
        int code = -1;
        final CommandLine commandLine = new CommandLine(command[0]);

        if (command.length > 1) {
            for (int i = 1; i < command.length; i++) {
                commandLine.addArgument(command[i], false);
            }
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        final Executor executor = DefaultExecutor.builder().setExecuteStreamHandler(new PumpStreamHandler(out, err)).get();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing command: {}", commandLine);
        }

        if (workingDir != null) {
            executor.setWorkingDirectory(workingDir.toFile());
        }

        try {
            if (env == null || env.isEmpty()) {
                code = executor.execute(commandLine);
            } else {
                code = executor.execute(commandLine, env);
            }
        } catch (final Throwable e) { // NOSONAR
            LOGGER.warn("An error occured while executing command: {}, {}", StringUtils.join(command, ' '), e.getMessage()); // NOSONAR No format when using Throwable
        } finally {
            if (error != null) {
                error.append(err.toString().trim());
            }

            if (output != null) {
                output.append(out.toString().trim());
            }

            if (LOGGER.isDebugEnabled()) { // NOSONAR Null check to avoid warnings
                if (StringUtils.isNotEmpty(error) && error != null) { // NOSONAR Null check to avoid warnings
                    LOGGER.error(error.toString());
                }
                if (StringUtils.isNotEmpty(output) && output != null) { // NOSONAR Null check to avoid warnings
                    LOGGER.debug(output.toString());
                }
            }
        }

        if (code == 0) {
            LOGGER.debug("Command execution result:\n\tExit code: {}", String.valueOf(code)); // NOSONAR Always written
        } else if (StringUtils.isEmpty(error)) {
            LOGGER.warn("Exit code: {}", String.valueOf(code));// NOSONAR Always written

            return -1;
        } else if (StringUtils.isNotEmpty(output)) {
            LOGGER.warn("Exit code: {}, output: {}", String.valueOf(code), output); // NOSONAR Always written

            return -1;
        } else {
            LOGGER.warn("Exit code: {}, error: {}", String.valueOf(code), error); // NOSONAR Always written

            return -2;
        }

        return 0;
    }
}
