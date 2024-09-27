package org.infodavid.commons.jdk;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.util.NumberUtils;
import org.infodavid.commons.util.system.CommandExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * The Class DiagnosticUtils.
 */
@SuppressWarnings("static-method")
@JsonIgnoreType
public final class DiagnosticUtils {

    /** The singleton. */
    private static WeakReference<DiagnosticUtils> instance = null;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticUtils.class);

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized DiagnosticUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new DiagnosticUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new utilities.
     */
    private DiagnosticUtils() {
    }

    /**
     * Builds the head dump.
     * @param path the path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void buildHeadDump(final String path) throws IOException {
        final File executable = DiagnosticUtils.getInstance().getJmapExecutable();

        if (!executable.exists()) {
            throw new UnsupportedOperationException("Executable not found: " + executable.getName());
        }

        final String pid = StringUtils.substringBefore(ManagementFactory.getRuntimeMXBean().getName(), "@");
        final File dump;

        if (StringUtils.isEmpty(path)) {
            dump = File.createTempFile("dump_" + pid + '_', ".hprof");
        } else {
            dump = new File(path);
        }

        final StringBuilder output = new StringBuilder();
        final StringBuilder error = new StringBuilder();

        try {
            if (CommandExecutorFactory.getInstance().executeCommand(output, error, new String[] { executable.getAbsolutePath(), "-dump:live,format=b,file=" + dump.getAbsolutePath(), pid }) != 0 && StringUtils.isNotEmpty(error.toString())) {
                throw new IOException(error.toString());
            }
        } catch (final IOException e) {
            FileUtils.deleteQuietly(dump);

            throw e;
        }
    }

    /**
     * Collect diagnostics.
     * @param buffer the buffer
     */
    public void collectDiagnostics(final StringBuilder buffer) {
        buffer.append("Alive threads:\n");

        for (final ThreadEntry entry : getActiveLiveThreads(15).values()) {
            buffer.append('\t');
            buffer.append(entry.name());
            buffer.append(", interrupted=");
            buffer.append(entry.interrupted());
            buffer.append(", daemon=");
            buffer.append(entry.daemon());
            buffer.append(", state=");
            buffer.append(entry.state());
            buffer.append('\n');

            for (final String item : entry.stackTrace()) {
                buffer.append('\t');
                buffer.append('\t');
                buffer.append(item);
                buffer.append('\n');
            }
        }

        buffer.append("Loaded objects:\n");

        for (final JMapEntry entry : getJavaHeapHistogram("org\\.infodavid\\..*", null).values()) {
            buffer.append('\t');
            buffer.append(entry.className());
            buffer.append(':');
            buffer.append(entry.instances());
            buffer.append(':');
            buffer.append(NumberUtils.getInstance().toHumanReadableByteCount(entry.bytes(), true));
            buffer.append('\n');
        }
    }

    /**
     * This method guarantees that garbage collection is done unlike <code>{@link System#gc()}</code>.
     * @param reference the reference
     * @throws InterruptedException the interrupted exception
     */
    public void gc(final WeakReference<?> reference) throws InterruptedException {
        WeakReference<?> ref = reference;
        final long timeout = System.currentTimeMillis() + 5000;

        if (ref == null) {
            Object obj = new Object();
            ref = new WeakReference<>(obj);
            obj = null; // NOSONAR
        }

        while (ref.get() != null && System.currentTimeMillis() < timeout) {
            System.gc(); // NOSONAR
            Thread.sleep(100); // NOSONAR Only for testing
        }
    }

    /**
     * This method guarantees that garbage collection is done unlike <code>{@link System#gc()}</code>.
     * @throws InterruptedException the interrupted exception
     */
    public void gc() throws InterruptedException {
        gc(null);
    }

    /**
     * Collect active thread.
     * @param stackLength the length of the stack trace
     * @return the map
     */
    public Map<String, ThreadEntry> getActiveLiveThreads(final int stackLength) {
        final Map<String, ThreadEntry> results = new HashMap<>();

        for (final Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            final Thread thread = entry.getKey();

            if (!thread.isAlive() || thread.getThreadGroup() != null && "system".equalsIgnoreCase(thread.getThreadGroup().getName()) || thread.getName().startsWith("nio") || thread.getName().startsWith("pool") || thread.getName().startsWith("http-") || thread == Thread.currentThread()) {
                continue;
            }

            final String name;

            if (thread.getThreadGroup() == null) {
                name = thread.getName();
            } else {
                name = thread.getThreadGroup().getName() + '.' + thread.getName();
            }

            final StackTraceElement[] elements = entry.getValue();
            final int length = stackLength < 1 ? elements.length : Math.min(stackLength, elements.length);
            final String[] trace = new String[length];

            for (int i = 0; i < length; i++) {
                final StackTraceElement element = elements[i];

                trace[i] = element.getClassName() + '.' + element.getMethodName();
            }

            final ThreadEntry threadEntry = new ThreadEntry(name, thread.getState(), thread.isDaemon(), thread.isInterrupted(), trace);
            results.put(threadEntry.name(), threadEntry);
        }

        return results;
    }

    /**
     * Gets the java heap histogram.
     * @param includePattern the include regular expression
     * @param excludePattern the exclude regular expression
     * @return the java heap histogram
     */
    public Map<String, JMapEntry> getJavaHeapHistogram(final String includePattern, final String excludePattern) {
        final JMapCallable callable = new JMapCallable();
        callable.setIncludePattern(includePattern);
        callable.setExcludePattern(excludePattern);

        try {
            return callable.call();
        } catch (final Exception e) {
            LOGGER.warn("An error occured while collection Java heap histogram", e);

            return Collections.emptyMap();
        }
    }

    /**
     * Gets the jmap executable.
     * @return the jmap executable
     */
    protected File getJmapExecutable() {
        final File binDirectory = new File(org.apache.commons.lang3.SystemUtils.getJavaHome(), "bin");
        final String executableName;

        if (org.apache.commons.lang3.SystemUtils.IS_OS_UNIX || org.apache.commons.lang3.SystemUtils.IS_OS_LINUX) {
            executableName = "jmap";
        } else if (org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS) {
            executableName = "jmap.exe";
        } else {
            throw new UnsupportedOperationException("Operative system not supported: " + org.apache.commons.lang3.SystemUtils.OS_NAME);
        }

        File result = new File(binDirectory, executableName);

        if (!result.exists()) {
            result = new File(new File(org.apache.commons.lang3.SystemUtils.getJavaHome().getParentFile(), "bin"), executableName);
        }

        return result;
    }
}
