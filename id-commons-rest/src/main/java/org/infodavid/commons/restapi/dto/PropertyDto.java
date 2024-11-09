package org.infodavid.commons.restapi.dto;

import java.util.Objects;

import org.infodavid.commons.model.Property;
import org.infodavid.commons.restapi.annotation.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class PropertyDto.
 */
@DataTransferObject(model = Property.class)
@Getter
@Setter
public class PropertyDto extends AbstractDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4596882638533418420L;

    /** The default value. */
    private String defaultValue;

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

        if (!(obj instanceof final PropertyDto other)) {
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
