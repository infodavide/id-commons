package org.infodavid.commons.persistence.dao;

import java.util.Optional;

import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Interface ConfigurationPropertyDao.
 */
public interface ConfigurationPropertyDao extends DefaultDao<Long, ConfigurationProperty> {

    /**
     * Delete by name.
     * @param name the name
     * @throws PersistenceException the persistence exception
     */
    void deleteByName(String name) throws PersistenceException;

    /**
     * Delete deletable.
     * @throws PersistenceException the persistence exception
     */
    void deleteDeletable() throws PersistenceException;

    /**
     * Selects by name.
     * @param value    the name
     * @param pageable the page definition
     * @return the property or null
     * @throws PersistenceException the persistence exception
     */
    Page<ConfigurationProperty> findByName(String value, Pageable pageable) throws PersistenceException;

    /**
     * Selects by scope.
     * @param value    the scope
     * @param pageable the page definition
     * @return the properties
     * @throws PersistenceException the persistence exception
     */
    Page<ConfigurationProperty> findByScope(String value, Pageable pageable) throws PersistenceException;

    /**
     * Find by scope and name.
     * @param scope the scope
     * @param name  the name
     * @return the optional
     * @throws PersistenceException the persistence exception
     */
    Optional<ConfigurationProperty> findByScopeAndName(String scope, String name) throws PersistenceException;

    /**
     * Find references.
     * @param pageable the page definition
     * @return the collection
     * @throws PersistenceException the persistence exception
     */
    Page<DefaultEntityReference> findReferences(Pageable pageable) throws PersistenceException;
}
