package org.infodavid.commons.net;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * The Record Host.
 * @param application the application
 * @param name        the name
 * @param uptime      the time since the activation
 * @param interfaces  the interfaces
 */
public record Host(String application, String name, long uptime, NetworkInterface... interfaces) {

    /*
     * (non-javadoc)
     * @see java.lang.Record#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /*
     * (non-javadoc)
     * @see java.lang.Record#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Host)) {
            return false;
        }
        Host other = (Host) obj;
        return Objects.equals(name, other.name);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.AbstractObject#toString()
     */
    @Override
    public String toString() { // NOSONAR No complexity
        final StringBuilder buffer = new StringBuilder();
        buffer.append("Application,");
        buffer.append(Objects.toString(application, StringUtils.EMPTY));
        buffer.append('\n');
        buffer.append("Hostname,");
        buffer.append(Objects.toString(name, StringUtils.EMPTY));
        buffer.append('\n');
        buffer.append("Interfaces");

        if (interfaces == null) {
            buffer.append(',');
        } else {
            for (final NetworkInterface info : interfaces) {
                buffer.append(',');
                buffer.append(info.toString());
            }
        }

        buffer.append('\n');
        buffer.append("uptime,");
        buffer.append(uptime);
        buffer.append('\n');

        return buffer.toString();
    }
}
