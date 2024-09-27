package org.infodavid.commons.util.jdbc;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The Record ColumnMetadata.
 * @param label       the label
 * @param name        the name
 * @param type        the type
 * @param typeName    the type name
 * @param index       the index
 * @param displaySize the display size
 */
public record ColumnMetadata(String label, String name, int type, String typeName, int index, int displaySize) {

    /*
     * (non-javadoc)
     * @see java.lang.Record#toString()
     */
    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
