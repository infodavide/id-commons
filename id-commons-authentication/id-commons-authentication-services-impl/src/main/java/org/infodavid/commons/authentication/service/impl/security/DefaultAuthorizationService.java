package org.infodavid.commons.authentication.service.impl.security;

import java.security.Principal;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.service.impl.Constants;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.impl.AbstractService;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.service.security.UserPrincipal;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultAuthorizationService.
 */
/* If necessary, declare the bean in the Spring configuration. */
@Slf4j
@SuppressWarnings("rawtypes")
public class DefaultAuthorizationService extends AbstractService implements AuthorizationService {

    /**
     * Instantiates a new default authorization service.
     * @param logger             the logger
     * @param applicationContext the application context
     */
    public DefaultAuthorizationService(final Logger logger, final ApplicationContext applicationContext) {
        super(logger, applicationContext);
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
        return getApplicationContext().getBean(AuthenticationService.class).getPrincipal();
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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Role check for user: {} and role: {}", user, role);
        }

        if (user == null) {
            LOGGER.debug(Constants.USER_IS_NULL);

            // We assume that anonymous user is not null and null user is the system itself
            return true;
        }

        if (user.getGroups() == null || user.getGroups().isEmpty()) {
            LOGGER.debug(Constants.USER_HAS_NO_ROLE_DENIED);

            return false;
        }

        for (final Group group : user.getGroups()) {
            if (group.getRoles().contains(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE)) {
                LOGGER.debug(Constants.USER_IS_AN_ADMINISTRATOR_ALLOWED);

                return true;
            }
            if (group.getRoles().contains(role)) {
                LOGGER.debug(Constants.USER_HAS_ROLE_PATTERN, role, true);

                return true;
            }
        }

        LOGGER.debug(Constants.USER_HAS_ROLE_PATTERN, role, false);

        return false;
    }
}
