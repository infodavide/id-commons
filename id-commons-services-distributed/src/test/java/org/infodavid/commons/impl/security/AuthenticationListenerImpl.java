package org.infodavid.commons.impl.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.infodavid.commons.model.User;
import org.infodavid.commons.security.AuthenticationListener;

/**
 * The Class AuthenticationListenerImpl.
 */
class AuthenticationListenerImpl implements AuthenticationListener {

    /** The logins. */
    private final List<Long> logins = new ArrayList<>();

    /** The logouts. */
    private final List<Long> logouts = new ArrayList<>();

    /**
     * Gets the logins.
     * @return the logins
     */
    public List<Long> getLogins() {
        return logins;
    }

    /**
     * Gets the logouts.
     * @return the logouts
     */
    public List<Long> getLogouts() {
        return logouts;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationListener#onLogin(org.infodavid.model.User, java.util.Map)
     */
    @Override
    public void onLogin(final User user, final Map<String, String> properties) {
        logins.add(user.getId());
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationListener#onLogout(org.infodavid.model.User, java.util.Map)
     */
    @Override
    public void onLogout(final User user, final Map<String, String> properties) {
        logouts.add(user.getId());
    }
}
