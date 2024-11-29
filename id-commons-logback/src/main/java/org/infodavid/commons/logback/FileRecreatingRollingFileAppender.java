package org.infodavid.commons.logback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ch.qos.logback.core.rolling.RollingFileAppender;

/**
 * The Class FileRecreatingRollingFileAppender.
 * @param <E> the element type
 */
public class FileRecreatingRollingFileAppender<E> extends RollingFileAppender<E> { // NOSONAR Inheritance depth

    /*
     * (non-javadoc)
     * @see ch.qos.logback.core.FileAppender#writeOut(java.lang.Object)
     */
    @Override
    protected void writeOut(final E event) throws IOException {
        if (!Files.exists(Paths.get(getFile()))) {
            super.openFile(getFile()); // recreates the file
        }

        super.writeOut(event);
    }
}