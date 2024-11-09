package org.infodavid.commons.impl.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.UserDao;
import org.infodavid.commons.security.AuthenticationListener;
import org.infodavid.commons.security.AuthenticationService;
import org.springframework.security.core.Authentication;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Locked;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultAuthenticationCache.
 */
@Slf4j
public class DefaultAuthenticationCache implements AuthenticationCache {

    /**
     * The Class Listener.
     */
    protected class Listener implements RemovalListener<Long, Authentication> {

        /*
         * (non-Javadoc)
         * @see com.github.benmanes.caffeine.cache.RemovalListener#onRemoval(java.lang.Object, java.lang.Object, com.github.benmanes.caffeine.cache.RemovalCause)
         */
        @Override
        public void onRemoval(@Nullable final Long key, @Nullable final Authentication value, @NonNull final RemovalCause cause) {
            if (key == null || value == null) {
                return;
            }

            LOGGER.debug("Removing cached entry associated to user name: {}", value.getName());
            final Optional<User> optional = userDao.findById(key);

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
    }

    /** The authentication service. */
    @Getter
    @Setter
    private AuthenticationService authenticationService = null;

    /** The authentication cache. */
    private Cache<Long, Authentication> cache;

    /** The removal listener. */
    protected final Listener removalListener;

    /** The user data access object. */
    protected final UserDao userDao;

    /**
     * Instantiates a new authentication service.
     * @param userDao the user data access object
     */
    public DefaultAuthenticationCache(final UserDao userDao) {
        this.userDao = userDao;
        removalListener = new Listener();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#get(java.lang.Long)
     */
    @Override
    @Locked.Read
    public Authentication get(final Long userId) {
        return cache.getIfPresent(userId);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#getMap()
     */
    @Override
    @Locked.Read
    public Map<Long, Authentication> getMap() {
        if (cache == null) {
            return Collections.emptyMap();
        }

        return new HashMap<>(cache.asMap());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#getSize()
     */
    @Override
    @Locked.Read
    public long getSize() {
        return cache.estimatedSize();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#invalidate()
     */
    @Override
    @Locked.Write
    public void invalidate() {
        cache.invalidateAll();
        cache.cleanUp();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#invalidate(java.lang.Long)
     */
    @Override
    @Locked.Write
    public void invalidate(@Min(1) final Long userId) {
        cache.invalidate(userId);
        cache.cleanUp();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#put(java.lang.Long, org.springframework.security.core.Authentication)
     */
    @Override
    @Locked.Write
    public void put(@Min(1) final Long userId, final Authentication result) {
        cache.put(userId, result);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.security.AuthenticationCache#reconfigure(int, java.util.concurrent.TimeUnit)
     */
    @Override
    @Locked.Write
    public void reconfigure(final int expireAfterAccessDuration, final TimeUnit unit) {
        LOGGER.debug("Initializing...");
        Map<Long, Authentication> existing;

        if (cache == null) {
            existing = Collections.emptyMap();
        } else {
            existing = new HashMap<>(cache.asMap());
        }

        final Caffeine<Long, Authentication> builder = Caffeine.newBuilder().maximumSize(100).removalListener(removalListener);

        if (expireAfterAccessDuration > 0) {
            LOGGER.info("Building cache using expire after access duration: {} {}", String.valueOf(expireAfterAccessDuration), StringUtils.lowerCase(unit.name())); // NOSONAR Always written
            builder.expireAfterAccess(expireAfterAccessDuration, unit);
        } else {
            LOGGER.info("Building cache without expiration");
        }

        cache = builder.build();
        existing.entrySet().forEach(e -> cache.put(e.getKey(), e.getValue()));
        LOGGER.debug("Initialized");
    }
}
