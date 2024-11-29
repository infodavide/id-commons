package org.infodavid.commons.util.system;

import java.nio.file.Path;
import java.util.Map;

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
     * @param env     the environment variables
     * @param command the command
     * @return the exit code
     */
    int executeCommand(Map<String, String> env, String... command);

    /**
     * Execute.
     * @param output     the output
     * @param error      the error
     * @param workingDir the working directory
     * @param env        the environment variables
     * @param command    the command line
     * @return the exit code
     */
    int executeCommand(StringBuilder output, StringBuilder error, Path workingDir, Map<String, String> env, String[] command);
}
