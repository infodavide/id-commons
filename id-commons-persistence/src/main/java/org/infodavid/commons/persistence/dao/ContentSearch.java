package org.infodavid.commons.persistence.dao;

import java.io.Serializable;

import org.infodavid.commons.model.PersistentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Interface ContentSearch.
 */
public interface ContentSearch<K extends Serializable, T extends PersistentEntity<K>> {

    /**
     * Search.
     * @param needle   the needle
     * @param pageable the page definition
     * @return the page
     * @throws PersistenceException the persistence exception
     */
    Page<T> search(String needle, Pageable pageable) throws PersistenceException;
}
