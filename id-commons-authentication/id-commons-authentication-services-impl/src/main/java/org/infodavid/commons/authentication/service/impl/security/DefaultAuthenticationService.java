package org.infodavid.commons.authentication.service.impl.security;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.infodavid.commons.authentication.service.UserPrincipalImpl;
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
import org.infodavid.commons.service.security.UserPrincipal;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
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

    /**
     * The Class DefaultAuthenticationBuilder.
     */
    private static class DefaultAuthenticationBuilder implements AuthenticationBuilder {

        /*
         * (non-Javadoc)
         * @see org.infodavid.commons.service.security.AuthenticationBuilder#build(java.security.Principal, java.util.Collection, java.util.Date)
         */
        @Override
        public Authentication build(final UserPrincipal principal, final Collection<GrantedAuthority> authorities, final Date expirationDate) {
            final UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal, ((User) principal).getPassword(), authorities);
            result.setDetails(principal);

            return result;
        }

        /*
         * (non-Javadoc)
         * @see org.infodavid.commons.service.security.AuthenticationBuilder#isExpired(org.springframework.security.core.Authentication)
         */
        @Override
        public boolean isExpired(final Authentication authentication) {
            return false;
        }
    }

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

        if (authenticationBuilder == null) {
            this.authenticationBuilder = new DefaultAuthenticationBuilder();
        } else {
            this.authenticationBuilder = authenticationBuilder;
        }

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

        final UserPrincipalImpl principal = new UserPrincipalImpl(user);
        final String value = properties == null ? null : properties.get(org.infodavid.commons.authentication.model.Constants.SESSION_REMOTE_IP_ADDRESS_PROPERTY);

        if (StringUtils.isNotEmpty(value)) {
            principal.setLastIp(value);
        }

        principal.setLastConnectionDate(new Date());
        LOGGER.debug("Instantiating a new authentication object");
        final Authentication result = authenticationBuilder.build(principal, principal.getGrantedAuthorities(), Date.from(Instant.now().plus(sessionInactivityTimeout, ChronoUnit.MINUTES)));

        try {
            principal.getProperties().add(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY, PropertyType.STRING, authenticationBuilder.serialize(result));
            userDao.update(principal);
        } catch (final IOException | PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        LOGGER.info("Authentication object placed into context for user: {}", principal.getName());
        fireOnLogin(principal, properties);
        SecurityContextHolder.getContext().setAuthentication(result);

        return result;
    }

    /**
     * Fire on login.
     * @param principal  the principal
     * @param properties the properties
     */
    protected void fireOnLogin(final Principal principal, final Map<String, String> properties) {
        for (final AuthenticationListener listener : listeners) {
            try {
                listener.onLogin(principal, properties);
            } catch (final Exception e) {
                LOGGER.warn("Listener cannot process login event: {}", e.getMessage());
            }
        }
    }

    /**
     * Fire on logout.
     * @param principal  the principal
     * @param properties the properties
     */
    protected void fireOnLogout(final Principal principal, final Map<String, String> properties) {
        for (final AuthenticationListener listener : listeners) {
            try {
                listener.onLogout(principal, properties);
            } catch (final Exception e) {
                LOGGER.warn("Listener cannot process logout event", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getAuthentication(java.lang.String)
     */
    @Override
    public Optional<Authentication> getAuthentication(final String login) throws ServiceException {
        if (StringUtils.isEmpty(login)) {
            return Optional.empty();
        }

        Optional<User> optional;

        try {
            optional = userDao.findByName(login);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (optional.isPresent()) {
            return Optional.ofNullable(getAuthentication(optional.get()));
        }

        return Optional.empty();
    }

    /**
     * Gets the authentication.
     * @param user the user
     * @return the authentication
     * @throws ServiceException the service exception
     */
    protected Authentication getAuthentication(final User user) throws ServiceException {
        if (user == null || StringUtils.isEmpty(user.getName())) {
            return null;
        }

        AuthenticationBuilder builder = authenticationBuilder;

        if (builder == null) {
            builder = new AuthenticationBuilder() {

                @Override
                public Authentication build(final UserPrincipal principal, final Collection<GrantedAuthority> authorities, final Date expirationDate) {
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
            throw new ServiceException("Cannot deserialize authentication", e);
        }
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
            LOGGER.debug("No security context available");

            return null;
        }

        if (context.getAuthentication() == null) {
            LOGGER.debug("No authentication available");

            return null;
        }

        LOGGER.debug("Authentication: {}", context.getAuthentication());

        if (context.getAuthentication() instanceof AnonymousAuthenticationToken) { // NOSONAR Use of pattern matching
            result = org.infodavid.commons.service.Constants.ANONYMOUS_PRINCIPAL;
        } else if (context.getAuthentication().getPrincipal() instanceof final UserPrincipal principal) { // NOSONAR Use of pattern matching
            result = principal;
        } else if (context.getAuthentication().getDetails() instanceof final UserPrincipal principal) { // NOSONAR Use of pattern matching
            result = principal;
        }

        LOGGER.debug("Current user: {}", result);

        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#getPrincipal(org.springframework.security.core.Authentication)
     */
    @Override
    public Optional<UserPrincipal> getPrincipal(final Authentication authentication) throws ServiceException {
        if (authentication == null || !(authentication.getCredentials() instanceof String)) {
            throw new IllegalArgumentException(Constants.GIVEN_AUTHENTICATION_IS_INVALID);
        }

        LOGGER.debug("Retrieving principal for authentication: {}", authentication);
        Optional<UserPrincipal> result = Optional.empty();

        if (authentication instanceof AnonymousAuthenticationToken) { // NOSONAR Use of pattern matching
            result = Optional.of(org.infodavid.commons.service.Constants.ANONYMOUS_PRINCIPAL);
        } else if (authentication.getPrincipal() instanceof final UserPrincipal principal) { // NOSONAR Use of pattern matching
            result = Optional.of(principal);
        } else if (authentication.getDetails() instanceof final UserPrincipal principal) { // NOSONAR Use of pattern matching
            result = Optional.of(principal);
        }

        if (result.isEmpty()) {
            Optional<User> optional;

            try {
                optional = userDao.findByProperty(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY, authenticationBuilder.serialize(authentication));
            } catch (PersistenceException | IOException e) {
                throw new ServiceException("Cannot serialize authentication", e);
            }

            if (optional.isEmpty()) {
                return Optional.empty();
            }

            result = Optional.of(new UserPrincipalImpl(optional.get()));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.security.AuthenticationService#invalidate(org.springframework.security.core.Authentication, java.util.Map)
     */
    @Override
    public boolean invalidate(final Authentication authentication, final Map<String, String> properties) throws ServiceException {
        if (authentication == null) {
            return false;
        }

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
            try {
                final Authentication authentication = getAuthentication(user);

                if (authentication == null || authenticationBuilder.isExpired(authentication)) {
                    user.getProperties().remove(org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE, org.infodavid.commons.authentication.model.Constants.SESSION_TOKEN_PROPERTY);
                    userDao.update(user);
                }

                invalidate(user, Collections.emptyMap());
            } catch (final ServiceException e) {
                LOGGER.debug("Cannot invalidate user", e);
            }
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
