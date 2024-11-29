package org.infodavid.commons.rest.security.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

/**
 * The Class ByteArrayServletInputStream.
 */
class ByteArrayServletInputStream extends ServletInputStream {

    /** The data. */
    private final ByteArrayInputStream delegate;

    /** The last index. */
    private int lastIndex = -1;

    /** The length. */
    private final int length;

    /** The listener. */
    private ReadListener listener = null;

    /**
     * Instantiates a new byte array servlet input stream.
     * @param data the data
     */
    ByteArrayServletInputStream(final byte[] data) {
        delegate = new ByteArrayInputStream(data);
        length = data.length;
    }

    /*
     * (non-Javadoc)
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    /*
     * (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        lastIndex = -1;
        delegate.close();
        super.close();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletInputStream#isFinished()
     */
    @Override
    public boolean isFinished() {
        return lastIndex >= length - 1;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletInputStream#isReady()
     */
    @Override
    public boolean isReady() {
        return isFinished();
    }

    /*
     * (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        if (isFinished()) {
            return -1;
        }

        lastIndex++;
        final int result = delegate.read();

        if (isFinished() && listener != null) {
            try {
                listener.onAllDataRead();
            } catch (final IOException e) {
                listener.onError(e);

                throw e;
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void reset() throws IOException {
        lastIndex = -1;
        delegate.reset();
        super.reset();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletInputStream#setReadListener(javax.servlet.ReadListener)
     */
    @Override
    public void setReadListener(final ReadListener readListener) {
        listener = readListener;

        try {
            if (isFinished()) {
                readListener.onAllDataRead();
            } else {
                readListener.onDataAvailable();
            }
        } catch (final IOException e) {
            readListener.onError(e);
        }
    }
}
