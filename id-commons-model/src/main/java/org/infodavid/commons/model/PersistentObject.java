package org.infodavid.commons.model;

import java.io.Serializable;
import java.util.Date;

/**
 * The Interface PersistentObject.
 * @param <K> the key type
 */
public interface PersistentObject<K extends Serializable> extends Serializable {

    /**
     * Gets the archiving date.
     * @return the date
     */
    Date getArchivingDate();

    /**
     * Gets the creation date.
     * @return the creation date
     */
    Date getCreationDate();

    /**
     * Gets the identifier.
     * @return the identifier
     */
    K getId();

    /**
     * Gets the modification date.
     * @return the date
     */
    Date getModificationDate();

    /**
     * Checks if is deletable.
     * @return true, if is deletable
     */
    boolean isDeletable();

    /**
     * Sets the archiving date.
     * @param value the new date
     */
    void setArchivingDate(Date value);

    /**
     * Sets the creation date.
     * @param value the new date
     */
    void setCreationDate(Date value);

    /**
     * Sets the deletable.
     * @param deletable the new deletable
     */
    void setDeletable(boolean deletable);

    /**
     * Sets the identifier.
     * @param id the identifier
     */
    void setId(K id);

    /**
     * Sets the modification date.
     * @param value the date
     */
    void setModificationDate(Date value);
}
