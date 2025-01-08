package org.infodavid.commons.service.security;

import org.infodavid.commons.service.exception.ServiceException;

/**
 * The Interface AuthorizationService.
 */
@SuppressWarnings("rawtypes")
public interface AuthorizationService {

    /**
     * Assert add authorization.
     * @param principal   the principal
     * @param entityClass the entity class
     * @param parentId    the parent identifier
     * @throws IllegalAccessException the illegal access exception
     */
    void assertAddAuthorization(UserPrincipal principal, Class entityClass, Object parentId) throws IllegalAccessException;

    /**
     * Assert delete authorization.
     * @param principal   the principal
     * @param entityClass the entity class
     * @param id          the identifier of the entity to be deleted
     * @throws IllegalAccessException the illegal access exception
     */
    void assertDeleteAuthorization(UserPrincipal principal, Class entityClass, Object id) throws IllegalAccessException;

    /**
     * Assert role.
     * @param principal the principal
     * @param role      the role
     * @throws IllegalAccessException the illegal access exception
     */
    void assertRole(UserPrincipal principal, String role) throws IllegalAccessException;

    /**
     * Assert update authorization.
     * @param principal   the principal
     * @param entityClass the entity class
     * @param id          the identifier of the entity to be updated
     * @throws IllegalAccessException the illegal access exception
     */
    void assertUpdateAuthorization(UserPrincipal principal, Class entityClass, Object id) throws IllegalAccessException;

    /**
     * Can add.
     * @param principal   the principal
     * @param entityClass the entity class
     * @param parentId    the parent identifier
     * @return true, if successful
     */
    boolean canAdd(UserPrincipal principal, Class entityClass, Object parentId);

    /**
     * Can delete.
     * @param principal   the principal
     * @param entityClass the entity class
     * @param id          the identifier
     * @return true, if successful
     */
    boolean canDelete(UserPrincipal principal, Class entityClass, Object id);

    /**
     * Can edit.
     * @param principal   the principal
     * @param entityClass the entity class
     * @param id          the identifier
     * @return true, if successful
     */
    boolean canEdit(UserPrincipal principal, Class entityClass, Object id);

    /**
     * Gets the principal associated to the current security context.
     * @return the principal
     * @throws ServiceException the service exception
     */
    UserPrincipal getPrincipal() throws ServiceException;
}
