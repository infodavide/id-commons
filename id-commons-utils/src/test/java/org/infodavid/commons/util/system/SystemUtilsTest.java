package org.infodavid.commons.util.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.attribute.UserPrincipal;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.test.TestCase;
import org.infodavid.commons.test.condition.EnabledOnHost;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * The Class SystemUtilsTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class SystemUtilsTest extends TestCase {

    /**
     * Test get architecture.
     * @throws Exception the exception
     */
    @Test
    void testGetArchitecture() throws Exception {
        final String result = SystemUtils.getInstance().getArchitecture();

        assertNotNull(result, "Wrong result");
        assertFalse(StringUtils.isEmpty(result), "Wrong result");
    }

    /**
     * Test get available time zones.
     * @throws Exception the exception
     */
    @Test
    void testGetAvailableTimeZones() throws Exception {
        final String[] result = SystemUtils.getInstance().getAvailableTimeZones();

        assertNotNull(result, "Wrong result");
        assertNotEquals(0, result.length, "Wrong result");
    }

    /**
     * Test get group principal.
     * @throws Exception the exception
     */
    @Test
    void testGetGroupPrincipal() throws Exception {
        final String name;

        if (org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS) {
            if (Locale.getDefault().getISO3Language().equals(Locale.FRANCE.getISO3Language())) {
                name = "Utilisateurs";
            } else {
                name = "Users";
            }
        } else {
            name = "users";
        }

        final UserPrincipal result = SystemUtils.getInstance().getGroupPrincipal(name);

        assertNotNull(result, "Wrong result");
    }

    /**
     * Test get name.
     * @throws Exception the exception
     */
    @Test
    void testGetName() throws Exception {
        final String result = SystemUtils.getInstance().getName();

        assertNotNull(result, "Wrong result");
        assertFalse(StringUtils.isEmpty(result), "Wrong result");
    }

    /**
     * Test get time zone.
     * @throws Exception the exception
     */
    @Test
    void testGetTimeZone() throws Exception {
        final String result = SystemUtils.getInstance().getTimeZone();

        assertNotNull(result, "Wrong result");
        assertFalse(StringUtils.isEmpty(result), "Wrong result");
    }

    /**
     * Test get user name.
     * @throws Exception the exception
     */
    @Test
    void testGetUserName() throws Exception {
        final String result = SystemUtils.getInstance().getUserName();

        assertNotNull(result, "Wrong result");
        assertFalse(StringUtils.isEmpty(result), "Wrong result");
    }

    /**
     * Test get user principal.
     * @throws Exception the exception
     */
    @Test
    void testGetUserPrincipal() throws Exception {
        final UserPrincipal result = SystemUtils.getInstance().getUserPrincipal();

        assertNotNull(result, "Wrong result");
    }

    /**
     * Test get version.
     * @throws Exception the exception
     */
    @Test
    void testGetVersion() throws Exception {
        final String result = SystemUtils.getInstance().getVersion();

        assertNotNull(result, "Wrong result");
        assertFalse(StringUtils.isEmpty(result), "Wrong result");
    }

    /**
     * Test set time.
     * @throws Exception the exception
     */
    @EnabledOnOs(OS.WINDOWS)
    @EnabledOnHost(".*jenkins.*")
    @Test
    void testSetDateTime() throws Exception {
        final ZonedDateTime previous = SystemUtils.getInstance().getDateTime();
        assertNotNull(previous, "Wrong result");

        final ZonedDateTime expected = previous.minusDays(1);

        SystemUtils.getInstance().setDateTime(expected);

        final ZonedDateTime current = SystemUtils.getInstance().getDateTime();

        try {
            assertTrue(current.isAfter(expected.minusSeconds(10)), "Wrong result");
            assertTrue(current.isBefore(expected.plusSeconds(10)), "Wrong result");
        } finally {
            SystemUtils.getInstance().setDateTime(previous);
        }
    }

    /**
     * Test set time zone.
     * @throws Exception the exception
     */
    @EnabledOnOs(OS.LINUX)
    @EnabledOnHost(".*jenkins.*")
    @Test
    void testSetTimeZone() throws Exception {
        final String previous = SystemUtils.getInstance().getTimeZone();
        assertNotNull(previous, "Wrong result");
        assertFalse(StringUtils.isEmpty(previous), "Wrong result");

        final String expected = "America/New_York";
        SystemUtils.getInstance().setTimeZone(expected);

        final String tz = SystemUtils.getInstance().getTimeZone();

        try {
            assertEquals(expected, tz, "Wrong result");
        } finally {
            SystemUtils.getInstance().setTimeZone(previous);
        }
    }

    /**
     * Test set time zone.
     * @throws Exception the exception
     */
    @EnabledOnOs(OS.LINUX)
    @EnabledOnHost(".*jenkins.*")
    @Test
    void testSetTimeZoneWithInvalid() throws Exception {
        final SystemUtils utils = SystemUtils.getInstance();
        assertThrows(IllegalArgumentException.class, () -> {
            utils.setTimeZone(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            utils.setTimeZone("");
        });
    }
}
