package org.infodavid.commons.model;

import java.io.Serializable;

/**
 * The Interface PersistentEntity.
 * @param <K> the key type
 */
public interface PersistentObject<K extends Serializable> extends Serializable {

    /**
     * Gets the identifier.
     * @return the identifier
     */
    K getId();

    /**
     * Sets the identifier.
     * @param id the identifier
     */
    void setId(K id);
}
