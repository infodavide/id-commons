package org.infodavid.commons.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.ArrayUtils;
import org.infodavid.commons.util.concurrency.ThreadUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DiscoveryClient.
 */
@Slf4j
@Getter
@Setter
public class DiscoveryClient {

    /**
     * The Class DispatcherRunnable.
     */
    private static class DispatcherRunnable implements Runnable {

        /** The listener. */
        private final DiscoveryListener listener;

        /** The packet. */
        private final DatagramPacket packet;

        /**
         * Instantiates a new dispatcher runnable.
         * @param packet   the packet
         * @param listener the listener
         */
        public DispatcherRunnable(final DatagramPacket packet, final DiscoveryListener listener) {
            this.packet = packet;
            this.listener = listener;
        }

        /*
         * (non-javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            LOGGER.debug("Processing response from host: {}", packet.getAddress());

            try {
                final byte[] data = ArrayUtils.subarray(packet.getData(), packet.getOffset(), packet.getLength());

                if (data == null || data.length == 0) {
                    LOGGER.warn("Invalid response from host: {}", packet.getAddress());
                } else {
                    listener.received(data, packet.getAddress());
                }
            } catch (final Exception e) {
                LOGGER.error("Malformed response from host: {}", packet.getAddress(), e);
            }
        }
    }

    /** The active. */
    private final AtomicBoolean active = new AtomicBoolean(true);

    /** The broadcast address. */
    private InetAddress broadcastAddress;

    /** The charset. */
    private Charset charset = DiscoveryServiceRunnable.DEFAULT_CHARSET;

    /** The command. */
    private byte[] command = DiscoveryServiceRunnable.DEFAULT_DISCOVERY_COMMAND;

    /** The discovery port. */
    private int discoveryPort = DiscoveryServiceRunnable.DEFAULT_DISCOVERY_PORT;

    /** The timeout. */
    private int timeout = DiscoveryServiceRunnable.DEFAULT_TIMEOUT;

    /**
     * Discover the hosts in the network.
     * @param listener the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void discover(final DiscoveryListener listener) throws IOException { // NOSONAR No complexity
        LOGGER.info("Starting discovery of hosts using UDP port: {}", String.valueOf(discoveryPort)); // NOSONAR Always written
        active.set(true);

        try (ExecutorService executor = ThreadUtils.newThreadPoolExecutor(getClass(), LOGGER, 4, Byte.MAX_VALUE); DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(timeout);
            List<InetAddress> broadcastAddresses;

            if (broadcastAddress == null) {
                broadcastAddresses = new ArrayList<>();
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

                while (interfaces.hasMoreElements()) {
                    final NetworkInterface networkInterface = interfaces.nextElement();

                    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                        continue; // Don't want to broadcast to the loopback interface
                    }

                    for (final InterfaceAddress iaddr : networkInterface.getInterfaceAddresses()) {
                        final InetAddress addr = iaddr.getBroadcast();

                        if (addr == null) {
                            continue;
                        }

                        broadcastAddresses.add(addr);
                    }
                }
            } else {
                broadcastAddresses = Collections.singletonList(broadcastAddress);
            }

            for (final InetAddress addr : broadcastAddresses) {
                socket.send(new DatagramPacket(command, command.length, addr, discoveryPort));
                LOGGER.info("Request packet using address: {}", addr.getHostAddress());
            }

            while (active.get()) {
                final byte[] buffer = new byte[15000];
                final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                try {
                    socket.receive(packet);
                    LOGGER.debug("Response from host: {}", packet.getAddress());
                    executor.submit(new DispatcherRunnable(packet, listener));
                } catch (@SuppressWarnings("unused") final SocketTimeoutException e) {
                    // noop
                }
            }
        } catch (final IOException e) {
            LOGGER.error("Discovery failure", e);
        } finally {
            active.set(false);
            LOGGER.info("Discovery terminated");
            listener.stopped();
        }
    }

    /**
     * Checks if is active.
     * @return true, if is active
     */
    public boolean isActive() {
        return active.get();
    }

    /**
     * Sets the command.
     * @param command the command to set
     */
    public void setCommand(final byte[] command) {
        this.command = command;

        if (command == null || command.length == 0) {
            this.command = DiscoveryServiceRunnable.DEFAULT_DISCOVERY_COMMAND;
            LOGGER.warn("Invalid discovery command, using: {}", String.valueOf(this.command)); // NOSONAR Always written
        }
    }

    /**
     * Sets the discovery port.
     * @param discoveryPort the discoveryPort to set
     */
    public void setDiscoveryPort(final int discoveryPort) {
        this.discoveryPort = discoveryPort;

        if (discoveryPort <= 1000) {
            this.discoveryPort = DiscoveryServiceRunnable.DEFAULT_DISCOVERY_PORT;
            LOGGER.warn("Invalid discovery port, using: {}", String.valueOf(this.discoveryPort)); // NOSONAR Always written
        }
    }

    /**
     * Sets the timeout.
     * @param timeout the timeout to set
     */
    public void setTimeout(final int timeout) {
        this.timeout = timeout;

        if (timeout <= 0) {
            this.timeout = DiscoveryServiceRunnable.DEFAULT_TIMEOUT;
            LOGGER.warn("Invalid discovery timeout, using: {}", String.valueOf(this.timeout)); // NOSONAR Always written
        }
    }

    /**
     * Stop.
     */
    public void stop() {
        active.set(false);
    }
}
