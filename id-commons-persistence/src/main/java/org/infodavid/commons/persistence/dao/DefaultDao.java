package org.infodavid.commons.persistence.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import org.infodavid.commons.model.PersistentObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Interface DefaultDao.
 * @param <K> the key type
 * @param <T> the generic type
 */
public interface DefaultDao<K extends Serializable, T extends PersistentObject<K>> {

    /**
     * Count.
     * @return the number of rows
     * @throws PersistenceException the persistence exception
     */
    long count() throws PersistenceException;

    /**
     * Delete by identifier.
     * @param id the identifier
     * @throws PersistenceException the persistence exception
     */
    void deleteById(K id) throws PersistenceException;

    /**
     * Select the data.
     * @param pageable the page definition
     * @return the entities
     * @throws PersistenceException the persistence exception
     */
    Page<T> findAll(Pageable pageable) throws PersistenceException;

    /**
     * Find by identifier.
     * @param id the identifier
     * @return the value or null
     * @throws PersistenceException the persistence exception
     */
    Optional<T> findById(K id) throws PersistenceException;

    /**
     * Insert.
     * @param value the value
     * @throws PersistenceException the persistence exception
     */
    void insert(T value) throws PersistenceException;

    /**
     * Insert.
     * @param values the values
     * @throws PersistenceException the persistence exception
     */
    void insert(Collection<T> values) throws PersistenceException;

    /**
     * Update.
     * @param value the value
     * @throws PersistenceException the persistence exception
     */
    void update(T value) throws PersistenceException;

    /**
     * Update.
     * @param values the values
     * @throws PersistenceException the persistence exception
     */
    void update(Collection<T> values) throws PersistenceException;
}
