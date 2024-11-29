package org.infodavid.commons.rest.v1.api.dto;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.processing.Generated;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class AbstractDto.
 */
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public abstract class AbstractDto<K extends Serializable> implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7617395809265767672L;

    /** The creation date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Generated("Set when adding the data")
    private Date creationDate;

    /** The deletable. */
    @Generated("Set by the service according to the authenticated user")
    private boolean deletable = true;

    /** The editable. */
    @Generated("Set by the service according to the authenticated user")
    private boolean editable = true;

    /** The identifier. */
    private K id;

    /** The modification date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Generated("Set when updating the data")
    private Date modificationDate;

    /**
     * The Constructor.
     */
    protected AbstractDto() {
    }

    /**
     * The Constructor.
     * @param id the identifier
     */
    protected AbstractDto(final K id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
