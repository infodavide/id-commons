package org.infodavid.commons.authentication.service.test;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.security.auth.login.LoginException;

import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.service.UserService;
import org.infodavid.commons.service.ConfigurationManager;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.security.AuthenticationBuilder;
import org.infodavid.commons.service.security.AuthenticationListener;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.service.security.UserPrincipal;
import org.infodavid.commons.service.test.ConfigurationManagerMock;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.base.Objects;

import lombok.Getter;

/**
 * The Class AuthenticationServiceMock.
 */
public class AuthenticationServiceMock implements AuthenticationService {

    /** The authentication builder. */
    private AuthenticationBuilder authenticationBuilder = new AuthenticationBuilder() {

        @Override
        public Authentication build(final UserPrincipal principal, final Collection<GrantedAuthority> authorities, final Date expirationDate) {
            final UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal, "");
            result.getAuthorities().addAll(authorities);

            return result;
        }

        @Override
        public boolean isExpired(final Authentication authentication) {
            return false;
        }
    };

    /** The authentications. */
    private final Map<String, Authentication> authentications = new HashMap<>();

    /** The configuration manager. */
    private final ConfigurationManager configurationManager = new ConfigurationManagerMock("authentication");

    /** The listeners. */
    @Getter
    private final LinkedHashSet<AuthenticationListener> listeners = new LinkedHashSet<>();

    /** The user service. */
    private final UserService userService;

    /**
     * Instantiates a new authentication service mock.
     * @param userService the user service
     */
    public AuthenticationServiceMock(final UserService userService) {
        this.userService = userService;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#addListener(org.infodavid.commons.service.security.AuthenticationListener)
     */
    @Override
    public void addListener(final AuthenticationListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#authenticate(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public Authentication authenticate(final String login, final String password, final Map<String, String> properties) throws IllegalAccessException, ServiceException, LoginException {
        final Optional<User> optional = userService.findByName(login);

        if (optional.isEmpty()) {
            throw new LoginException();
        }

        final User user = optional.get();

        if (Objects.equal(user.getPassword(), password)) {
            final Authentication result = new UsernamePasswordAuthenticationToken(user, password);
            authentications.put(user.getName(), result);
            result.setAuthenticated(true);

            for (final AuthenticationListener listener : listeners) {
                listener.onLogin(user, properties);
            }

            return result;
        }

        throw new LoginException();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getAuthentication(java.lang.String)
     */
    @Override
    public Optional<Authentication> getAuthentication(final String login) {
        final Authentication result = authentications.get(login);

        if (result == null) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getAuthenticationBuilder()
     */
    @Override
    public AuthenticationBuilder getAuthenticationBuilder() {
        return authenticationBuilder;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getConfigurationManager()
     */
    @Override
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getPrincipal()
     */
    @Override
    public UserPrincipal getPrincipal() {
        final SecurityContext context = SecurityContextHolder.getContext();
        UserPrincipal result = null;

        if (context == null) {
            return null;
        }

        if (context.getAuthentication() == null) {
            return null;
        }

        if (context.getAuthentication() instanceof AnonymousAuthenticationToken) { // NOSONAR Use of pattern matching
            result = org.infodavid.commons.service.Constants.ANONYMOUS_PRINCIPAL;
        } else if (context.getAuthentication().getPrincipal() instanceof final UserPrincipal principal) { // NOSONAR Use of pattern matching
            result = principal;
        } else if (context.getAuthentication().getDetails() instanceof final UserPrincipal principal) { // NOSONAR Use of pattern matching
            result = principal;
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getPrincipal(org.springframework.security.core.Authentication)
     */
    @Override
    public Optional<UserPrincipal> getPrincipal(final Authentication authentication) {
        for (final Entry<String, Authentication> entry : authentications.entrySet()) {
            if (entry.getKey().equals(authentication.getName())) {
                return Optional.of((UserPrincipal) entry.getValue().getPrincipal());
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#invalidate(org.springframework.security.core.Authentication, java.util.Map)
     */
    @Override
    public boolean invalidate(final Authentication authentication, final Map<String, String> properties) {
        final Optional<UserPrincipal> optional = getPrincipal(authentication);

        if (optional.isPresent()) {
            return invalidate(optional.get(), properties);
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#invalidate(java.security.Principal, java.util.Map)
     */
    @Override
    public boolean invalidate(final Principal principal, final Map<String, String> properties) {
        if (principal == null) {
            return false;
        }

        authentications.remove(principal.getName());

        for (final AuthenticationListener listener : listeners) {
            listener.onLogout(principal, properties);
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#invalidateAll()
     */
    @Override
    public void invalidateAll() {
        final Iterator<Authentication> ite = authentications.values().iterator();

        while (ite.hasNext()) {
            final Authentication authentication = ite.next();
            ite.remove();

            for (final AuthenticationListener listener : listeners) {
                listener.onLogout((Principal) authentication.getPrincipal(), null);
            }
        }

    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#isAuthenticated(java.security.Principal)
     */
    @Override
    public boolean isAuthenticated(final Principal principal) {
        if (principal == null) {
            return false;
        }

        return authentications.get(principal.getName()) != null;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#removeListener(org.infodavid.commons.service.security.AuthenticationListener)
     */
    @Override
    public boolean removeListener(final AuthenticationListener listener) {
        if (listener == null) {
            return false;
        }

        return listeners.remove(listener);
    }
}
