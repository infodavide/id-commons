package org.infodavid.commons.net;

import static org.apache.commons.lang3.SystemUtils.IS_OS_FREE_BSD;
import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_NET_BSD;
import static org.apache.commons.lang3.SystemUtils.IS_OS_OPEN_BSD;
import static org.apache.commons.lang3.SystemUtils.IS_OS_UNIX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.apache.commons.lang3.SystemUtils.OS_NAME;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.util.ArrayUtils;
import org.infodavid.commons.util.system.CommandExecutorFactory;
import org.slf4j.Logger;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

/**
 * The Class NetUtils.
 */
public abstract sealed class NetUtils permits LinuxNetUtils, WindowsNetUtils {

    /** The hostname pattern. */
    public static final Pattern HOSTNAME_PATTERN = Pattern.compile("(?!-)[A-Z\\d-]{1,63}(?<!-)$", Pattern.CASE_INSENSITIVE);

    /** The Constant IP_PATTERN. */
    public static final Pattern IP_PATTERN = Pattern.compile("^(?:\\d{1,3}\\.){3}\\d{1,3}$");

    /** The Constant IS_NOT_SUPPORTED. */
    protected static final String IS_NOT_SUPPORTED = " is not supported";

    /** The Constant METHOD_IS_NOT_SUPPORTED_ON. */
    protected static final String METHOD_IS_NOT_SUPPORTED_ON = "Method is not supported on ";

    /** The Constant SINGLETON. */
    private static NetUtils singleton;

    /**
     * Gets the single instance.
     * @return single instance
     */
    public static synchronized NetUtils getInstance() {
        if (singleton == null) {
            if (IS_OS_FREE_BSD || IS_OS_LINUX || IS_OS_MAC || IS_OS_MAC_OSX || IS_OS_NET_BSD || IS_OS_OPEN_BSD || IS_OS_UNIX) {
                singleton = new LinuxNetUtils();
            } else if (IS_OS_WINDOWS) {
                singleton = new WindowsNetUtils();
            } else {
                throw new UnsupportedOperationException(OS_NAME + IS_NOT_SUPPORTED);
            }
        }

        return singleton;
    }

    /**
     * Close quietly.
     * @param socket the socket
     */
    public void closeQuietly(final Closeable socket) {
        if (socket == null) {
            return;
        }

        try {
            socket.close();
        } catch (@SuppressWarnings("unused") final Exception e) { // NOSONAR Quietly
            // noop
        }
    }

    /**
     * Execute.
     * @param command the command
     * @return the status code
     */
    protected int execute(final String[] command) {
        int code = -1;

        try {
            code = CommandExecutorFactory.getInstance().executeCommand(command);
        } catch (final Exception e) {
            getLogger().warn("An error occured while executing command: " + command, e); // NOSONAR Always written
        }

        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Exit code: {}", String.valueOf(code));
        }

