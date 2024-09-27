package org.infodavid.commons.util.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The Class FileProcessingAdapter.
 */
public class FileProcessingAdapter implements FileProcessingListener {

    /*
     * (non-javadoc)
     * @see org.infodavid.util.io.FileProcessingListener#processed(java.nio.file.Path, byte)
     */
    @Override
    public void processed(final Path path, final byte action) {
        Objects.requireNonNull(path);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.io.FileProcessingListener#failed(java.nio.file.Path, java.io.IOException)
     */
    @Override
    public void failed(final Path path, final IOException e) {
        Objects.requireNonNull(path);
    }
}
