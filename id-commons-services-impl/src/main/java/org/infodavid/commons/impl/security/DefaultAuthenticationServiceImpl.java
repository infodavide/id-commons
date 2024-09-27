package org.infodavid.commons.impl.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.infodavid.commons.impl.service.AbstractService;
import org.infodavid.commons.impl.service.ApplicationContextProvider;
import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.UserDao;
import org.infodavid.commons.security.AnonymousAuthenticationImpl;
import org.infodavid.commons.security.AuthenticationBuilder;
import org.infodavid.commons.security.AuthenticationListener;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.listener.ApplicationPropertyChangedListener;
import org.infodavid.commons.util.concurrency.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import jakarta.persistence.PersistenceException;

/**
 * The Class DefaultAuthenticationServiceImpl.<br>
 * Keep this class abstract to make it optional for the projects using this module.<br>
 * To use this service, the project must extends this class and add the Spring annotation(s).
 */
/* If necessary, declare the bean in the Spring configuration. */
@Transactional
public class DefaultAuthenticationServiceImpl extends AbstractService implements AuthenticationService, ApplicationPropertyChangedListener, InitializingBean {

    /**
     * The Class Listener.
     */
    protected class Listener implements RemovalListener<Long, Authentication> {

        /*
         * (non-javadoc)
         * @see com.github.benmanes.caffeine.cache.RemovalListener#onRemoval(java.lang.Object, java.lang.Object, com.github.benmanes.caffeine.cache.RemovalCause)
         */
        @Override
        public void onRemoval(@Nullable final Long key, @Nullable final Authentication value, @NonNull final RemovalCause cause) {
            if (key == null || value == null) {
                return;
            }

            getLogger().debug("Removing cached entry associated to user name: {}", value.getName());
            final Optional<User> optional = userDao.findById(key);

            if (optional.isPresent()) {
                fireOnLogout(optional.get(), Collections.emptyMap());
            }
        }
    }

    /** The Constant CACHE_SIZE. */
    private static final String CACHE_SIZE = "Cache size: {}";

    /** The Constant GIVEN_AUTHENTICATION_IS_INVALID. */
    private static final String GIVEN_AUTHENTICATION_IS_INVALID = "Given authentication is invalid";

    /** The Constant INVALID_USERNAME. */
    private static final String INVALID_USERNAME = "Invalid username";

