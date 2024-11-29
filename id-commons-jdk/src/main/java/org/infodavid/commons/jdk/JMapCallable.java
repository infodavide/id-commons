package org.infodavid.commons.jdk;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.util.system.CommandExecutorFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class JMapCallable.
 */
@Setter
@Getter
class JMapCallable implements Callable<Map<String, JMapEntry>> {

    /** The exclude pattern. */
    private String excludePattern = null;

    /** The include pattern. */
    private String includePattern = null;

    /*
     * (non-javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Map<String, JMapEntry> call() throws Exception {
        final Map<String, JMapEntry> results = new HashMap<>();
        final File executable = DiagnosticUtils.getJmapExecutable();

        if (!executable.exists()) {
            throw new UnsupportedOperationException("Executable not found: " + executable.getName());
        }

        final String pid = StringUtils.substringBefore(ManagementFactory.getRuntimeMXBean().getName(), "@");
        final StringBuilder output = new StringBuilder();
        final StringBuilder error = new StringBuilder();

        if (CommandExecutorFactory.getInstance().executeCommand(output, error, null, null, new String[] {
                executable.getAbsolutePath(), "-histo:live", pid
        }) != 0 && StringUtils.isNotEmpty(error.toString())) {
            throw new IOException(error.toString());
        }

        new JMapParserRunnable(includePattern, excludePattern, output, results).run();

        return results;
    }
}
