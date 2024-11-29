package org.infodavid.commons.service.security;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.infodavid.commons.service.ConfigurationManager;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.security.core.Authentication;

/**
 * The Class AuthenticationService.
 */
public interface AuthenticationService {

    /**
     * Adds the listener.
     * @param listener the listener
     */
    void addListener(AuthenticationListener listener);

    /**
     * Authenticate.
     * @param login      the login
     * @param password   the password
     * @param properties the properties (like remote IP address, etc.)
     * @return the authentication associated to the user
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     * @throws LoginException         the login exception
     */
    Authentication authenticate(String login, String password, Map<String, String> properties) throws IllegalAccessException, ServiceException, LoginException;

    /**
     * Gets the currently authenticated users.
     * @return the identifiers
     */
    Collection<Principal> getAuthenticated();

    /**
     * Gets the user associated to the current security context.
     * @param userId the user identifier
     * @return the login
     * @throws ServiceException
     */
    Authentication getAuthentication(String login) throws ServiceException;

    /**
     * Gets the authentication builder.
     * @return the builder
     */
    AuthenticationBuilder getAuthenticationBuilder();

    /**
     * Gets the configuration manager.
     * @return the configuration manager
     */
    ConfigurationManager getConfigurationManager();

    /**
     * Gets the listeners.
     * @return the listeners
     */
    Set<AuthenticationListener> getListeners();

    /**
     * Gets the principal associated to the authentication.
     * @param authentication the authentication
     * @return the principal
     */
    Principal getPrincipal(Authentication authentication);

    /**
     * Invalidate and logout the authenticated user.
     * @param authentication the authentication
     * @param properties     the properties (like remote IP address, etc.)
     * @throws ServiceException the service exception
     */
    void invalidate(Authentication authentication, Map<String, String> properties) throws ServiceException;

    /**
     * Invalidate and logout the authenticated user.
     * @param principal  the authenticated principal
     * @param properties the properties (like remote IP address, etc.)
     * @return true, if successful
     */
    boolean invalidate(Principal principal, Map<String, String> properties);

    /**
     * Invalidate and logout all authenticated users.
     */
    void invalidateAll();

    /**
     * Checks if is authenticated.
     * @param principal the principal
     * @return true, if is authenticated
     */
    boolean isAuthenticated(Principal principal);

    /**
     * Removes the listener.
     * @param listener the listener
     * @return true, if successful
     */
    boolean removeListener(AuthenticationListener listener);
}
