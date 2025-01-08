package org.infodavid.commons.model;

import java.io.Serializable;
import java.util.Date;

/**
 * The Interface PersistentEntity.
 * @param <K> the key type
 */
public interface PersistentEntity<K extends Serializable> extends PersistentObject<K> {

    /**
     * Gets the creation date.
     * @return the creation date
     */
    Date getCreationDate();

    /**
     * Gets the modification date.
     * @return the date
     */
    Date getModificationDate();

    /**
     * Sets the creation date.
     * @param value the new date
     */
    void setCreationDate(Date value);

    /**
     * Sets the modification date.
     * @param value the date
     */
    void setModificationDate(Date value);
}
