package org.infodavid.commons.model;

import java.io.Serializable;
import java.util.Date;

import org.infodavid.commons.model.annotation.ModelObject;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractArchivableObject<K extends Serializable> extends AbstractEntity<K> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7292704857170542768L;

    @Column(name = "archiving_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date archivingDate;

    /**
     * The Constructor.
     * @param source the source
     */
    protected AbstractArchivableObject(final AbstractArchivableObject<K> source) {
        super(source);
        archivingDate = source.archivingDate;
    }

    /**
     * The Constructor.
     * @param id the identifier
     */
    protected AbstractArchivableObject(final K id) {
        super(id);
    }
}
