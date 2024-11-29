package org.infodavid.commons.authentication.service.impl.security;

import java.io.Serializable;
import java.security.Principal;

import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.service.impl.Constants;
import org.infodavid.commons.model.PersistentObject;
import org.infodavid.commons.service.security.AuthorizationService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultAuthorizationService.
 */
/* If necessary, declare the bean in the Spring configuration. */
@Slf4j
public class DefaultAuthorizationService implements AuthorizationService {

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertAddAuthorization(java.security.Principal, java.lang.Class, org.infodavid.commons.model.PersistentObject)
     */
    @Override
    public <K extends Serializable, T extends PersistentObject<K>> void assertAddAuthorization(final Principal principal, final Class<T> entityClass, final T value) throws IllegalAccessException {
        assertRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertDeleteAuthorization(java.security.Principal, java.lang.Class, java.io.Serializable)
     */
    @Override
    public <K extends Serializable, T extends PersistentObject<K>> void assertDeleteAuthorization(final Principal principal, final Class<T> entityClass, final K id) throws IllegalAccessException {
        assertRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertRole(java.security.Principal, java.lang.String)
     */
    @Override
    public void assertRole(final Principal principal, final String role) throws IllegalAccessException {
        if (!hasRole(principal, role)) {
            throw new IllegalAccessException("User does not have role: " + role);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertUpdateAuthorization(java.security.Principal, java.lang.Class, java.io.Serializable)
     */
    @Override
    public <K extends Serializable, T extends PersistentObject<K>> void assertUpdateAuthorization(final Principal principal, final Class<T> entityClass, final K id) throws IllegalAccessException {
        assertRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#canAdd(java.security.Principal, java.lang.Class, org.infodavid.commons.model.PersistentObject)
     */
    @Override
    public <K extends Serializable, T extends PersistentObject<K>> boolean canAdd(final Principal principal, final Class<T> entityClass, final T entity) {
        return hasRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#canDelete(java.security.Principal, java.lang.Class, java.io.Serializable)
     */
    @Override
    public <K extends Serializable, T extends PersistentObject<K>> boolean canDelete(final Principal principal, final Class<T> entityClass, final K id) {
        return hasRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#canEdit(java.security.Principal, java.lang.Class, java.io.Serializable)
     */
    @Override
    public <K extends Serializable, T extends PersistentObject<K>> boolean canEdit(final Principal principal, final Class<T> entityClass, final K id) {
        return hasRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#getPrincipal()
     */
    @Override
    public Principal getPrincipal() {
        final SecurityContext context = SecurityContextHolder.getContext();
        User result = null;

        if (context == null) {
            LOGGER.trace("No security context available");

            return null;
        }

        if (context.getAuthentication() == null) {
            LOGGER.trace("No authentication available");

            return null;
        }

        LOGGER.trace("Authentication: {}", context.getAuthentication());

        if (context.getAuthentication() instanceof AnonymousAuthenticationToken) { // NOSONAR Use of pattern matching
            result = org.infodavid.commons.authentication.model.Constants.ANONYMOUS_USER;
        } else if (context.getAuthentication().getPrincipal() instanceof final User user) { // NOSONAR Use of pattern matching
            result = user;
        } else if (context.getAuthentication().getDetails() instanceof final User user) { // NOSONAR Use of pattern matching
            result = user;
        }

        LOGGER.trace("Current user: {}", result);

        return result;
    }

    /**
     * Checks for role.
     * @param principal the principal
     * @param role      the role
     * @return true, if successful
     */
    @SuppressWarnings("boxing")
    public boolean hasRole(final Principal principal, final String role) {
        final User user = (User) principal;

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Role check for user: {} and role: {}", user, role);
        }

        if (user == null) {
            LOGGER.trace(Constants.USER_IS_NULL);

            // We assume that anonymous user is not null and null user is the system itself
            return true;
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            LOGGER.trace(Constants.USER_HAS_NO_ROLE_DENIED);

            return false;
        }

        if (user.getRoles().contains(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE)) {
            LOGGER.trace(Constants.USER_IS_AN_ADMINISTRATOR_ALLOWED);

            return true;
        }

        final boolean result = user.getRoles().contains(role);
        LOGGER.trace(Constants.USER_HAS_ROLE_PATTERN, role, result);

        return result;
    }
}
