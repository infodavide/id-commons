package org.infodavid.commons.impl.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.infodavid.commons.model.User;
import org.infodavid.commons.security.AuthenticationListener;

import lombok.Getter;

/**
 * The Class AuthenticationListenerImpl.
 */
class AuthenticationListenerImpl implements AuthenticationListener {

    /** The logins. */
    @Getter
    private final List<Long> logins = new ArrayList<>();

    /** The logouts. */
    @Getter
    private final List<Long> logouts = new ArrayList<>();

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