    /** The Constant INVALID_USERNAME_OR_PASSWORD. */
    private static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password"; // NOSONAR Message

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthenticationServiceImpl.class);

    /** The Constant USER_HAS_NOT_THE_ROLE. */
    public static final String USER_HAS_NOT_THE_ROLE = "User has not the role: %s";

    /** The Constant USER_HAS_ROLE. */
    public static final String USER_HAS_ROLE = "User has role: {}";

    /** The authentication cache. */
    private Cache<Long, Authentication> cache;

    /** The listeners. */
    private final Set<AuthenticationListener> listeners = new HashSet<>();

    /** The lock. */
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    /** The removal listener. */
    protected final Listener removalListener;

    /** The user DAO. */
    protected final UserDao userDao;

    /**
     * Instantiates a new authentication service.
     * @param applicationContextProvider the application context provider
     * @param userDao                    the user data access object
     */
    public DefaultAuthenticationServiceImpl(final ApplicationContextProvider applicationContextProvider, final UserDao userDao) {
        super(applicationContextProvider);
        this.userDao = userDao;
        removalListener = new Listener();
        buildCache(org.infodavid.commons.service.Constants.DEFAULT_SESSION_INACTIVITY_TIMEOUT, TimeUnit.MINUTES);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#addListener(org.infodavid.security.AuthenticationListener)
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
     * (non-javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws ServiceException, IllegalAccessException {
        getLogger().debug("Initializing authentication service");
        final ApplicationService applicationService = getApplicationContext().getBean(ApplicationService.class);
        // We have only one property, listener can be attached before adding or updating it
        applicationService.addListener(this);
        final Page<ApplicationProperty> found = applicationService.findByName(org.infodavid.commons.service.Constants.SESSION_INACTIVITY_TIMEOUT_PROPERTY, Pageable.unpaged());

        if (found.isEmpty()) {
            final ApplicationProperty property = new ApplicationProperty();
            property.setDeletable(false);
            property.setReadOnly(false);
            property.setName(org.infodavid.commons.service.Constants.SESSION_INACTIVITY_TIMEOUT_PROPERTY);
            property.setType(PropertyType.INTEGER);
            property.setLabel("Session inactivity timeout (minutes)");
            property.setValue(org.infodavid.commons.service.Constants.DEFAULT_SESSION_INACTIVITY_TIMEOUT);
            applicationService.add(property);
        } else {
            final ApplicationProperty property = found.getContent().get(0);

            // Ensure value is set
            if (StringUtils.isEmpty(property.getValue())) {
                property.setValue(org.infodavid.commons.service.Constants.DEFAULT_SESSION_INACTIVITY_TIMEOUT);
            }

            property.setDeletable(false);
            property.setReadOnly(false);
            property.setType(PropertyType.INTEGER);
            applicationService.update(property);
        }

        getLogger().debug("Authentication service initialized");
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.security.AuthenticationService#authenticate(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Authentication authenticate(final String login, final String password, final Map<String, String> properties) throws IllegalAccessException, ServiceException, LoginException {
        return doAuthenticate(login, password, properties, null);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.security.AuthenticationService#authenticate(java.lang.String, java.lang.String, java.util.Map, org.infodavid.commons.security.AuthenticationBuilder)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Authentication authenticate(final String login, final String password, final Map<String, String> properties, final AuthenticationBuilder builder) throws IllegalAccessException, ServiceException, LoginException {
        return doAuthenticate(login, password, properties, builder);
    }

    /**
     * Builds the cache with the given expiration duration.
     * @param expireAfterAccessDuration the expire after access duration
     * @param unit                      the unit associated to the specified duration
     */
    protected void buildCache(final int expireAfterAccessDuration, final TimeUnit unit) {
        lock.writeLock().lock();
        getLogger().debug("Initializing...");

        try {
            Map<Long, Authentication> existing;

            if (cache == null) {
                existing = Collections.emptyMap();
            } else {
                existing = new HashMap<>(cache.asMap());
            }

            final Caffeine<Long, Authentication> builder = Caffeine.newBuilder().maximumSize(100).removalListener(removalListener);

            if (expireAfterAccessDuration > 0) {
                getLogger().info("Building cache using expire after access duration: {} {}", String.valueOf(expireAfterAccessDuration), StringUtils.lowerCase(unit.name())); // NOSONAR Always written
                builder.expireAfterAccess(expireAfterAccessDuration, unit);
            } else {
                getLogger().info("Building cache without expiration");
            }

            cache = builder.build();
            existing.entrySet().forEach(e -> cache.put(e.getKey(), e.getValue()));
            getLogger().debug("Initialized");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#checkRole(java.lang.String)
     */
    @Override
    public void checkRole(final String role) throws IllegalAccessException, ServiceException {
        if (hasRole(role)) {
            getLogger().trace(USER_HAS_ROLE, role);

            return;
        }

        throw new IllegalAccessException(String.format(USER_HAS_NOT_THE_ROLE, role));
    }

    /**
     * Do authenticate.
     * @param login      the login
     * @param password   the password
     * @param properties the properties
     * @param builder    the builder
     * @return the authentication
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     * @throws LoginException         the login exception
     */
    protected Authentication doAuthenticate(final String login, final String password, final Map<String, String> properties, final AuthenticationBuilder builder) throws IllegalAccessException, ServiceException, LoginException {
        SecurityContextHolder.getContext().setAuthentication(AnonymousAuthenticationImpl.INSTANCE);

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            throw new IllegalAccessException("Username and password are required");
        }

        if (login.indexOf('\'') != -1) {
            throw new IllegalAccessException(INVALID_USERNAME);
        }

        getLogger().debug("Trying authentication of user: {}", login);
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
            throw new BadCredentialsException(INVALID_USERNAME_OR_PASSWORD);
        }
        if (StringUtils.isEmpty(user.getPassword())) { // NOSONAR
            getLogger().warn("Password is empty for user: {}", user.getName());

            throw new BadCredentialsException(INVALID_USERNAME_OR_PASSWORD);
        }
        if (!user.getPassword().equalsIgnoreCase(password)) { // NOSONAR
            getLogger().warn("Password is wrong for user: {} ({})", user.getName(), password);

            throw new BadCredentialsException(INVALID_USERNAME_OR_PASSWORD);
        }

        if (user.isExpired()) {
            throw new AccountExpiredException("User account expired: " + user.getName());
        }

        if (user.isLocked()) {
            throw new LockedException("User account locked: " + user.getName());
        }

        Authentication result = getAuthentication(user);

        if (result == null) {
            getLogger().info("Authentication success for user: {}", user.getName());

            final User updated = new User(user);
            final String value = properties == null ? null : properties.get(org.infodavid.commons.service.Constants.IP_ADDRESS_PROPERTY);

            if (StringUtils.isNotEmpty(value)) {
                updated.setLastIp(value);
            }

            updated.setConnectionsCount(updated.getConnectionsCount() + 1);
            updated.setLastConnectionDate(new Date());

            try {
                userDao.update(updated);
                user = updated;
            } catch (final PersistenceException e) {
                throw new ServiceException(ExceptionUtils.getRootCause(e));
            }

            getLogger().trace("Instantiating a new authentication");
            final Collection<GrantedAuthority> authorities = new ArrayList<>();

            if (user.getRole() != null) {
                authorities.add(new SimpleGrantedAuthority(user.getRole()));
            }

            if (builder == null) {
                result = new UsernamePasswordAuthenticationToken(user, password, authorities);
                ((UsernamePasswordAuthenticationToken) result).setDetails(user);
            } else {
                result = builder.build(user, authorities, null);
            }

            lock.readLock().lock();

            try {
                // caches and force use of transaction
                getLogger().debug("Caching authentication: {} for user: {}", result, user.getName());
                cache.put(user.getId(), result);

                if (getLogger().isTraceEnabled()) {
                    getLogger().debug(CACHE_SIZE, String.valueOf(cache.estimatedSize()));
                }

                fireOnLogin(user, properties);
            } finally {
                lock.readLock().unlock();
            }
        } else {
            getLogger().debug("User already authenticated: {}", user.getName());
        }

        SecurityContextHolder.getContext().setAuthentication(result);

        return result;
    }

    /**
     * Fire on login.
     * @param listener   the listener
     * @param user       the user
     * @param properties the properties
     */
    protected void fireOnLogin(final User user, final Map<String, String> properties) {
        for (final AuthenticationListener listener : listeners) {
            try {
                listener.onLogin(user, properties);
            } catch (final InterruptedException e) { // NOSONAR Handled by utilities
                ThreadUtils.getInstance().onInterruption(LOGGER, e);
            } catch (final Exception e) {
                getLogger().warn("Listener cannot process login event: {}", e.getMessage());
            }
        }
    }

    /**
     * Fire on logout.
     * @param listener   the listener
     * @param user       the user
     * @param properties the properties
     */
    protected void fireOnLogout(final User user, final Map<String, String> properties) {
        for (final AuthenticationListener listener : listeners) {
            try {
                listener.onLogout(user, properties);
            } catch (final InterruptedException e) { // NOSONAR Handled by utilities
                ThreadUtils.getInstance().onInterruption(LOGGER, e);
            } catch (final Exception e) {
                getLogger().warn("Listener cannot process logout event", e);
            }
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#getAuthenticatedUsers()
     */
    @Override
    public Collection<User> getAuthenticatedUsers() {
        lock.readLock().lock();

        try {
            final Collection<User> results = new HashSet<>();
            final Map<Long, Authentication> map;

            if (cache == null) {
                map = Collections.emptyMap();
            } else {
                map = new HashMap<>(cache.asMap());
            }

            for (final Entry<Long, Authentication> entry : map.entrySet()) {
                final Optional<User> optional = userDao.findById(entry.getKey());

                if (optional.isPresent()) {
                    results.add(optional.get());
                }
            }

            return results;
        } finally {
            lock.readLock().unlock();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#getAuthentication(java.lang.Long)
     */
    @Override
    public Authentication getAuthentication(final Long userId) {
        getLogger().trace("Retrieving authentication for user identifier: {}", userId);

        if (userId == null || userId.longValue() <= 0) {
            throw new IllegalArgumentException("Given user identifier is invalid");
        }

        lock.readLock().lock();

        try {
            final Authentication result = cache.getIfPresent(userId);

            if (result == null) {
                getLogger().trace("No authentication found");

                return null;
            }

            getLogger().trace("Found authentication: {}", result);

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#getAuthentication(org.infodavid.model.User)
     */
    @Override
    public Authentication getAuthentication(final User user) {
        getLogger().trace("Retrieving authentication for user: {}", user);

        if (user == null || user.getId() == null || user.getId().longValue() <= 0) {
            throw new IllegalArgumentException("Given user is invalid");
        }

        return getAuthentication(user.getId());
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#getListeners()
     */
    @Override
    public Set<AuthenticationListener> getListeners() { return listeners; }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractService#getLogger()
     */
    @Override
    protected Logger getLogger() { return LOGGER; }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#getUser()
     */
    @Override
    public User getUser() throws ServiceException {
        final SecurityContext context = SecurityContextHolder.getContext();
        User result = null;

        if (context == null) {
            getLogger().trace("No security context available");

            return null;
        }

        if (context.getAuthentication() == null) {
            getLogger().trace("No authentication available");

            return null;
        }

        getLogger().trace("Authentication: {}", context.getAuthentication());

        if (context.getAuthentication().getPrincipal() instanceof final User user) {
            result = user;
        } else if (context.getAuthentication().getDetails() instanceof final User user) {
            result = user;
        }

        getLogger().trace("Current user: {}", result);

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#getUser(org.infodavid.security.AuthenticationToken)
     */
    @Override
    public User getUser(final Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException(GIVEN_AUTHENTICATION_IS_INVALID);
        }

        getLogger().trace("Retrieving user for token: {}", authentication);
        lock.readLock().lock();

        try {
            final Map<Long, Authentication> map;

            if (cache == null) {
                map = Collections.emptyMap();
            } else {
                map = new HashMap<>(cache.asMap());
            }

            for (final Entry<Long, Authentication> entry : map.entrySet()) {
                if (authentication.equals(entry.getValue())) {
                    final Optional<User> optional = userDao.findById(entry.getKey());

                    if (optional.isEmpty()) {
                        return null;
                    }

                    return optional.get();
                }
            }

            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#hasRole(java.lang.String)
     */
    @Override
    @SuppressWarnings("boxing")
    public boolean hasRole(final String role) throws ServiceException {
        final User user = getUser();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Role check for user: {} and role: {}", user, role);
        }

        if (user == null) {
            getLogger().trace(Constants.USER_IS_NULL);

            // We assume that anonymous user is not null and null user is the system itself
            return true;
        }

        if (user.getRole() == null) {
            getLogger().trace(Constants.USER_HAS_NO_ROLE_DENIED);

            return false;
        }

        if (org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE.equals(user.getRole())) {
            getLogger().trace(Constants.USER_IS_AN_ADMINISTRATOR_ALLOWED);

            return true;
        }

        final boolean result = user.getRole().equals(role);
        getLogger().trace(Constants.USER_HAS_ROLE_PATTERN, role, result);

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#invalidate(org.springframework.security.core.Authentication, java.util.Map)
     */
    @Override
    public void invalidate(final Authentication authentication, final Map<String, String> properties) {
        if (authentication == null) {
            return;
        }

        final User user = getUser(authentication);

        if (user != null) {
            invalidate(user, properties);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#invalidate(org.infodavid.model.User, java.util.Map)
     */
    @Override
    public boolean invalidate(final User user, final Map<String, String> properties) {
        if (user == null || user.getId() == null || user.getId().longValue() <= 0) {
            return false;
        }

        lock.readLock().lock();

        try {
            getLogger().warn("Invalidating authentication for user: {}", user.getName());
            cache.invalidate(user.getId());
            cache.cleanUp();

            if (getLogger().isTraceEnabled()) {
                getLogger().debug(CACHE_SIZE, String.valueOf(cache.estimatedSize()));
            }

            return true;
        } finally {
            lock.readLock().unlock();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#invalidateAll()
     */
    @Override
    public void invalidateAll() {
        getAuthenticatedUsers().forEach(u -> invalidate(u, Collections.emptyMap()));
        lock.readLock().lock();

        try {
            cache.invalidateAll();
            cache.cleanUp();

            if (getLogger().isTraceEnabled()) {
                getLogger().debug(CACHE_SIZE, String.valueOf(cache.estimatedSize()));
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#isAuthenticated(org.infodavid.model.User)
     */
    @Override
    public boolean isAuthenticated(final User user) {
        if (user == null || user.getId() == null || user.getId().longValue() <= 0) {
            throw new IllegalArgumentException("User is invalid");
        }

        return getAuthenticatedUsers().contains(user);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.listener.ApplicationPropertyChangedListener#propertyChanged(org.infodavid.model.ApplicationProperty[])
     */
    @Override
    public synchronized void propertyChanged(final ApplicationProperty... properties) {
        for (final ApplicationProperty property : properties) {
            propertyChanged(property);
        }
    }

    /**
     * Property changed.
     * @param property the property
     */
    private void propertyChanged(final ApplicationProperty property) {
        if (org.infodavid.commons.service.Constants.SESSION_INACTIVITY_TIMEOUT_PROPERTY.equals(property.getName()) && StringUtils.isNumeric(property.getValue())) {
            // plus 1 minute to let webui or gui disconnect the user before invalidation by this cache
            int value = Integer.parseInt(property.getValue());

            if (value > 0) {
                value += 1;
            }

            buildCache(value, TimeUnit.MINUTES);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.security.AuthenticationService#removeListener(org.infodavid.security.AuthenticationListener)
     */
    @Override
    public boolean removeListener(final AuthenticationListener listener) {
        if (listener == null) {
            return false;
        }

        getLogger().debug("Unregistering listener: {}", listener);

        return listeners.remove(listener);
    }
}
