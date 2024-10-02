package org.infodavid.commons.impl.security;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.infodavid.commons.security.AuthenticationService;
import org.springframework.security.core.Authentication;

import jakarta.validation.constraints.Min;

/**
 * The Interface AuthenticationCache.
 */
public interface AuthenticationCache {

    /**
     * Gets the cache data.
     * @param userId the user identifier
     * @return the cache data
     */
    Authentication get(final Long userId);

    /**
     * Gets the authentication service.
     * @return the authentication service
     */
    AuthenticationService getAuthenticationService();

    /**
     * Gets the cache data.
     * @return the cache data
     */
    Map<Long, Authentication> getMap();

    /**
     * Gets the cache size.
     * @return the cache size
     */
    long getSize();

    /**
     * Invalidate cache data.
     */
    void invalidate();

    /**
     * Invalidate cache data.
     * @param userId the user identifier
     */
    void invalidate(@Min(1) final Long userId);

    /**
     * Put cache data.
     * @param userId the user identifier
     * @param result the result
     */
    void put(@Min(1) final Long userId, final Authentication result);

    /**
     * Configure with the given expiration duration.
     * @param expireAfterAccessDuration the expire after access duration
     * @param unit                      the unit associated to the specified duration
     */
    void reconfigure(final int expireAfterAccessDuration, final TimeUnit unit);

    /**
     * Sets the authentication service.
     * @param euthenticationService the new authentication service
     */
    void setAuthenticationService(AuthenticationService euthenticationService);

}
