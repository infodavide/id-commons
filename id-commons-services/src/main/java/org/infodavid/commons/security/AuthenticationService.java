package org.infodavid.commons.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.infodavid.commons.model.User;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
     * @ßee {@link UsernamePasswordAuthenticationToken}
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     * @throws LoginException         the login exception
     */
    Authentication authenticate(String login, String password, Map<String, String> properties) throws IllegalAccessException, ServiceException, LoginException;

    /**
     * Authenticate.
     * @param login      the login
     * @param password   the password
     * @param properties the properties (like remote IP address, etc.)
     * @param builder    the authentication builder
     * @return the authentication associated to the user
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     * @throws LoginException         the login exception
     */
    Authentication authenticate(String login, String password, Map<String, String> properties, AuthenticationBuilder builder) throws IllegalAccessException, ServiceException, LoginException;

    /**
     * Checks for permission based on role.
     * @param role the role
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     */
    void checkRole(String role) throws IllegalAccessException, ServiceException;

    /**
     * Gets the currently authenticated users.
     * @return the identifiers
     */
    Collection<User> getAuthenticatedUsers();

    /**
     * Gets the authentication token.
     * @param userId the user identifier
     * @return the authentication
     */
    Authentication getAuthentication(Long userId);

    /**
     * Gets the authentication token associated to the user identifier.
     * @param user the user
     * @return the authentication
     */
    Authentication getAuthentication(User user);

    /**
     * Gets the listeners.
     * @return the listeners
     */
    Set<AuthenticationListener> getListeners();

    /**
     * Gets the user associated to the current security context.
     * @return the user
     * @throws ServiceException the service exception
     */
    User getUser() throws ServiceException;

    /**
     * Gets the user.
     * @param authentication the authentication
     * @return the user
     */
    User getUser(Authentication authentication);

    /**
     * Checks for permission.
     * @param role the role
     * @return true, if successful
     * @throws ServiceException the service exception
     */
    boolean hasRole(String role) throws ServiceException;

    /**
     * Invalidate and logout the authenticated user.
     * @param authentication the authentication
     * @param properties     the properties (like remote IP address, etc.)
     */
    void invalidate(Authentication authentication, Map<String, String> properties);

    /**
     * Invalidate and logout the authenticated user.
     * @param user       the authenticated user
     * @param properties the properties (like remote IP address, etc.)
     * @return true, if successful
     */
    boolean invalidate(User user, Map<String, String> properties);

    /**
     * Invalidate and logout all authenticated users.
     */
    void invalidateAll();

    /**
     * Checks if is authenticated.
     * @param user the user
     * @return true, if is authenticated
     */
    boolean isAuthenticated(User user);

    /**
     * Removes the listener.
     * @param listener the listener
     * @return true, if successful
     */
    boolean removeListener(AuthenticationListener listener);
}
