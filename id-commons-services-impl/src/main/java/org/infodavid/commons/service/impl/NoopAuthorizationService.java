package org.infodavid.commons.service.impl;

import java.io.Serializable;
import java.security.Principal;

import org.infodavid.commons.model.PersistentEntity;
import org.infodavid.commons.service.security.AuthorizationService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoopAuthorizationService implements AuthorizationService {

    @Override
    public <K extends Serializable, T extends PersistentEntity<K>> void assertAddAuthorization(final Principal principal, final Class<T> entityClass, final T entity) throws IllegalAccessException {
        // noop
    }

    @Override
    public <K extends Serializable, T extends PersistentEntity<K>> void assertDeleteAuthorization(final Principal principal, final Class<T> entityClass, final K id) throws IllegalAccessException {
        // noop
    }

    @Override
    public void assertRole(final Principal principal, final String role) throws IllegalAccessException {
        // noop
    }

    @Override
    public <K extends Serializable, T extends PersistentEntity<K>> void assertUpdateAuthorization(final Principal principal, final Class<T> entityClass, final K id) throws IllegalAccessException {
        // noop
    }

    @Override
    public <K extends Serializable, T extends PersistentEntity<K>> boolean canAdd(final Principal principal, final Class<T> entityClass, final T entity) {
        return true;
    }

    @Override
    public <K extends Serializable, T extends PersistentEntity<K>> boolean canDelete(final Principal principal, final Class<T> entityClass, final K id) {
        return true;
    }

    @Override
    public <K extends Serializable, T extends PersistentEntity<K>> boolean canEdit(final Principal principal, final Class<T> entityClass, final K id) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#getPrincipal()
     */
    @Override
    public Principal getPrincipal() {
        final SecurityContext context = SecurityContextHolder.getContext();
        Principal result = null;

        if (context == null) {
            LOGGER.debug("No security context available");

            return null;
        }

        if (context.getAuthentication() == null) {
            LOGGER.debug("No authentication available");

            return null;
        }

        LOGGER.debug("Authentication: {}", context.getAuthentication());

        if (context.getAuthentication() instanceof AnonymousAuthenticationToken) { // NOSONAR Use of pattern matching
            result = org.infodavid.commons.model.Constants.ANONYMOUS_PRINCIPAL;
        } else if (context.getAuthentication().getPrincipal() instanceof final Principal principal) { // NOSONAR Use of pattern matching
            result = principal;
        } else if (context.getAuthentication().getDetails() instanceof final Principal principal) { // NOSONAR Use of pattern matching
            result = principal;
        }

        LOGGER.debug("Current user: {}", result);

        return result;
    }

}
