package org.infodavid.commons.restapi.dto;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.infodavid.commons.restapi.annotation.DataTransferObject;

/**
 * The Record HealthInfoDto.
 * @param value      the value
 * @param production the production
 * @param uptime     the uptime
 */
@DataTransferObject
public record HealthInfoDto(String value, boolean production, long uptime, Map<String, Object> attributes) {

    /*
     * (non-javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
