package org.infodavid.commons.persistence.dao;

import java.util.Collection;
import java.util.Optional;

import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.User;
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
     * Selects by one of the given roles.
     * @param values   the roles
     * @param pageable the page definition
     * @return the users
     * @throws PersistenceException the persistence exception
     */
    Page<User> findByRoleIn(Collection<String> values, Pageable pageable) throws PersistenceException;

    /**
     * Find references.
     * @param pageable the page definition
     * @return the collection
     * @throws PersistenceException the persistence exception
     */
    Page<DefaultEntityReference> findReferences(Pageable pageable) throws PersistenceException;
}
