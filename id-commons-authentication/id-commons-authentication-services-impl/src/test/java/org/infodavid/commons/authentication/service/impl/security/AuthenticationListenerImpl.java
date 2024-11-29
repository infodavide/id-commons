package org.infodavid.commons.authentication.service.impl.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.infodavid.commons.service.security.AuthenticationListener;

/**
 * The Class AuthenticationListenerImpl.
 */
class AuthenticationListenerImpl implements AuthenticationListener {

    /** The names of users having requested a login. */
    private final List<String> logins = new ArrayList<>();

    /** The names of users having requested a logout. */
    private final List<String> logouts = new ArrayList<>();

    /**
     * Gets the names of users having requested a login.
     * @return the names
     */
    public List<String> getLogins() {
        return logins;
    }

    /**
     * Gets the names of users having requested a logout.
     * @return the names
     */
    public List<String> getLogouts() {
        return logouts;
    }

    @Override
    public void onLogin(final Principal principal, final Map<String, String> properties) {
        logins.add(principal.getName());
    }

    @Override
    public void onLogout(final Principal principal, final Map<String, String> properties) {
        logouts.add(principal.getName());
    }
}
