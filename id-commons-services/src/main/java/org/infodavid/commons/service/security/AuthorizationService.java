package org.infodavid.commons.service.security;

import java.io.Serializable;
import java.security.Principal;

import org.infodavid.commons.model.PersistentObject;
import org.infodavid.commons.service.exception.ServiceException;

/**
 * The Interface AuthorizationService.
 */
public interface AuthorizationService {

    /**
     * Assert add authorization.
     * @param <K>         the identifier type
     * @param <T>         the generic type
     * @param principal   the principal
     * @param entityClass the entity class
     * @param entity      the entity to add
     * @throws IllegalAccessException the illegal access exception
     */
    <K extends Serializable, T extends PersistentObject<K>> void assertAddAuthorization(Principal principal, Class<T> entityClass, T entity) throws IllegalAccessException;

    /**
     * Assert delete authorization.
     * @param <K>         the identifier type
     * @param <T>         the generic type
     * @param principal   the principal
     * @param entityClass the entity class
     * @param id          the identifier of the entity to be deleted
     * @throws IllegalAccessException the illegal access exception
     */
    <K extends Serializable, T extends PersistentObject<K>> void assertDeleteAuthorization(Principal principal, Class<T> entityClass, K id) throws IllegalAccessException;

    /**
     * Assert role.
     * @param principal the principal
     * @param role      the role
     * @throws IllegalAccessException the illegal access exception
     */
    void assertRole(Principal principal, String role) throws IllegalAccessException;

    /**
     * Assert update authorization.
     * @param <K>         the identifier type
     * @param <T>         the generic type
     * @param principal   the principal
     * @param entityClass the entity class
     * @param id          the identifier of the entity to be updated
     * @throws IllegalAccessException the illegal access exception
     */
    <K extends Serializable, T extends PersistentObject<K>> void assertUpdateAuthorization(Principal principal, Class<T> entityClass, K id) throws IllegalAccessException;

    /**
     * Can add.
     * @param <K>         the identifier type
     * @param <T>         the generic type
     * @param principal   the principal
     * @param entityClass the entity class
     * @param entity      the entity
     * @return true, if successful
     */
    <K extends Serializable, T extends PersistentObject<K>> boolean canAdd(Principal principal, Class<T> entityClass, T entity);

    /**
     * Can delete.
     * @param <K>         the key type
     * @param <T>         the identifier type
     * @param principal   the principal
     * @param entityClass the entity class
     * @param id          the identifier
     * @return true, if successful
     */
    <K extends Serializable, T extends PersistentObject<K>> boolean canDelete(Principal principal, Class<T> entityClass, K id);

    /**
     * Can edit.
     * @param <K>         the identifier type
     * @param <T>         the generic type
     * @param principal   the principal
     * @param entityClass the entity class
     * @param id          the identifier
     * @return true, if successful
     */
    <K extends Serializable, T extends PersistentObject<K>> boolean canEdit(Principal principal, Class<T> entityClass, K id);

    /**
     * Gets the user associated to the current security context.
     * @return the user
     * @throws ServiceException the service exception
     */
    Principal getPrincipal() throws ServiceException;
}
