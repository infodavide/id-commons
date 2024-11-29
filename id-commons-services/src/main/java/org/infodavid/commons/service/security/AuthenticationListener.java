package org.infodavid.commons.service.security;

import java.security.Principal;
import java.util.Map;

/**
 * The listener interface for receiving authentication events. The class that is interested in processing a authentication event implements this interface, and the object created with that class is registered with a component using the component's <code>addAuthenticationListener<code> method. When the authentication event occurs, that object's appropriate method is invoked.
 */
public interface AuthenticationListener {

    /**
     * On login.
     * @param principal  the principal
     * @param properties the properties
     */
    void onLogin(Principal principal, Map<String, String> properties);

    /**
     * On logout.
     * @param principal  the principal
     * @param properties the properties
     */
    void onLogout(Principal principal, Map<String, String> properties);
}
