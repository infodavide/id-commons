package org.infodavid.commons.model;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.processing.Generated;

import org.infodavid.commons.model.annotation.ModelObject;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class AbstractEntity.
 * @param <K> the key type
 */
@ModelObject
@MappedSuperclass
@Access(AccessType.PROPERTY)
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractEntity<K extends Serializable> extends AbstractObject<K> implements PersistentEntity<K> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7292704857170542767L;

    /** The creation date. */
    private Date creationDate;

    /** The modification date. */
    private Date modificationDate;

    /**
     * The Constructor.
     * @param source the source
     */
    protected AbstractEntity(final AbstractEntity<K> source) {
        super(source);
        creationDate = source.creationDate;
        modificationDate = source.modificationDate;
    }

    /**
     * The Constructor.
     * @param id the identifier
     */
    protected AbstractEntity(final K id) {
        super(id);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentEntity#getCreationDate()
     */
    @Override
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationDate() {
        return creationDate;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractObject#getId()
     */
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public K getId() {
        return super.getId();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentEntity#getModificationDate()
     */
    @Override
    @Column(name = "modification_date")
    @Temporal(TemporalType.TIMESTAMP)
    @Generated("Set by the service on add or update")
    @Version
    public Date getModificationDate() {
        return modificationDate;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentEntity#setCreationDate(java.util.Date)
     */
    @Override
    public void setCreationDate(final Date value) {
        creationDate = value;

        if (getModificationDate() == null) {
            setModificationDate(value);
        }
    }
}
