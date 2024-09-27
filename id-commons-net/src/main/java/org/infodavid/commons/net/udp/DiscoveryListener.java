package org.infodavid.commons.net.udp;

import java.net.InetAddress;

/**
 * The interface DiscoveryListener.
 */
public interface DiscoveryListener {

    /**
     * Received.
     * @param data the data
     * @param address the address
     */
    void received(byte[] data, InetAddress address);

    /**
     * Stopped.
     */
    void stopped();

}
