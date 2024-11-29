package org.infodavid.commons.util.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.util.exception.LambdaRuntimeException;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class CompressionUtils.
 */
@JsonIgnoreType
@UtilityClass
@Slf4j
public final class CompressionUtils {

    /** The Constant ZIP. */
    private static final String ZIP = "zip";

    /**
     * Compress.
     * @param path          the path
     * @param type          the type
     * @param out           the out
     * @param level         the level
     * @param excluded      the excluded
     * @param includeHidden the include hidden
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void compress(final Path path, final String type, final OutputStream out, final byte level, final Collection<String> excluded, final boolean includeHidden) throws IOException {
        compress(path, type, out, level, excluded, includeHidden, new FileProcessingAdapter());
    }

    /**
     * Compress.
     * @param path          the path
     * @param type          the type
     * @param out           the out
     * @param level         the level
     * @param excluded      the excluded
     * @param includeHidden the include hidden
     * @param listener      the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void compress(final Path path, final String type, final OutputStream out, final byte level, final Collection<String> excluded, final boolean includeHidden, final FileProcessingListener listener) throws IOException {
        if (ZIP.equalsIgnoreCase(type)) {
            CompressionUtils.zip(path, out, level, excluded, includeHidden, listener);

            return;
        }

        final ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();

        if (Files.isRegularFile(path)) {
            try (ArchiveOutputStream aos = archiveStreamFactory.createArchiveOutputStream(type, out)) {
                final ArchiveEntry entry = aos.createArchiveEntry(path, path.getFileName().toString());

                try {
                    aos.putArchiveEntry(entry);
                    Files.copy(path, aos);
                    aos.closeArchiveEntry();
                } catch (final IOException e) {
                    if (listener != null) {
                        listener.failed(path, e);
                    }

                    throw new LambdaRuntimeException(e);
                }

                if (listener != null) {
                    listener.processed(path, FileProcessingListener.COMPRESSED);
                }
            } catch (final ArchiveException e) {
                throw (IOException) e.getCause();
            }
        }

        final Predicate<Path> filter = new FilterPredicate(Collections.emptyList(), excluded, includeHidden);

        try (ArchiveOutputStream aos = archiveStreamFactory.createArchiveOutputStream(type, out)) {
            PathUtils.walk(path, filter, p -> {
                if (path.equals(p)) {
                    return;
                }

                try {
                    final ArchiveEntry entry = aos.createArchiveEntry(path, path.relativize(p).toString());
                    aos.putArchiveEntry(entry);

                    if (Files.isRegularFile(p)) {
                        Files.copy(p, aos);
                    }

                    aos.closeArchiveEntry();
                } catch (final IOException e) {
                    if (listener != null) {
                        try {
                            listener.failed(p, e);
                        } catch (final IOException e1) {
                            throw new LambdaRuntimeException(e1);
                        }
                    }

                    throw new LambdaRuntimeException(e);
                }

                if (listener != null) {
                    listener.processed(p, FileProcessingListener.COMPRESSED);
                }
            });
        } catch (final ArchiveException | LambdaRuntimeException e) {
            throw (IOException) e.getCause();
        }
    }

    /**
     * Compress.
     * @param path          the path
     * @param type          the type
     * @param out           the out
     * @param excluded      the excluded
     * @param includeHidden the include hidden
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void compress(final Path path, final String type, final OutputStream out, final Collection<String> excluded, final boolean includeHidden) throws IOException {
        compress(path, type, out, (byte) 1, excluded, includeHidden, new FileProcessingAdapter());
    }

    /**
     * Compress.
     * @param path          the path
     * @param type          the type
     * @param file          the file
     * @param level         the level
     * @param excluded      the excluded
     * @param includeHidden the include hidden
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void compress(final Path path, final String type, final Path file, final byte level, final Collection<String> excluded, final boolean includeHidden) throws IOException {
        compress(path, type, file, level, excluded, includeHidden, new FileProcessingAdapter());
    }

    /**
     * Compress.
     * @param path          the path
     * @param type          the type
     * @param file          the file
     * @param level         the level
     * @param excluded      the excluded
     * @param includeHidden the include hidden
     * @param listener      the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void compress(final Path path, final String type, final Path file, final byte level, final Collection<String> excluded, final boolean includeHidden, final FileProcessingListener listener) throws IOException {
        PathUtils.assertReadable(file);

        try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
            compress(path, type, out, level, excluded, includeHidden, listener);
        }
    }

    /**
     * Compress.
     * @param path          the path
     * @param type          the type
     * @param file          the file
     * @param excluded      the excluded
     * @param includeHidden the include hidden
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void compress(final Path path, final String type, final Path file, final Collection<String> excluded, final boolean includeHidden) throws IOException {
        compress(path, type, file, (byte) 1, excluded, includeHidden);
    }

    /**
     * Extract.
     * @param in       the in
     * @param path     the path
     * @param excluded the excluded
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void extract(final InputStream in, final Path path, final Collection<String> excluded) throws IOException {
        extract(in, path, excluded, new FileProcessingAdapter());
    }

    /**
     * Extract.
     * @param in       the in
     * @param dir      the directory
     * @param excluded the excluded
     * @param listener the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void extract(final InputStream in, final Path dir, final Collection<String> excluded, final FileProcessingListener listener) throws IOException {
        final Predicate<Path> filter = new FilterPredicate(Collections.emptyList(), excluded, true);
        final ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();

        try (BufferedInputStream bis = new BufferedInputStream(in); ArchiveInputStream ais = archiveStreamFactory.createArchiveInputStream(bis)) {
            ArchiveEntry entry;

            while ((entry = ais.getNextEntry()) != null) {
                final Path path = dir.resolve(entry.getName());

                if (!filter.test(path)) {
                    continue;
                }

                final Path parent = path.getParent();

                if (!Files.exists(parent)) { // NOSONAR NIO API
                    Files.createDirectories(parent);
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(path);
                } else {
                    Files.copy(ais, path);
                }

                if (listener != null) {
                    listener.processed(path, FileProcessingListener.EXTRACTED);
                }
            }
        } catch (final ArchiveException e) {
            throw new IOException(e);
        }
    }

    /**
     * Extract.
     * @param file     the file
     * @param dir      the directory
     * @param excluded the excluded
     * @param listener the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void extract(final Path file, final Path dir, final Collection<String> excluded, final FileProcessingListener listener) throws IOException {
        PathUtils.assertReadable(file);
        PathUtils.assertFile(file);

        if (PathUtils.isValidZipFile(file)) {
            CompressionUtils.unzip(file, dir, excluded, listener);

            return;
        }

        try (final InputStream in = Files.newInputStream(file)) {
            extract(in, dir, excluded, listener);
        }
    }

    /**
     * Unzip.
     * @param in       the in
     * @param path     the path
     * @param excluded the excluded
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void unzip(final InputStream in, final Path path, final Collection<String> excluded) throws IOException {
        unzip(in, path, excluded, new FileProcessingAdapter());
    }

    /**
     * Unzip.
     * @param in       the input stream
     * @param dir      the directory
     * @param excluded the set of excluded files or directories
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void unzip(final InputStream in, final Path dir, final Collection<String> excluded, final FileProcessingListener listener) throws IOException {
        final Predicate<Path> filter = new FilterPredicate(Collections.emptyList(), excluded, true);

        try (final ZipInputStream zis = new ZipInputStream(new ValidatedZipInputStream(in))) {
            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {
                final Path path = dir.resolve(entry.getName()).toAbsolutePath();

                if (!filter.test(path)) {
                    continue;
                }

                final Path parent = path.getParent();

                if (!Files.exists(parent)) { // NOSONAR NIO API
                    Files.createDirectories(parent);
                }

                if (Files.exists(parent) && Files.isWritable(parent)) { // NOSONAR NIO API
                    if (entry.isDirectory()) {
                        Files.createDirectories(path);
                    } else {
                        Files.copy(zis, path);
                    }
                }

                zis.closeEntry();

                if (listener != null) {
                    listener.processed(path, FileProcessingListener.EXTRACTED);
                }

                entry = zis.getNextEntry();
            }
        }
    }

    /**
     * Unzip.
     * @param file     the file
     * @param dir      the directory
     * @param excluded the set of excluded files or directories
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void unzip(final Path file, final Path dir, final Collection<String> excluded, final FileProcessingListener listener) throws IOException {
        PathUtils.assertReadable(file);
        PathUtils.assertFile(file);

        if (!PathUtils.isValidZipFile(file)) {
            throw new IOException(file.toString() + " is not a valid ZIP file.");
        }

        try (final InputStream in = Files.newInputStream(file)) {
            unzip(in, dir, excluded, listener);
        }
    }

    /**
     * Zip.
     * @param path            the directory or file
     * @param zos             the ZIP output stream
     * @param parentPathInZip the parent path in ZIP file
     * @param level           the level from 0 (store) to 9
     * @param excluded        the set of excluded files or directories
     * @param includeHidden   true to include hidden directories and files
     * @param listener        the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void write(final Path path, final ZipOutputStream zos, final String parentPathInZip, final byte level, final Collection<String> excluded, final boolean includeHidden, final FileProcessingListener listener) throws IOException {
        PathUtils.assertReadable(path);

        if (Files.isRegularFile(path)) { // NOSONAR NIO API
            final ZipEntry entry;

            if (StringUtils.isEmpty(parentPathInZip)) {
                entry = new ZipEntry(path.getFileName().toString());
            } else {
                entry = new ZipEntry(parentPathInZip + '/' + path.getFileName().toString());
            }

            if (level <= 0) {
                entry.setMethod(ZipEntry.STORED);
            } else {
                entry.setMethod(ZipEntry.DEFLATED);
            }

            try {
                entry.setSize(Files.size(path));
                zos.putNextEntry(entry);
                Files.copy(path, zos);
                zos.closeEntry();
            } catch (final IOException e) {
                if (listener != null) {
                    listener.failed(path, e);
                }

                throw new LambdaRuntimeException(e);
            }

            if (listener != null) {
                listener.processed(path, FileProcessingListener.COMPRESSED);
            }

            return;
        }

        final Predicate<Path> filter = new FilterPredicate(Collections.emptyList(), excluded, includeHidden);
        PathUtils.walk(path, filter, p -> {
            if (path.equals(p)) {
                return;
            }

            final ZipEntry entry;

            if (StringUtils.isEmpty(parentPathInZip)) {
                entry = new ZipEntry(path.relativize(p).toString());
            } else {
                entry = new ZipEntry(parentPathInZip + '/' + path.relativize(p).toString());
            }

            if (level <= 0) {
                entry.setMethod(ZipEntry.STORED);
            } else {
                entry.setMethod(ZipEntry.DEFLATED);
            }

            try {
                if (Files.isRegularFile(p)) { // NOSONAR NIO API
                    entry.setSize(Files.size(p));
                    zos.putNextEntry(entry);
                    Files.copy(p, zos);
                    zos.closeEntry();
                }
            } catch (final IOException e) {
                if (listener != null) {
                    try {
                        listener.failed(p, e);
                    } catch (final IOException e1) {
                        throw new LambdaRuntimeException(e1);
                    }
                }

                throw new LambdaRuntimeException(e);
            }

            if (listener != null) {
                listener.processed(p, FileProcessingListener.COMPRESSED);
            }
        });
    }

    /**
     * Zip.
     * @param path          the directory or file
     * @param out           the output stream
     * @param level         the level from 0 (store) to 9
     * @param excluded      the set of excluded files or directories
     * @param includeHidden true to include hidden directories and files
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void zip(final Path path, final OutputStream out, final byte level, final Collection<String> excluded, final boolean includeHidden) throws IOException {
        zip(path, out, level, excluded, includeHidden, new FileProcessingAdapter());
    }

    /**
     * Zip.
     * @param path          the directory or file
     * @param out           the output stream
     * @param level         the level from 0 (store) to 9
     * @param excluded      the set of excluded files or directories
     * @param includeHidden true to include hidden directories and files
     * @param listener      the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void zip(final Path path, final OutputStream out, final byte level, final Collection<String> excluded, final boolean includeHidden, final FileProcessingListener listener) throws IOException {
        PathUtils.assertReadable(path);

        if (Files.isRegularFile(path)) {
            try (ZipOutputStream zos = new ZipOutputStream(out)) {
                zos.setLevel(level);
                final ZipEntry entry = new ZipEntry(path.getFileName().toString());

                if (level <= 0) {
                    entry.setMethod(ZipEntry.STORED);
                } else {
                    entry.setMethod(ZipEntry.DEFLATED);
                }

                try {
                    entry.setSize(Files.size(path));
                    zos.putNextEntry(entry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (final IOException e) {
                    if (listener != null) {
                        listener.failed(path, e);
                    }

                    throw new LambdaRuntimeException(e);
                }

                if (listener != null) {
                    listener.processed(path, FileProcessingListener.COMPRESSED);
                }
            } catch (final LambdaRuntimeException e) {
                throw (IOException) e.getCause();
            }

            return;
        }

        final Predicate<Path> filter = new FilterPredicate(Collections.emptyList(), excluded, includeHidden);

        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            zos.setLevel(level);
            PathUtils.walk(path, filter, p -> {
                if (path.equals(p)) {
                    return;
                }

                final ZipEntry entry = new ZipEntry(path.relativize(p).toString());

                if (level <= 0) {
                    entry.setMethod(ZipEntry.STORED);
                } else {
                    entry.setMethod(ZipEntry.DEFLATED);
                }

                try {
                    if (Files.isRegularFile(p)) {
                        entry.setSize(Files.size(p));
                        zos.putNextEntry(entry);
                        Files.copy(p, zos);
                        zos.closeEntry();
                    }
                } catch (final IOException e) {
                    if (listener != null) {
                        try {
                            listener.failed(p, e);
                        } catch (final IOException e1) {
                            throw new LambdaRuntimeException(e1);
                        }
                    }

                    throw new LambdaRuntimeException(e);
                }

                if (listener != null) {
                    listener.processed(p, FileProcessingListener.COMPRESSED);
                }
            });
        } catch (final LambdaRuntimeException e) {
            throw (IOException) e.getCause();
        }
    }

    /**
     * Zip.
     * @param path          the directory or file
     * @param out           the output stream
     * @param excluded      the set of excluded files or directories
     * @param includeHidden true to include hidden directories and files
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void zip(final Path path, final OutputStream out, final Collection<String> excluded, final boolean includeHidden) throws IOException {
        zip(path, out, (byte) 1, excluded, includeHidden, new FileProcessingAdapter());
    }

    /**
     * Zip.
     * @param path          the directory or file
     * @param file          the file
     * @param level         the level from 0 (store) to 9
     * @param excluded      the set of excluded files or directories
     * @param includeHidden true to include hidden directories and files
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void zip(final Path path, final Path file, final byte level, final Collection<String> excluded, final boolean includeHidden) throws IOException {
        zip(path, file, level, excluded, includeHidden, new FileProcessingAdapter());
    }

    /**
     * Zip.
     * @param path          the directory or file
     * @param file          the file
     * @param level         the level from 0 (store) to 9
     * @param excluded      the set of excluded files or directories
     * @param includeHidden true to include hidden directories and files
     * @param listener      the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void zip(final Path path, final Path file, final byte level, final Collection<String> excluded, final boolean includeHidden, final FileProcessingListener listener) throws IOException {
        try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
            zip(path, out, level, excluded, includeHidden, listener);
        }
    }

    /**
     * Zip.
     * @param path          the directory or file
     * @param file          the file
     * @param excluded      the set of excluded files or directories
     * @param includeHidden true to include hidden directories and files
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void zip(final Path path, final Path file, final Collection<String> excluded, final boolean includeHidden) throws IOException {
        zip(path, file, (byte) 1, excluded, includeHidden, new FileProcessingAdapter());
    }

    /**
     * Zip.
     * @param zis  the ZIP input stream
     * @param file the ZIP file where the content must be written
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void zip(final ZipInputStream zis, final Path file, final byte level) throws IOException {
        LOGGER.debug("Writing data to temporary file: {}", file);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(file, StandardOpenOption.TRUNCATE_EXISTING))) {
            zos.setLevel(level);
            final byte[] buffer = new byte[8192];
            ZipEntry currentEntry;

            while ((currentEntry = zis.getNextEntry()) != null) {
                final ZipEntry entry = (ZipEntry) currentEntry.clone();
                zos.putNextEntry(entry);
                int length;

                while ((length = zis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Written data: {}", PathUtils.getHumanReadableSize(Files.size(file), true));
        }
    }
}
