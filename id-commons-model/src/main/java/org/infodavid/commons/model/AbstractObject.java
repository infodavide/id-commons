package org.infodavid.commons.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.annotation.processing.Generated;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.infodavid.commons.model.annotation.ModelObject;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;

/**
 * The Class AbstractObject.
 * @param <K> the key type
 */
@ModelObject
@MappedSuperclass
public abstract class AbstractObject<K extends Serializable> implements PersistentObject<K> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7292704857170542767L;

    @Column(name = "archiving_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date archivingDate;

    /** The creation date. */
    @Column(name = "cdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /** The deletable flag. */
    @Column(name = "deletable")
    private boolean deletable;

    /** The identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private K id;

    /** The modification date. */
    @Column(name = "mdate")
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    private Date modificationDate;

    /**
     * The Constructor.
     */
    protected AbstractObject() {
    }

    /**
     * The Constructor.
     * @param source the source
     */
    protected AbstractObject(final AbstractObject<K> source) {
        archivingDate = source.archivingDate;
        creationDate = source.creationDate;
        deletable = source.deletable;
        id = source.id;
        modificationDate = source.modificationDate;
    }

    /**
     * The Constructor.
     * @param id the identifier
     */
    protected AbstractObject(final K id) {
        this.id = id;
    }

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

        if (!(obj instanceof final PersistentObject<?> other)) {
            return false;
        }

        return Objects.equals(id, other.getId());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentObject#getArchivingDate()
     */
    @Override
    public Date getArchivingDate() {
        return archivingDate;
    }

    /**
     * See super class or interface. (non-Javadoc)
     * @return the creation date
     */
    @Override
    @Generated("Generated when inserting the data into the database")
    public Date getCreationDate() {
        return creationDate;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.PersistentObject#getId()
     */
    @Override
    public K getId() {
        return id;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.PersistentObject#getModificationDate()
     */
    @Override
    @Generated("Generated when updating the data into the database")
    public Date getModificationDate() {
        return modificationDate;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.commons.model.PersistentObject#isDeletable()
     */
    @Override
    public boolean isDeletable() {
        return deletable;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentObject#setArchivingDate(java.util.Date)
     */
    @Override
    public void setArchivingDate(final Date value) {
        archivingDate = value;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.PersistentObject#setCreationDate(java.util.Date)
     */
    @Override
    public void setCreationDate(final Date value) {
        creationDate = value;

        if (getModificationDate() == null) {
            setModificationDate(value);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.commons.model.PersistentObject#setDeletable(boolean)
     */
    @Override
    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.PersistentObject#setId(java.io.Serializable)
     */
    @Override
    public void setId(final K value) {
        id = value;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.PersistentObject#setModificationDate(java.util.Date)
     */
    @Override
    public void setModificationDate(final Date value) {
        modificationDate = value;
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