        return code;
    }

    /**
     * Execute.
     * @param output  the output buffer
     * @param error   the error buffer
     * @param command the command
     * @return the status code
     */
    protected int execute(final StringBuilder output, final StringBuilder error, final String... command) {
        int code = -1;

        try {
            code = CommandExecutorFactory.getInstance().executeCommand(output, error, command);
        } catch (final Exception e) {
            getLogger().warn("An error occured while executing command: " + command, e); // NOSONAR Always written
        }

        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Exit code: {}", String.valueOf(code));
        }

        return code;
    }

    /**
     * Find an available TCP port.
     * @return the integer associated to the port
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public int findAvailableTcpPort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);

            return socket.getLocalPort();
        }
    }

    /**
     * Find an available TCP port.
     * @param port the initial port
     * @return the integer associated to the port
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public int findAvailableTcpPort(final int port) throws IOException {
        for (int i = port; i <= 65535; i++) {
            try (Socket socket = new Socket()) {
                socket.setSoTimeout(200);
                socket.setSoLinger(true, 0);
                socket.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), i), 200);

                if (!socket.isConnected()) {
                    return i;
                }
            } catch (@SuppressWarnings("unused") final SocketTimeoutException | ConnectException e) { // NOSONAR Expected exception
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the computer name.
     * @return the computer name
     */
    public String getComputerName() {
        return getHostName();
    }

    /**
     * Gets the computer name.
     * @return the computer name
     */
    public String getHostName() {
        final Map<String, String> env = System.getenv();

        if (env.containsKey("COMPUTERNAME")) {
            return env.get("COMPUTERNAME");
        }

        if (env.containsKey("HOSTNAME")) {
            return env.get("HOSTNAME");
        }

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException e) {
            getLogger().warn("Cannot read the hostname", e);

            return StringUtils.EMPTY;
        }
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected abstract Logger getLogger();

    /**
     * Gets the MAC address.
     * @param iface the interface
     * @return the address
     * @throws SocketException the socket exception
     */
    @SuppressWarnings("boxing")
    public String getMacAddress(final NetworkInterface iface) throws SocketException {
        final byte[] mac = iface.getHardwareAddress();

        if (mac == null) {
            return null;
        }

        final StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < mac.length; i++) {
            buffer.append(String.format("%02X%s", mac[i], i < mac.length - 1 ? "-" : ""));
        }

        return buffer.toString();
    }

    /**
     * Gets the MAC addresses.
     * @return the addresses
     */
    public List<String> getMacAddresses() {
        final Map<String, org.infodavid.commons.net.NetworkInterface> ifaces = getNetworkInterfaces();

        if (ifaces == null) {
            return Collections.emptyList();
        }

        return ifaces.values().stream().map(i -> i.macAddress()).toList();
    }

    /**
     * Gets the network mask.
     * @param value the value
     * @return the mask
     */
    @SuppressWarnings("boxing")
    public String getNetmask(final int value) {
        final long bits = 0xffffffff ^ (1 << 32 - value) - 1;

        return String.format("%d.%d.%d.%d", (bits & 0x0000000000ff000000L) >> 24, (bits & 0x0000000000ff0000) >> 16, (bits & 0x0000000000ff00) >> 8, bits & 0xff);

    }

    /**
     * Gets the network interfaces.
     * @return the network interfaces
     */
    public Map<String, org.infodavid.commons.net.NetworkInterface> getNetworkInterfaces() {
        final Map<String, org.infodavid.commons.net.NetworkInterface> results = new HashMap<>();
        final SystemInfo systemInfo = new SystemInfo();
        final String gateway = systemInfo.getOperatingSystem().getNetworkParams().getIpv4DefaultGateway();

        for (final NetworkIF iface : systemInfo.getHardware().getNetworkIFs()) {
            final String ipv4 = ArrayUtils.first(iface.getIPv4addr());
            final String ipv6 = ArrayUtils.first(iface.getIPv6addr());
            final String netmask = getNetmask(ArrayUtils.first(iface.getSubnetMasks()).intValue());
            results.put(iface.getName(), new org.infodavid.commons.net.NetworkInterface(iface.getName(), iface.getDisplayName(), iface.getMacaddr(), ipv4, ipv6, netmask, gateway, iface.getMTU(), iface.isConnectorPresent()));
        }

        return results;
    }

    /**
     * Checks if is same address.
     * @param left  the first address
     * @param right the second address
     * @return true, if addresses are for the same host
     */
    public boolean isSameHost(final String left, final String right) {
        try {
            if (StringUtils.equalsIgnoreCase(left, right)) {
                return true;
            }

            final boolean isLeftLoopbackAddress = InetAddress.getByName(left).isLoopbackAddress();
            final boolean isRightLoopbackAddress = InetAddress.getByName(right).isLoopbackAddress();

            if (isLeftLoopbackAddress && isRightLoopbackAddress) {
                return true;
            }

            if (isLeftLoopbackAddress || isRightLoopbackAddress) {
                String other;

                if (isLeftLoopbackAddress) {
                    other = right;
                } else {
                    other = left;
                }

                final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();

                while (ifaces.hasMoreElements()) {
                    final Enumeration<InetAddress> addresses = ifaces.nextElement().getInetAddresses();

                    while (addresses.hasMoreElements()) {
                        final InetAddress addr = addresses.nextElement();

                        if (addr.getHostAddress().equals(other) || addr.getHostName().equals(other)) {
                            return true;
                        }
                    }
                }
            }
        } catch (@SuppressWarnings("unused") final IOException e) {
            // noop
        }

        return false;
    }

    /**
     * Ping.
     * @param host the host
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void ping(final String host) throws IOException {
        final boolean reachable = InetAddress.getByName(host).isReachable(200);

        if (!reachable) {
            throw new IOException(host + " is not reachable");
        }
    }

    /**
     * Ping.
     * @param host the host
     * @param port the port
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void ping(final String host, final int port) throws IOException {
        boolean reachable = false;

        try (Socket socket = new Socket()) {
            socket.setSoTimeout(200);
            socket.setSoLinger(true, 0);
            socket.connect(new InetSocketAddress(host, port), 200);
            reachable = socket.isConnected();
        }

        if (!reachable) {
            throw new IOException(host + " is not reachable on port " + port);
        }
    }
}
