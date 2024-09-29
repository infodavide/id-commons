package org.infodavid.commons.util.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The Class DirectorySizeVisitor.
 */
class DirectorySizeVisitor extends SimpleFileVisitor<Path> {

    /** The listener. */
    private FileProcessingListener listener = null;

    /** The recursive. */
    private boolean recursive;

    /** The root path. */
    private Path rootPath;

    /** The size. */
    private final AtomicLong size = new AtomicLong(0);

    /**
     * Instantiates a new visitor.
     * @param listener the listener
     */
    public DirectorySizeVisitor(final FileProcessingListener listener) {
        this.listener = listener;
    }

    /**
     * Instantiates a new visitor.
     * @param rootPath  the root path
     * @param recursive the recursive
     */
    public DirectorySizeVisitor(final Path rootPath, final boolean recursive) {
        this.rootPath = rootPath;
        this.recursive = recursive;
    }

    /**
     * Gets the size.
     * @return the size
     */
    public long getSize() {
        return size.longValue();
    }

    /*
     * (non-javadoc)
     * @see java.nio.file.SimpleFileVisitor#preVisitDirectory(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
     */
    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(attrs);

        if (recursive || dir.equals(rootPath)) {
            return FileVisitResult.CONTINUE;
        }

        return FileVisitResult.SKIP_SUBTREE;
    }

    /*
     * (non-javadoc)
     * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
     */
    @Override
    public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) throws IOException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(attrs);
        size.addAndGet(attrs.size());

        if (listener != null) {
            listener.processed(path, FileProcessingListener.PROCESSED);
        }

        return FileVisitResult.CONTINUE;
    }

    /*
     * (non-javadoc)
     * @see java.nio.file.SimpleFileVisitor#visitFileFailed(java.lang.Object, java.io.IOException)
     */
    @Override
    public FileVisitResult visitFileFailed(final Path path, final IOException e) throws IOException {
        Objects.requireNonNull(path);

        if (listener == null) {
            throw e;
        }

        listener.failed(path, e);

        return FileVisitResult.CONTINUE;
    }
}
