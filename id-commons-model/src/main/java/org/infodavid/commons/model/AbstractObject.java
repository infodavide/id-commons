package org.infodavid.commons.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class AbstractObject.
 * @param <K> the key type
 */
@ModelObject
@MappedSuperclass
@NoArgsConstructor
@Setter
@Getter
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
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
