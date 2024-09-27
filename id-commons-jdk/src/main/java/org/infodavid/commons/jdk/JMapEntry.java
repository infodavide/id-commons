package org.infodavid.commons.jdk;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The Record JMapEntry.
 * @param className the class name
 * @param instances the instances
 * @param bytes     the bytes
 */
public record JMapEntry(String className, long instances, long bytes) {

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

        final JMapEntry other = (JMapEntry)obj;

        return Objects.equals(className, other.className);
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(className);
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
