package org.infodavid.commons.authentication.service.test;

import java.security.Principal;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.service.security.UserPrincipal;
import org.springframework.context.ApplicationContext;

/**
 * The Class AuthorizationServiceMock.
 */
@SuppressWarnings("rawtypes")
public class AuthorizationServiceMock implements AuthorizationService {

    /** The application context. */
    private ApplicationContext applicationContext;

    /**
     * Instantiates a new default authorization service.
     * @param applicationContext the application context
     */
    public AuthorizationServiceMock(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertAddAuthorization(org.infodavid.commons.service.security.UserPrincipal, java.lang.Class, java.lang.Object)
     */
    @Override
    public void assertAddAuthorization(final UserPrincipal principal, final Class entityClass, final Object parentId) throws IllegalAccessException {
        assertRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertDeleteAuthorization(org.infodavid.commons.service.security.UserPrincipal, java.lang.Class, java.lang.Object)
     */
    @Override
    public void assertDeleteAuthorization(final UserPrincipal principal, final Class entityClass, final Object id) throws IllegalAccessException {
        assertRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertRole(org.infodavid.commons.service.security.UserPrincipal, java.lang.String)
     */
    @Override
    public void assertRole(final UserPrincipal principal, final String role) throws IllegalAccessException {
        if (!hasRole(principal, role)) {
            throw new IllegalAccessException("User does not have role: " + role);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#assertUpdateAuthorization(org.infodavid.commons.service.security.UserPrincipal, java.lang.Class, java.lang.Object)
     */
    @Override
    public void assertUpdateAuthorization(final UserPrincipal principal, final Class entityClass, final Object id) throws IllegalAccessException {
        assertRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#canAdd(org.infodavid.commons.service.security.UserPrincipal, java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean canAdd(final UserPrincipal principal, final Class entityClass, final Object parentId) {
        return hasRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#canDelete(org.infodavid.commons.service.security.UserPrincipal, java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean canDelete(final UserPrincipal principal, final Class entityClass, final Object id) {
        return hasRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#canEdit(org.infodavid.commons.service.security.UserPrincipal, java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean canEdit(final UserPrincipal principal, final Class entityClass, final Object id) {
        return hasRole(principal, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthorizationService#getPrincipal()
     */
    @Override
    public UserPrincipal getPrincipal() throws ServiceException {
        return applicationContext.getBean(AuthenticationService.class).getPrincipal();
    }

    /**
     * Checks for role.
     * @param principal the principal
     * @param role      the role
     * @return true, if successful
     */
    public boolean hasRole(final Principal principal, final String role) {
        final User user = (User) principal;

        if (user == null) {
            // We assume that anonymous user is not null and null user is the system itself
            return true;
        }

        if (user.getGroups() == null || user.getGroups().isEmpty()) {
            return false;
        }

        for (final Group group : user.getGroups()) {
            if (group.getRoles().contains(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE)) {
                return true;
            }
            if (group.getRoles().contains(role)) {
                return true;
            }
        }

        return false;
    }
}
