package org.infodavid.commons.test.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class TcpServer.
 */
public abstract class AbstractTcpComponent implements Closeable {

    /**
     * The Class HandlerImpl.
     */
    protected class HandlerImpl extends IoHandlerAdapter {

        /*
         * (non-javadoc)
         * @see org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(org.apache.mina.core.session.IoSession, java.lang.Throwable)
         */
        @Override
        public void exceptionCaught(final IoSession session, final Throwable cause) throws Exception {
            if (session == null || !session.isConnected()) {
                return;
            }

            getLogger().warn("Exception on session: " + session.getRemoteAddress(), cause);

            if (listener != null) {
                listener.handleException(cause);
            }
        }

        /*
         * (non-javadoc)
         * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.mina.core.session.IoSession, java.lang.Object)
         */
        @Override
        public void messageReceived(final IoSession session, final Object message) throws Exception {
            final byte[] data = toArray((IoBuffer) message);
            getLogger().info("Data received for session {}: {} ", session.getRemoteAddress(), new String(data, charset)); // NOSONAR Always written

            if (listener != null) {
                listener.onData(session, data);
            }
        }

        /*
         * (non-javadoc)
         * @see org.apache.mina.core.service.IoHandlerAdapter#messageSent(org.apache.mina.core.session.IoSession, java.lang.Object)
         */
        @Override
        public void messageSent(final IoSession session, final Object message) throws Exception {
            final byte[] data = toArray((IoBuffer) message);
            getLogger().info("Data sent for session {}: {} ", session.getRemoteAddress(), new String(data, charset)); // NOSONAR Always written

            if (listener != null) {
                listener.onResponse(session, data);
            }
        }

        /*
         * (non-javadoc)
         * @see org.apache.mina.core.service.IoHandlerAdapter#sessionClosed(org.apache.mina.core.session.IoSession)
         */
        @Override
        public void sessionClosed(final IoSession session) throws Exception {
            getLogger().info("Session closed: {}", session.getRemoteAddress());

            if (listener != null) {
                listener.onClose(session);
            }
        }

        /*
         * (non-javadoc)
         * @see org.apache.mina.core.service.IoHandlerAdapter#sessionIdle(org.apache.mina.core.session.IoSession, org.apache.mina.core.session.IdleStatus)
         */
        @SuppressWarnings("boxing")
        @Override
        public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
            getLogger().trace("Idle session: {}, {}", session.getRemoteAddress(), session.getIdleCount(status));
        }

        /*
         * (non-javadoc)
         * @see org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(org.apache.mina.core.session.IoSession)
         */
        @Override
        public void sessionOpened(final IoSession session) throws Exception {
            getLogger().info("Session opened: {}", session.getRemoteAddress());

            if (listener != null) {
                listener.onOpen(session);
            }
        }
    }

    /**
     * To array.
     * @param buffer the buffer
     * @return the byte[]
     */
    protected static byte[] toArray(final IoBuffer buffer) {
        final byte[] result = new byte[buffer.remaining()];
        System.arraycopy(buffer.array(), 0, result, 0, result.length);

        return result;
    }

    /** The charset. */
    @Getter
    @Setter
    protected Charset charset = StandardCharsets.ISO_8859_1;

    /** The handler. */
    protected final HandlerImpl handler = new HandlerImpl();

    /** The listener. */
    @Getter
    @Setter
    protected TcpListener listener;

    /** The output stream. */
    @Getter
    private OutputStream out = null;

    /** The port. */
    @Getter
    @Setter
    private int port;

    /**
     * Instantiates a new server.
     * @param port the port
     */
    protected AbstractTcpComponent(final int port) {
        this.port = port;
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected abstract Logger getLogger();

    /**
     * Gets the output stream.
     * @return the output stream
     */
    public synchronized OutputStream getOutputStream() {
        if (out == null) {
            out = new OutputStream() {
                @Override
                public void close() throws IOException {
                    AbstractTcpComponent.this.close();
                }

                @Override
                public void write(final byte[] b) throws IOException {
                    AbstractTcpComponent.this.write(b);
                }

                @Override
                public void write(final byte[] b, final int off, final int len) throws IOException {
                    AbstractTcpComponent.this.write(ArrayUtils.subarray(b, off, len));
                }

                @Override
                public void write(final int b) throws IOException {
                    AbstractTcpComponent.this.write(new byte[] { (byte) b });
                }
            };
        }

        return out;
    }

    /**
     * Checks if is active.
     * @return true, if is active
     */
    public abstract boolean isActive();

    /**
     * Send.
     * @param message the message
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract void send(final byte[] message) throws IOException;

    /**
     * Send.
     * @param message   the message
     * @param delimiter the delimiter
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void send(final byte[] message, final byte[] delimiter) throws IOException {
        send(ArrayUtils.addAll(message, delimiter));
    }

    /**
     * Send.
     * @param message the message
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void send(final String message) throws IOException {
        send(message.getBytes(charset));
    }

    /**
     * Send.
     * @param message   the message
     * @param delimiter the delimiter
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void send(final String message, final byte[] delimiter) throws IOException {
        send(ArrayUtils.addAll(message.getBytes(charset), delimiter));
    }

    /**
     * Write.
     * @param message the message
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void write(final byte[] message) throws IOException {
        send(message);
    }

    /**
     * Write.
     * @param message   the message
     * @param delimiter the delimiter
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void write(final byte[] message, final byte[] delimiter) throws IOException {
        send(message, delimiter);
    }

    /**
     * Write.
     * @param message the message
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void write(final String message) throws IOException {
        send(message.getBytes(charset));
    }

    /**
     * Write.
     * @param message   the message
     * @param delimiter the delimiter
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void write(final String message, final byte[] delimiter) throws IOException {
        send(message, delimiter);
    }
}
