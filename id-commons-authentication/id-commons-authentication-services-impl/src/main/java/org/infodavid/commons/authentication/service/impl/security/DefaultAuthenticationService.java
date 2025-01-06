package org.infodavid.commons.authentication.service.impl.security;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.authentication.service.impl.Constants;
import org.infodavid.commons.model.AbstractProperty;
import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.service.ConfigurationManager;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.impl.AbstractService;
import org.infodavid.commons.service.impl.TransactionUtils;
import org.infodavid.commons.service.listener.PropertyChangedListener;
import org.infodavid.commons.service.security.AuthenticationBuilder;
import org.infodavid.commons.service.security.AuthenticationListener;
import org.infodavid.commons.service.security.AuthenticationService;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.PersistenceException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultAuthenticationService.<br>
 * Keep this class abstract to make it optional for the projects using this module.<br>
 * To use this service, the project must extends this class and add the Spring annotation(s).
 */
/* If necessary, declare the bean in the Spring configuration. */
@Transactional(readOnly = true)
@Slf4j
public class DefaultAuthenticationService extends AbstractService implements AuthenticationService, PropertyChangedListener, InitializingBean {

    /** The authentication builder. */
    @Getter
    private final AuthenticationBuilder authenticationBuilder;

    /** The configuration manager. */
    @Getter
    private final ConfigurationManager configurationManager;

    /** The listeners. */
    @Getter
    private final Set<AuthenticationListener> listeners = new HashSet<>();

    /** The session inactivity timeout. */
    @Getter
    private short sessionInactivityTimeout = org.infodavid.commons.authentication.model.Constants.DEFAULT_SESSION_INACTIVITY_TIMEOUT;

    /** The user data access object. */
    private final UserDao userDao;

