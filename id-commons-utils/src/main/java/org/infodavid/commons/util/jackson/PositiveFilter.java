package org.infodavid.commons.util.jackson;

/**
 * The Class PositiveFilter.
 */
public class PositiveFilter {

    /*
     * (non-javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) { // NOSONAR No hashCode method
        // Return true if filtering out (excluding), false to include
        if (o instanceof Number number) {
            return number.longValue() >= 0L;
        }

        return false;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
