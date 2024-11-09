package org.infodavid.commons.impl.security;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryExpired;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryExpiredEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryInvalidatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infodavid.commons.impl.service.DistributedDataServiceImpl;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.UserDao;
import org.infodavid.commons.security.AuthenticationListener;
import org.infodavid.commons.security.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DistributedAuthenticationCache.
 */
/* If necessary, declare the bean in the Spring configuration. */
@Transactional
@org.infinispan.notifications.Listener
@Slf4j
public class DistributedAuthenticationCache implements AuthenticationCache {

    /** The authentication service. */
    @Getter
    @Setter
    private AuthenticationService authenticationService = null;

    /** The service. */
    private final Cache<Long, Authentication> cache;

    /** The expiration. */
    private long expiration;

    /** The expiration unit. */
    private TimeUnit expirationUnit;

    /** The user data access object. */
    protected final UserDao userDao;

    /**
     * Instantiates a new authentication service.
     * @param userDao the user data access object
     * @param service the service
     */
    public DistributedAuthenticationCache(final UserDao userDao, final DistributedDataServiceImpl service) {
        this.userDao = userDao;
        cache = ((Cache<Long, Authentication>) service.getCache(getClass().getName())).getAdvancedCache().withFlags(Flag.FORCE_SYNCHRONOUS, Flag.IGNORE_RETURN_VALUES);
        cache.addListener(this);
    }

    /**
     * Cache entry created.
     * @param event the event
     */
    @CacheEntryExpired
    public void cacheEntryCreated(final CacheEntryExpiredEvent<Long, Authentication> event) {
        onRemoval(event.getKey(), event.getValue());
    }

    /**
     * Cache entry created.
     * @param event the event
     */
    @CacheEntryInvalidated
    public void cacheEntryCreated(final CacheEntryInvalidatedEvent<Long, Authentication> event) {
        onRemoval(event.getKey(), event.getValue());
    }

    /**
     * Cache entry created.
     * @param event the event
     */
    @CacheEntryRemoved
    public void cacheEntryCreated(final CacheEntryRemovedEvent<Long, Authentication> event) {
        onRemoval(event.getKey(), event.getValue());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#get(java.lang.Long)
     */
    @Override
    public Authentication get(final Long userId) {
        return cache.get(userId);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#getMap()
     */
    @Override
    public Map<Long, Authentication> getMap() {
        return cache;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#getSize()
     */
    @Override
    public long getSize() {
        return cache.size();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#invalidate()
     */
    @Override
    public void invalidate() {
        cache.clear();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#invalidate(java.lang.Long)
     */
    @Override
    public void invalidate(@Min(1) final Long userId) {
        cache.remove(userId);
    }

    /**
     * On removal.
     * @param userId         the user id
     * @param authentication the authentication
     */
    private void onRemoval(final Long userId, final Authentication authentication) {
        if (userId == null || authentication == null) {
            return;
        }

        LOGGER.debug("Removing cached entry associated to user name: {}", authentication.getName());
        final Optional<User> optional = userDao.findById(userId);

        if (optional.isPresent() && authenticationService != null) {
            final User user = optional.get();

            for (final AuthenticationListener listener : authenticationService.getListeners()) {
                try {
                    listener.onLogout(user, Collections.emptyMap());
                } catch (final Exception e) {
                    LOGGER.warn("Listener cannot process logout event", e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#put(java.lang.Long, org.springframework.security.core.Authentication)
     */
    @Override
    public void put(@Min(1) final Long userId, final Authentication authentication) {
        cache.put(userId, authentication, expiration, expirationUnit);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#reconfigure(int, java.util.concurrent.TimeUnit)
     */
    @Override
    public void reconfigure(final int expireAfterAccessDuration, final TimeUnit unit) {
        expiration = expireAfterAccessDuration;
        expirationUnit = unit;
    }
}
