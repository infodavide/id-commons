package org.infodavid.commons.security;

import java.util.Map;

import org.infodavid.commons.model.User;

/**
 * The listener interface for receiving authentication events. The class that is interested in processing a authentication event implements this interface, and the object created with that class is registered with a component using the component's <code>addAuthenticationListener<code> method. When the authentication event occurs, that object's appropriate method is invoked.
 */
public interface AuthenticationListener {

    /**
     * On login.
     * @param user       the user
     * @param properties the properties
     */
    void onLogin(User user, Map<String, String> properties);

    /**
     * On logout.
     * @param user       the user
     * @param properties the properties
     */
    void onLogout(User user, Map<String, String> properties);
}
