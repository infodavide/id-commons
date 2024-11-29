package org.infodavid.commons.logback;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * The Class DuplicatedMessageFilterTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class DuplicatedMessageFilterTest extends TestCase {

    /** The Constant MESSAGE. */
    private static final String MESSAGE = "Getting identifier {}";

    /** The Constant NORMAL_MARKER. */
    private static final String NORMAL_MARKER = "NORMAL";

    /** The Constant SECURITY_MARKER. */
    private static final String SECURITY_MARKER = "SECURITY";

    /**
     * Should allow only four repetitions.
     */
    @Test
    void shouldAllowOnlyFourRepetitions() {
        final DuplicatedMessageFilter filter = new DuplicatedMessageFilter();
        filter.setAllowedRepetitions(4);
        filter.start();

        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("2")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("3")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.DENY, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.DENY, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.DENY, logMessage(filter, MESSAGE, id("1")));
    }

    /**
     * Should consider message similarity.
     */
    @Test
    void shouldConsiderMessageSimilarity() {
        final DuplicatedMessageFilter filter = new DuplicatedMessageFilter();
        filter.setAllowedRepetitions(4);
        filter.start();

        // Identifier property is taken into consideration so all are different and allowed
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("2")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("3")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("4")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("5")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("6")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("7")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("8")));
    }

    /**
     * Should exclude markers.
     * @throws Exception the exception
     */
    @Test
    void shouldExcludeMarkers() throws Exception {
        final DuplicatedMessageFilter filter = new DuplicatedMessageFilter();
        filter.setAllowedRepetitions(4);
        filter.setExcludeMarkers(SECURITY_MARKER);
        filter.start();

        assertEquals(FilterReply.NEUTRAL, logMessage(marker(SECURITY_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(SECURITY_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(SECURITY_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(SECURITY_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(SECURITY_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(SECURITY_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(NORMAL_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(NORMAL_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(NORMAL_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(NORMAL_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(marker(NORMAL_MARKER), filter, MESSAGE, id("1")));
        assertEquals(FilterReply.DENY, logMessage(marker(NORMAL_MARKER), filter, MESSAGE, id("1")));
    }

    /**
     * Should not allow repetitions when cache size exceeded.
     */
    @Test
    void shoulAllowRepetitionsWhenCacheSizeExceeded() {
        final DuplicatedMessageFilter filter = new DuplicatedMessageFilter();
        filter.setAllowedRepetitions(4);
        filter.setCacheSize(2);
        filter.start();

        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, "Another log message 1", null));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, "Another log message 2", null));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
    }

    /**
     * Should reappear after cache has expired.
     * @throws Exception the exception
     */
    @Test
    void shouldReappearAfterCacheHasExpired() throws Exception {
        final DuplicatedMessageFilter filter = new DuplicatedMessageFilter();
        filter.setAllowedRepetitions(4);
        filter.setExpireAfterWriteSeconds(2);
        filter.start();

        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.DENY, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.DENY, logMessage(filter, MESSAGE, id("1")));
        assertEquals(FilterReply.DENY, logMessage(filter, MESSAGE, id("1")));

        Thread.sleep(3000); // NOSONAR For tests

        // messages are allowed as the cache for customer 1 has expired
        assertEquals(FilterReply.NEUTRAL, logMessage(filter, MESSAGE, id("1")));
    }

    /**
     * Id.
     * @param id the id
     * @return the string[]
     */
    private static String[] id(final String id) {
        return new String[] { id };
    }

    /**
     * Logger.
     * @return the logger
     */
    private static Logger logger() {
        return LoggerFactory.getLogger("org.infodavid");
    }

    /**
     * Log message.
     * @param marker  the marker
     * @param filter  the filter
     * @param message the message
     * @param params  the parameters
     * @return the filter reply
     */
    private static FilterReply logMessage(final Marker marker, final TurboFilter filter, final String message, final String[] params) {
        return filter.decide(marker, (ch.qos.logback.classic.Logger) logger(), null, message, params, null);
    }

    /**
     * Log message.
     * @param filter  the filter
     * @param message the message
     * @param params  the parameters
     * @return the filter reply
     */
    private static FilterReply logMessage(final TurboFilter filter, final String message, final String[] params) {
        return logMessage(null, filter, message, params);
    }

    /**
     * Marker.
     * @param marker the marker
     * @return the marker
     */
    private static Marker marker(final String marker) {
        return MarkerFactory.getMarker(marker);
    }
}
