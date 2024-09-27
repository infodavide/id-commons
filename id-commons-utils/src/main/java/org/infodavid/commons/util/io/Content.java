package org.infodavid.commons.util.io;

import java.util.Arrays;
import java.util.Date;

/**
 * The Record Content.
 * @param data             the data
 * @param modificationDate the modification date
 */
record Content(byte[] data, long modificationDate) {

    /*
     * (non-javadoc)
     * @see java.lang.Record#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        
        return result;
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
        
        if (!(obj instanceof Content)) {
            return false;
        }
        
        Content other = (Content) obj;
        
        return Arrays.equals(data, other.data);
    }
    
    /*
     * (non-javadoc)
     * @see org.infodavid.model.AbstractObject#toString()
     */
    @Override
    public String toString() { // NOSONAR No complexity
        final StringBuilder buffer = new StringBuilder();
        buffer.append(getClass());
        buffer.append('@');
        buffer.append(hashCode());
        buffer.append("(length: ");
        buffer.append(data == null ? 0 : data.length);
        buffer.append(",modificationDate: ");
        buffer.append(new Date(modificationDate));

        return buffer.toString();
    }
}
