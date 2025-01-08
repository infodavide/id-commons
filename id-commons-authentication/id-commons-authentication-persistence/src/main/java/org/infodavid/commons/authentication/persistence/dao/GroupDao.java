package org.infodavid.commons.authentication.persistence.dao;

import java.util.Optional;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.persistence.dao.DefaultDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Interface GroupDao.
 */
public interface GroupDao extends DefaultDao<Long, Group> {

    /**
     * Selects by name.
     * @param value the name
     * @return the group
     * @throws PersistenceException the persistence exception
     */
    Optional<Group> findByName(String value) throws PersistenceException;

    /**
     * Find having property.
     * @param scope the scope
     * @param name  the name
     * @return the group
     * @throws PersistenceException the persistence exception
     */
    Optional<Group> findByProperty(String scope, String name, String value) throws PersistenceException;

    /**
     * Selects by role.
     * @param value    the role
     * @param pageable the page definition
     * @return the groups
     * @throws PersistenceException the persistence exception
     */
    Page<Group> findByRole(String value, Pageable pageable) throws PersistenceException;

    /**
     * Find having property.
     * @param scope    the scope
     * @param name     the name
     * @param pageable the page definition
     * @return the groups
     * @throws PersistenceException the persistence exception
     */
    Page<Group> findHavingProperty(String scope, String name, Pageable pageable) throws PersistenceException;

    /**
     * Find references.
     * @param pageable the page definition
     * @return the collection
     * @throws PersistenceException the persistence exception
     */
    Page<DefaultEntityReference> findReferences(Pageable pageable) throws PersistenceException;
}
