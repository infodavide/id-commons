package org.infodavid.commons.util.logging.logback;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * The Class DuplicatedMessageFilter.
 */
public class DuplicatedMessageFilter extends TurboFilter {

    /** The Constant MAX_KEY_LENGTH. */
    private static final byte MAX_KEY_LENGTH = 100;

    /** The Constant DEFAULT_CACHE_SIZE. */
    private static final byte DEFAULT_CACHE_SIZE = 100;

    /** The Constant DEFAULT_ALLOWED_REPETITIONS. */
    private static final byte DEFAULT_ALLOWED_REPETITIONS = 5;

    /** The Constant DEFAULT_EXPIRE_AFTER_WRITE_SECONDS. */
    private static final byte DEFAULT_EXPIRE_AFTER_WRITE_SECONDS = 30;

    /** The allowed repetitions. */
    private int allowedRepetitions = DEFAULT_ALLOWED_REPETITIONS;

    /** The cache size. */
    private int cacheSize = DEFAULT_CACHE_SIZE;

    /** The expire after write seconds. */
    private int expireAfterWriteSeconds = DEFAULT_EXPIRE_AFTER_WRITE_SECONDS;

    /** The exclude markers. */
    private String excludeMarkers = "";

    /** The exclude markers list. */
    private List<Marker> excludeMarkersList = new ArrayList<>();

    /** The cache. */
    private Cache<String, Integer> cache;

    /**
     * Start.
     */
    @Override
    public void start() {
        cache = Caffeine.newBuilder().expireAfterWrite(expireAfterWriteSeconds, TimeUnit.SECONDS).initialCapacity(cacheSize).maximumSize(cacheSize).build();
        excludeMarkersList = excludeMarkers(excludeMarkers);
        super.start();
    }

    /**
     * Stop.
     */
    @Override
    public void stop() {
        cache.invalidateAll();
        cache = null;
        super.stop();
    }

    /**
     * Decide.
     * @param marker the marker
     * @param logger the logger
     * @param level  the level
     * @param format the format
     * @param params the params
     * @param t      the t
     * @return the filter reply
     */
    @Override
    public FilterReply decide(final Marker marker, final Logger logger, final Level level, final String format, final Object[] params, final Throwable t) {
        if (excludeMarkersList.contains(marker)) {
            return FilterReply.NEUTRAL;
        }

        int count = 0;

        if (StringUtils.isNotBlank(format)) {
            final String key = StringUtils.abbreviate(format + paramsAsString(params, logger), MAX_KEY_LENGTH);
            final Integer msgCount = cache.getIfPresent(key);

            if (msgCount != null) {
                count = msgCount.intValue() + 1;
            }

            cache.put(key, Integer.valueOf(count));
        }

        return count <= allowedRepetitions ? FilterReply.NEUTRAL : FilterReply.DENY;
    }

    /**
     * Gets the allowed repetitions.
     * @return the allowed repetitions
     */
    public int getAllowedRepetitions() {
        return allowedRepetitions;
    }

    /**
     * Sets the allowed repetitions.
     * @param allowedRepetitions the new allowed repetitions
     */
    public void setAllowedRepetitions(final int allowedRepetitions) {
        this.allowedRepetitions = allowedRepetitions;
    }

    /**
     * Gets the cache size.
     * @return the cache size
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * Sets the cache size.
     * @param cacheSize the new cache size
     */
    public void setCacheSize(final int cacheSize) {
        this.cacheSize = cacheSize;
    }

    /**
     * Gets the expire after write seconds.
     * @return the expire after write seconds
     */
    public int getExpireAfterWriteSeconds() {
        return expireAfterWriteSeconds;
    }

    /**
     * Sets the expire after write seconds.
     * @param expireAfterWriteSeconds the new expire after write seconds
     */
    public void setExpireAfterWriteSeconds(final int expireAfterWriteSeconds) {
        this.expireAfterWriteSeconds = expireAfterWriteSeconds;
    }

    /**
     * Gets the exclude markers.
     * @return the exclude markers
     */
    public String getExcludeMarkers() {
        return excludeMarkers;
    }

    /**
     * Sets the exclude markers.
     * @param excludeMarkers the new exclude markers
     */
    public void setExcludeMarkers(final String excludeMarkers) {
        this.excludeMarkers = excludeMarkers;
    }

    /**
     * Exclude markers.
     * @param markersToExclude the markers to exclude
     * @return the list
     */
    private static List<Marker> excludeMarkers(final String markersToExclude) {
        final List<String> listOfMarkers = Arrays.asList(markersToExclude.split("\\s*,\\s*"));

        return listOfMarkers.stream().map(MarkerFactory::getMarker).toList();
    }

    /**
     * Parameters as string.
     * @param params the parameters
     * @param logger the logger
     * @return the string
     */
    private static String paramsAsString(final Object[] params, final Logger logger) {
        if (params != null && StringUtils.startsWith(logger.getName(), "org.infodavid")) {
            return Arrays.stream(params).map(Object::toString).collect(joining("_"));
        }

        return "";
    }
}
