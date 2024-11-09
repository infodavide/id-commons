package org.infodavid.commons.test.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class TcpClient.
 */
@Slf4j
public class TcpClient extends AbstractTcpComponent {

    /**
     * The main method.
     * @param args the arguments
     */
    @SuppressWarnings("resource")
    public static void main(final String[] args) {
        try (TcpClient c = new TcpClient(8080)) {
            c.setEchoResponseEnable(true);
            c.connect();
            final Scanner scanner = new Scanner(System.in);
            String line;

            do {
                System.out.println("Type the message and press enter to send it or press enter to stop the client......"); // NOSONAR For testing
                line = scanner.nextLine();

                if (!line.trim().isEmpty()) {
                    c.send(line);
                }
            } while (!line.trim().isEmpty());
        } catch (final Exception e) {
            e.printStackTrace();// NOSONAR For testing
        } finally {
            System.exit(0);
        }
    }

    /** The connection retry expiration in milliseconds. */
    @Getter
    @Setter
    private int connectionRetryExpiration = 2000;

    /** The connection retry interval in milliseconds. */
    @Getter
    @Setter
    private int connectionRetryInterval = 200;

    /** The echo response enable. */
    @Getter
    @Setter
    private boolean echoResponseEnable = false;

    /** The future. */
    private ConnectFuture future = null;

    /** The host name. */
    @Setter
    private String hostName = null;

    /**
     * Instantiates a new client mock.
     * @param port the port
     */
    public TcpClient(final int port) {
        super(port);
        hostName = "127.0.0.1";
    }

    /**
     * Instantiates a new client mock.
     * @param hostName the host name
     * @param port     the port
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public TcpClient(final String hostName, final int port) throws IOException {
        super(port);
        this.hostName = hostName;
    }

    /*
     * (non-javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (future == null) {
            return;
        }

        final IoSession session = future.getSession();

        if (session != null && session.isActive()) {
            session.closeNow();
            LOGGER.info("Session closed: {}", session.getRemoteAddress());
        }
    }

    /**
     * Connect.
     * @return the client
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public synchronized TcpClient connect() throws IOException {
        final NioSocketConnector connector = new NioSocketConnector();
        connector.setHandler(handler);
        connector.getSessionConfig().setReadBufferSize(2048);
        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 2);
        connector.getSessionConfig().setReuseAddress(false);

        try {
            future = connector.connect(new InetSocketAddress(hostName, getPort())).await();
        } catch (final InterruptedException e) {
            LOGGER.warn("Thread interrupted", e);
            Thread.currentThread().interrupt();
        }

        return this;
    }

    /**
     * Gets the host name.
     * @return the host name
     */
    public String getHostName() {
        synchronized (this) {
            if (hostName == null) {
                try {
                    hostName = InetAddress.getLocalHost().getHostName();
                } catch (@SuppressWarnings("unused") final UnknownHostException e) {
                    hostName = "localhost";
                }
            }

            return hostName;
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.test.net.AbstractTcpComponent#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.test.net.AbstractTcpComponent#isActive()
     */
    @Override
    public boolean isActive() {
        return future != null && future.isConnected();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.test.net.AbstractTcpComponent#send(byte[])
     */
    @Override
    public void send(final byte[] message) throws IOException {
        if (message == null) {
            LOGGER.warn("Cannot send a null message");
        }

        if (!isActive()) {
            LOGGER.warn("Cannot send message, client is not connected");
        }

        final String data = new String(message, getCharset());
        final IoBuffer buffer = IoBuffer.wrap(message);
        final IoSession session = future.getSession();

        if (session != null && session.isActive()) {
            LOGGER.info("Writing {} to session: {}", data, session.getRemoteAddress());
            session.write(buffer);
        }
    }
}
