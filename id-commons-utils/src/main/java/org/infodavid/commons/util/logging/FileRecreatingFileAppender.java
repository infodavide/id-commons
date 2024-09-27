package org.infodavid.commons.util.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ch.qos.logback.core.FileAppender;

/**
 * The Class FileRecreatingFileAppender.
 * @param <E> the element type
 */
public class FileRecreatingFileAppender<E> extends FileAppender<E> {

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