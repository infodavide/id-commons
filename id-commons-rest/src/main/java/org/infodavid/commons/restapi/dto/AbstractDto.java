package org.infodavid.commons.restapi.dto;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.processing.Generated;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class AbstractDto.
 */
@JsonInclude(Include.NON_NULL)
public abstract class AbstractDto implements Serializable {

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
    private String id;

    /** The modification date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Generated("Set when updating the data")
    private Date modificationDate;

    /**
     * The Constructor.
     */
    protected AbstractDto() {
        init();
    }

    /**
     * The Constructor.
     * @param id the identifier
     */
    protected AbstractDto(final String id) {
        init();
        this.id = id;
    }

    /**
     * Gets the creation date.
     * @return the creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the identifier.
     * @return the identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the modification date.
     * @return the modification date
     */
    public Date getModificationDate() {
        return modificationDate;
    }

    /**
     * Initializes the object.
     */
    public void init() {
        creationDate = null;
        modificationDate = null;
        id = null;
    }

    /**
     * Checks if is deletable.
     * @return the deletable
     */
    public boolean isDeletable() {
        return deletable;
    }

    /**
     * Checks if is editable.
     * @return the editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the creation date.
     * @param value the new creation date
     */
    public void setCreationDate(final Date value) {
        creationDate = value;
    }

    /**
     * Sets the deletable.
     * @param deletable the deletable to set
     */
    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Sets the editable.
     * @param editable the editable to set
     */
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    /**
     * Sets the identifier.
     * @param value the new identifier
     */
    public void setId(final String value) {
        id = value;
    }

    /**
     * Sets the modification date.
     * @param value the new modification date
     */
    public void setModificationDate(final Date value) {
        modificationDate = value;
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
