package org.infodavid.commons.service.impl;

import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.service.security.UserPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class NoopAuthorizationService.
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class NoopAuthorizationService implements AuthorizationService {

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertAddAuthorization(org.infodavid.commons.service.security.UserPrincipal, java.lang.Class, java.lang.Object)
     */
    @Override
    public void assertAddAuthorization(final UserPrincipal principal, final Class entityClass, final Object parentId) throws IllegalAccessException {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertDeleteAuthorization(java.security.Principal, java.lang.Class, java.lang.Object)
     */
    @Override
    public void assertDeleteAuthorization(final UserPrincipal principal, final Class entityClass, final Object id) throws IllegalAccessException {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertRole(java.security.Principal, java.lang.String)
     */
    @Override
    public void assertRole(final UserPrincipal principal, final String role) throws IllegalAccessException {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertUpdateAuthorization(java.security.Principal, java.lang.Class, java.lang.Object)
     */
    @Override
    public void assertUpdateAuthorization(final UserPrincipal principal, final Class entityClass, final Object id) throws IllegalAccessException {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#canAdd(org.infodavid.commons.service.security.UserPrincipal, java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean canAdd(final UserPrincipal principal, final Class entityClass, final Object parentId) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#canDelete(java.security.Principal, java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean canDelete(final UserPrincipal principal, final Class entityClass, final Object id) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#canEdit(java.security.Principal, java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean canEdit(final UserPrincipal principal, final Class entityClass, final Object id) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#getPrincipal()
     */
    @Override
    public UserPrincipal getPrincipal() {
        final SecurityContext context = SecurityContextHolder.getContext();
        UserPrincipal result = null;

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
            result = org.infodavid.commons.service.Constants.ANONYMOUS_PRINCIPAL;
        } else if (context.getAuthentication().getPrincipal() instanceof final UserPrincipal principal) { // NOSONAR Use of pattern matching
            result = principal;
        } else if (context.getAuthentication().getDetails() instanceof final UserPrincipal principal) { // NOSONAR Use of pattern matching
            result = principal;
        }

        LOGGER.debug("Current user: {}", result);

        return result;
    }
}
