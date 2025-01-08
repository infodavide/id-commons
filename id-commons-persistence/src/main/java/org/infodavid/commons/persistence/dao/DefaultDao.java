package org.infodavid.commons.persistence.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.infodavid.commons.model.PersistentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Interface DefaultDao.
 * @param <K> the key type
 * @param <T> the generic type
 */
public interface DefaultDao<K extends Serializable, T extends PersistentEntity<K>> {

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
    <S extends T> S insert(S value) throws PersistenceException;

    /**
     * Insert.
     * @param values the values
     * @throws PersistenceException the persistence exception
     */
    <S extends T> List<S> insert(Iterable<S> values) throws PersistenceException;

    /**
     * Update.
     * @param value the value
     * @throws PersistenceException the persistence exception
     */
    <S extends T> void update(S value) throws PersistenceException;

    /**
     * Update.
     * @param values the values
     * @throws PersistenceException the persistence exception
     */
    <S extends T> void update(Iterable<S> values) throws PersistenceException;
}