    /**
     * Instantiates a new authentication service.
     * @param logger                the logger
     * @param applicationContext    the application context
     * @param userDao               the data access object
     * @param authenticationBuilder the authentication builder
     * @param configurationManager  the configuration manager
     */
    public DefaultAuthenticationService(final Logger logger, final ApplicationContext applicationContext, final UserDao userDao, final AuthenticationBuilder authenticationBuilder, final ConfigurationManager configurationManager) {
        super(logger, applicationContext);
        this.authenticationBuilder = authenticationBuilder;
        this.configurationManager = configurationManager;
        this.userDao = userDao;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#addListener(org.infodavid.commons.authentication.security.AuthenticationListener)
     */
    @Override
    public void addListener(final AuthenticationListener listener) {
        if (listener == null) {
            return;
        }

        getLogger().debug("Registering listener: {}", listener);
        listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws ServiceException, IllegalAccessException, SQLException {
        // Caution: @Transactionnal on afterPropertiesSet and PostConstruct method is not evaluated
        LOGGER.debug("Initializing authentication service");

        TransactionUtils.doInTransaction("Checking authentication properties", LOGGER, getApplicationContext(), () -> {
            final Optional<ConfigurationProperty> found = configurationManager.findByName(org.infodavid.commons.authentication.model.Constants.SESSION_INACTIVITY_TIMEOUT_PROPERTY);

            if (found.isEmpty()) {
                final ConfigurationProperty property = new ConfigurationProperty();
                property.setDeletable(false);
                property.setReadOnly(false);
                property.setScope(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE);
                property.setName(org.infodavid.commons.authentication.model.Constants.SESSION_INACTIVITY_TIMEOUT_PROPERTY);
                property.setType(PropertyType.INTEGER);
                property.setLabel("Session inactivity timeout (minutes)");
                property.setValue(org.infodavid.commons.authentication.model.Constants.DEFAULT_SESSION_INACTIVITY_TIMEOUT);
                configurationManager.add(property);
            } else {
                final ConfigurationProperty property = found.get();

                // Ensure value is set
                if (StringUtils.isEmpty(property.getValue())) {
                    property.setValue(org.infodavid.commons.authentication.model.Constants.DEFAULT_SESSION_INACTIVITY_TIMEOUT);
                }

                sessionInactivityTimeout = Short.parseShort(property.getValue());
                property.setDeletable(false);
                property.setReadOnly(false);
                property.setType(PropertyType.INTEGER);
                configurationManager.update(property);
            }

            return null;
        });
        // We have only one property, listener can be attached before adding or updating it
        configurationManager.addListener(this);
        LOGGER.debug("Authentication service initialized");
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#authenticate(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Authentication authenticate(final String login, final String password, final Map<String, String> properties) throws IllegalAccessException, ServiceException, LoginException {
        SecurityContextHolder.clearContext();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            throw new IllegalAccessException("Username and password are required");
        }

        if (login.indexOf('\'') != -1) {
            throw new IllegalAccessException(Constants.INVALID_USERNAME);
        }

        LOGGER.debug("Trying authentication of user: {}", login);
        User user = null;

        try {
            Optional<User> optional = userDao.findByName(login);

            if (!optional.isPresent() && StringUtils.isNumeric(login)) {
                optional = userDao.findById(Long.valueOf(login));
            }

            if (optional.isPresent()) {
                user = optional.get();
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (user == null) {
            throw new BadCredentialsException(Constants.INVALID_USERNAME_OR_PASSWORD);
        }
        if (StringUtils.isEmpty(user.getPassword())) { // NOSONAR
            LOGGER.warn("Password is empty for user: {}", user.getName());

            throw new BadCredentialsException(Constants.INVALID_USERNAME_OR_PASSWORD);
        }
        if (!user.getPassword().equalsIgnoreCase(password)) { // NOSONAR
            LOGGER.warn("Password is wrong for user: {} ({})", user.getName(), password);

            throw new BadCredentialsException(Constants.INVALID_USERNAME_OR_PASSWORD);
        }

        if (user.isExpired()) {
            throw new AccountExpiredException("User account expired: " + user.getName());
        }

        if (user.isLocked()) {
            throw new LockedException("User account locked: " + user.getName());
        }

        LOGGER.info("Authentication success for user: {}", user.getName());

        final User updated = new User(user);
        final String value = properties == null ? null : properties.get(org.infodavid.commons.authentication.model.Constants.SESSION_REMOTE_IP_ADDRESS_PROPERTY);

        if (StringUtils.isNotEmpty(value)) {
            updated.setLastIp(value);
        }

        updated.setConnectionsCount(updated.getConnectionsCount() + 1);
        updated.setLastConnectionDate(new Date());

        LOGGER.trace("Instantiating a new authentication");
        final Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getRoles() != null) {
            user.getRoles().forEach(v -> authorities.add(new SimpleGrantedAuthority(v)));
        }

        AuthenticationBuilder builder = authenticationBuilder;

        if (builder == null) {
            builder = new AuthenticationBuilder() {

                @SuppressWarnings("hiding")
                @Override
                public Authentication build(final Principal principal, final Collection<GrantedAuthority> authorities, final Date expirationDate) {
                    final UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal, password, authorities);
                    result.setDetails(principal);

                    return result;
                }

                @Override
                public boolean isExpired(final Authentication authentication) {
                    return false;
                }
            };
        }

        final Authentication result = builder.build(user, authorities, Date.from(Instant.now().plus(sessionInactivityTimeout, ChronoUnit.MINUTES)));

        try {
            updated.getProperties().add(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY, PropertyType.STRING, builder.serialize(result));
            userDao.update(updated);
            user = updated;
        } catch (final IOException | PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        fireOnLogin(user, properties);
        SecurityContextHolder.getContext().setAuthentication(result);

        return result;
    }

    /**
     * Fire on login.
     * @param user       the user
     * @param properties the properties
     */
    protected void fireOnLogin(final User user, final Map<String, String> properties) {
        for (final AuthenticationListener listener : listeners) {
            try {
                listener.onLogin(user, properties);
            } catch (final Exception e) {
                LOGGER.warn("Listener cannot process login event: {}", e.getMessage());
            }
        }
    }

    /**
     * Fire on logout.
     * @param user       the user
     * @param properties the properties
     */
    protected void fireOnLogout(final User user, final Map<String, String> properties) {
        for (final AuthenticationListener listener : listeners) {
            try {
                listener.onLogout(user, properties);
            } catch (final Exception e) {
                LOGGER.warn("Listener cannot process logout event", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getAuthenticated()
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Collection<Principal> getAuthenticated() {
        final Collection<Principal> results = new HashSet<>();

        for (final User user : userDao.findHavingProperty(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY, Pageable.unpaged())) {
            final Authentication authentication = getAuthentication(user);

            if (authentication == null || authenticationBuilder.isExpired(authentication)) {
                user.getProperties().remove(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY);
                userDao.update(user);
            }

            results.add(user);

        }

        return results;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getAuthentication(java.lang.String)
     */
    @Override
    public Authentication getAuthentication(final String login) throws ServiceException {
        Optional<User> optional;

        try {
            optional = userDao.findByName(login);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (optional.isPresent()) {
            return getAuthentication(optional.get());
        }

        return null;
    }

    /**
     * Gets the authentication.
     * @param user the user
     * @return the authentication
     */
    public Authentication getAuthentication(final User user) {
        AuthenticationBuilder builder = authenticationBuilder;

        if (builder == null) {
            builder = new AuthenticationBuilder() {

                @Override
                public Authentication build(final Principal principal, final Collection<GrantedAuthority> authorities, final Date expirationDate) {
                    return null;
                }

                @Override
                public boolean isExpired(final Authentication authentication) {
                    return false;
                }
            };
        }

        try {
            return builder.deserialize(user.getProperties().getOrDefault(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY, StringUtils.EMPTY));
        } catch (final IOException e) {
            LOGGER.warn("Cannot deserialize authentication: {}", e.getMessage());
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getPrincipal(org.springframework.security.core.Authentication)
     */
    @Override
    public Principal getPrincipal(final Authentication authentication) {
        if (authentication == null || !(authentication.getCredentials() instanceof String)) {
            throw new IllegalArgumentException(Constants.GIVEN_AUTHENTICATION_IS_INVALID);
        }

        LOGGER.trace("Retrieving principal for authentication: {}", authentication);
        final Optional<User> optional = userDao.findByProperty(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY, (String) authentication.getCredentials());

        if (optional.isEmpty()) {
            return null;
        }

        return optional.get();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#invalidate(org.springframework.security.core.Authentication, java.util.Map)
     */
    @Override
    public void invalidate(final Authentication authentication, final Map<String, String> properties) throws ServiceException {
        if (authentication == null) {
            return;
        }

        final Principal principal = getPrincipal(authentication);

        if (principal != null) {
            invalidate(principal, properties);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#invalidate(java.security.Principal, java.util.Map)
     */
    @Override
    public boolean invalidate(final Principal principal, final Map<String, String> properties) {
        if (principal == null || StringUtils.isEmpty(principal.getName())) {
            return false;
        }

        LOGGER.warn("Invalidating authentication for principal: {}", principal.getName());
        final Optional<User> optional = userDao.findByName(principal.getName());

        if (optional.isEmpty()) {
            return false;
        }

        final User user = optional.get();
        user.getProperties().remove(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY);

        try {
            userDao.update(user);
        } catch (final PersistenceException e) {
            LOGGER.warn("Cannot update user", e);
        }

        fireOnLogout(user, properties);

        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#invalidateAll()
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void invalidateAll() {
        for (final User user : userDao.findHavingProperty(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY, Pageable.unpaged())) {
            final Authentication authentication = getAuthentication(user);

            if (authentication == null || authenticationBuilder.isExpired(authentication)) {
                user.getProperties().remove(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY);
                userDao.update(user);
            }

            invalidate(user, Collections.emptyMap());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#isAuthenticated(java.security.Principal)
     */
    @Override
    public boolean isAuthenticated(final Principal principal) {
        if (principal == null || StringUtils.isEmpty(principal.getName())) {
            throw new IllegalArgumentException("Principal is invalid");
        }

        final Optional<User> optional = userDao.findByName(principal.getName());

        if (optional.isEmpty()) {
            return false;
        }

        final User user = optional.get();

        return StringUtils.isNotEmpty(user.getProperties().getOrDefault(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY, StringUtils.EMPTY));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.listener.PropertyChangedListener#propertyChanged(org.infodavid.commons.model.AbstractProperty[])
     */
    @SuppressWarnings("rawtypes")
    @Override
    public synchronized void propertyChanged(final AbstractProperty... properties) {
        for (final AbstractProperty property : AbstractProperty.filterByScope(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, properties)) {
            if (org.infodavid.commons.authentication.model.Constants.SESSION_INACTIVITY_TIMEOUT_PROPERTY.equals(property.getName()) && StringUtils.isNumeric(property.getValue())) {
                int value = Integer.parseInt(property.getValue());

                if (value > 0) {
                    value += 1;
                }

                sessionInactivityTimeout = (short) value;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#removeListener(org.infodavid.commons.authentication.security.AuthenticationListener)
     */
    @Override
    public boolean removeListener(final AuthenticationListener listener) {
        if (listener == null) {
            return false;
        }

        LOGGER.debug("Unregistering listener: {}", listener);

        return listeners.remove(listener);
    }
}
