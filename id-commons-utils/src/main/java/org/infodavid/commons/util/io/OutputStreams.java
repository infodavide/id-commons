package org.infodavid.commons.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The Class OutputStreams.
 */
@NoArgsConstructor
public class OutputStreams extends OutputStream {

    /** The streams. */
    @Getter
    private final List<OutputStream> streams = new ArrayList<>();

    /**
     * Instantiates a new output streams.
     * @param streams the streams
     */
    @SuppressWarnings("resource")
    public OutputStreams(final OutputStream... streams) {
        java.util.Objects.requireNonNull(streams);

        for (final OutputStream stream : streams) {
            java.util.Objects.requireNonNull(stream);
            this.streams.add(stream);
        }
    }

    /*
     * (non-javadoc)
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {
        for (final OutputStream os : streams) {
            os.close();
        }
    }

    /*
     * (non-javadoc)
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        for (final OutputStream os : streams) {
            os.flush();
        }
    }

    /*
     * (non-javadoc)
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(final byte[] b) throws IOException {
        for (final OutputStream os : streams) {
            os.write(b);
        }
    }

    /*
     * (non-javadoc)
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        for (final OutputStream os : streams) {
            os.write(b, off, len);
        }
    }

    /*
     * (non-javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException {
        for (final OutputStream os : streams) {
            os.write(b);
        }
    }

}
