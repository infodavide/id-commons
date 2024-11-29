package org.infodavid.commons.authentication.persistence.dao;

import java.util.Optional;

import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.persistence.dao.DefaultDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Interface UserDao.
 */
public interface UserDao extends DefaultDao<Long, User> {

    /**
     * Delete deletable.
     * @throws PersistenceException the persistence exception
     */
    void deleteDeletable() throws PersistenceException;

    /**
     * Selects by email.
     * @param value the email
     * @return the user
     * @throws PersistenceException the persistence exception
     */
    Optional<User> findByEmail(String value) throws PersistenceException;

    /**
     * Selects by name.
     * @param value the name
     * @return the user
     * @throws PersistenceException the persistence exception
     */
    Optional<User> findByName(String value) throws PersistenceException;

    /**
     * Find having property.
     * @param scope the scope
     * @param name  the name
     * @return the user
     * @throws PersistenceException the persistence exception
     */
    Optional<User> findByProperty(String scope, String name, String value) throws PersistenceException;

    /**
     * Find having property.
     * @param scope    the scope
     * @param name     the name
     * @param pageable the page definition
     * @return the users
     * @throws PersistenceException the persistence exception
     */
    Page<User> findHavingProperty(String scope, String name, Pageable pageable) throws PersistenceException;

    /**
     * Selects by role.
     * @param value   the role
     * @param pageable the page definition
     * @return the users
     * @throws PersistenceException the persistence exception
     */
    Page<User> findByRole(String value, Pageable pageable) throws PersistenceException;

    /**
     * Find references.
     * @param pageable the page definition
     * @return the collection
     * @throws PersistenceException the persistence exception
     */
    Page<DefaultEntityReference> findReferences(Pageable pageable) throws PersistenceException;
}
