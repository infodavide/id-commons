package org.infodavid.commons.restapi.dto;

import java.util.List;

import org.infodavid.commons.restapi.annotation.DataTransferObject;
import org.springframework.data.domain.Page;

/**
 * The Record PageDto.
 * @param canAdd    the can add
 * @param number    the number
 * @param results   the results
 * @param size      the size
 * @param totalSize the total size
 */
@DataTransferObject(model = Page.class)
@SuppressWarnings("rawtypes")
public record PageDto(boolean canAdd, int number, List results, int size, long totalSize) {

    /*
     * (non-javadoc)
     * @see java.lang.Record#toString()
     */
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(getClass());
        buffer.append("@");
        buffer.append(hashCode());
        buffer.append('(');
        buffer.append("canAdd=").append(canAdd);
        buffer.append(",number=").append(number);
        buffer.append(",size=").append(size);
        buffer.append(",totalSize=").append(totalSize);
        buffer.append(",results=").append(results == null ? 0 : results.size());
        buffer.append(')');

        return buffer.toString();
    }
}
