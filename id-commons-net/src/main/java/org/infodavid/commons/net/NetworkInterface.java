package org.infodavid.commons.net;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * The Record NetworkInterface.
 * @param name        the name
 * @param displayName the display name
 * @param macAddress  the MAC address
 * @param ipv4Address the IP v4 address
 * @param ipv6Address the IP v6 address
 * @param netmask     the network mask
 * @param gateway     the gateway
 * @param mtu         the maximum transmission unit
 * @param connected   the connected
 */
public record NetworkInterface(String name, String displayName, String macAddress, String ipv4Address, String ipv6Address, String netmask, String gateway, long mtu, boolean connected) {

    /*
     * (non-javadoc)
     * @see org.infodavid.model.AbstractObject#toString()
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(Objects.toString(name, StringUtils.EMPTY));
        buffer.append(';');
        buffer.append(Objects.toString(displayName, StringUtils.EMPTY));
        buffer.append(';');
        buffer.append(Objects.toString(macAddress, StringUtils.EMPTY));
        buffer.append(';');
        buffer.append(Objects.toString(ipv4Address, StringUtils.EMPTY));
        buffer.append(';');
        buffer.append(Objects.toString(ipv6Address, StringUtils.EMPTY));
        buffer.append(';');
        buffer.append(Objects.toString(netmask, StringUtils.EMPTY));
        buffer.append(';');
        buffer.append(Objects.toString(gateway, StringUtils.EMPTY));
        buffer.append(';');
        buffer.append(mtu);
        buffer.append(';');
        buffer.append(connected ? "true" : "false");

        return buffer.toString();
    }
}
