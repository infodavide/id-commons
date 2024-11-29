package org.infodavid.commons.rest.v1.api.dto;

import java.io.Serializable;
import java.util.Objects;

import org.infodavid.commons.rest.api.annotation.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class EntityPropertyDto.
 */
@DataTransferObject(model = "org.infodavid.commons.model.EntityProperty")
@Getter
@Setter
public class EntityPropertyDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4596882638533418420L;

    /** The default value. */
    private String defaultValue;

    /** The deletable. */
    private boolean deletable;

    /** The label. */
    private String label;

    /** The maximum. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double maximum;

    /** The minimum. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double minimum;

    /** The name. */
    private String name;

    /** The read only. */
    private boolean readOnly;

    /** The scope. */
    private String scope;

    /** The type. */
    private String type;

    /** The type definition. */
    private String typeDefinition;

    /** The value. */
    private String value;

    /*
     * (non-javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) { // NOSONAR Generated
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof final EntityPropertyDto other)) {
            return false;
        }

        return Objects.equals(name, other.name);
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();

        return prime * result + (name == null ? 0 : name.hashCode());
    }
}
