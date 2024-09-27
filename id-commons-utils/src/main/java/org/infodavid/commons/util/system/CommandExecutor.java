package org.infodavid.commons.util.system;

import java.nio.file.Path;

/**
 * The Interface CommandExecutor.
 */
public interface CommandExecutor {

    /**
     * Execute.
     * @param command the command
     * @return the exit code
     */
    int executeCommand(String... command);

    /**
     * Execute.
     * @param output     the output
     * @param error      the error
     * @param workingDir the working directory
     * @param command    the command line
     * @return the exit code
     */
    int executeCommand(StringBuilder output, StringBuilder error, Path workingDir, String[] command);

    /**
     * Execute.
     * @param output  the output
     * @param error   the error
     * @param command the command line
     * @return the exit code
     */
    int executeCommand(StringBuilder output, StringBuilder error, String[] command);
}
