package org.infodavid.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.charset.StandardCharsets;

import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The Class StringUtilsTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class StringUtilsTest extends TestCase {

    /**
     * Test compare same versions.
     * @throws Exception the exception
     */
    @Test
    void testCompareSameVersions() throws Exception {
        assertEquals(0, StringUtils.getInstance().compareVersion("1.2.0", "1.2.0"), "Wrong processing");
        assertEquals(0, StringUtils.getInstance().compareVersion("1.2", "1.2.0"), "Wrong processing");
    }

    /**
     * Test compare positive versions.
     * @throws Exception the exception
     */
    @Test
    void testComparePositiveVersions() throws Exception {
        assertEquals(1, StringUtils.getInstance().compareVersion("1.2.0", "1.1.2"), "Wrong processing");
        assertEquals(1, StringUtils.getInstance().compareVersion("1.2", "1.1.2"), "Wrong processing");
    }

    /**
     * Test compare negative versions.
     * @throws Exception the exception
     */
    @Test
    void testCompareNegativeVersions() throws Exception {
        assertEquals(-1, StringUtils.getInstance().compareVersion("1.1.2", "1.2.0"), "Wrong processing");
        assertEquals(-1, StringUtils.getInstance().compareVersion("1.1.2", "1.2"), "Wrong processing");
    }

    /**
     * Test decode.
     * @throws Exception the exception
     */
    @Test
    void testDecode() throws Exception {
        final String value = "È lei la più bella";

        final String encoded = new String(StringUtils.getInstance().encode(value), StandardCharsets.UTF_8);

        assertNotEquals(value, encoded, "Not processed");
        assertEquals(value, StringUtils.getInstance().decode(encoded), "Wrong processing");
    }
}
