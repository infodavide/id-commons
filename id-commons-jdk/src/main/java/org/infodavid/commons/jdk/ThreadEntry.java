package org.infodavid.commons.jdk;

import java.lang.Thread.State;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The Record ThreadEntry.
 * @param name        the name
 * @param state       the state
 * @param daemon      the daemon
 * @param interrupted the interrupted
 * @param stackTrace  the stack trace
 */
public record ThreadEntry(String name, State state, boolean daemon, boolean interrupted, String... stackTrace) {

    /*
     * (non-javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ThreadEntry other = (ThreadEntry) obj;

        return Objects.equals(name, other.name);
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
