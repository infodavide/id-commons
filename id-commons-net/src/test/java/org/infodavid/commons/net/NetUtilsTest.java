package org.infodavid.commons.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The Class NetUtilsTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class NetUtilsTest extends TestCase {

    /**
     * Test find available TCP port.
     * @throws Exception the exception
     */
    @Test
    void testFindAvailableTcpPort() throws Exception {
        assertTrue(NetUtils.getInstance().findAvailableTcpPort() > 0, "Wrong TCP port");
    }

    /**
     * Test get computer name.
     * @throws Exception the exception
     */
    @Test
    void testGetComputerName() throws Exception {
        assertTrue(StringUtils.isNotEmpty(NetUtils.getInstance().getComputerName()), "Wrong computer name");
    }

    /**
     * Test get MAC addresses.
     * @throws Exception the exception
     */
    @Test
    void testGetMacAddresses() throws Exception {
        final List<String> results = NetUtils.getInstance().getMacAddresses();

        assertNotNull(results, "Results is null");
        assertTrue(results.size() > 0, "Wrong results");

        for (final String item : results) {
            assertTrue(StringUtils.isNotEmpty(item), "Wrong MAC address");

            if ("N/A".equals(item)) {
                continue;
            }

            assertNotEquals(-1, item.indexOf(':'), "MAC address contains ':'");
            assertEquals(-1, item.indexOf(' '), "MAC address contains a space");
            assertEquals(-1, item.indexOf('-'), "MAC address contains '-'");
            assertEquals(-1, item.indexOf('_'), "MAC address contains '_'");
        }
    }

    /**
     * Test get MAC addresses info.
     * @throws Exception the exception
     */
    @Test
    void testGetMacAddressesInfo() throws Exception {
        final List<String> results = NetUtils.getInstance().getMacAddresses();

        assertNotNull(results, "Results is null");
        assertTrue(results.size() > 0, "Wrong results");

        for (final String item : results) {
            assertTrue(StringUtils.isNotEmpty(item), "Wrong MAC address");
        }
    }

    /**
     * Test get MAC addresses info.
     * @throws Exception the exception
     */
    @Test
    void testGetNetworkInterfaces() throws Exception {
        final Map<String, NetworkInterface> results = NetUtils.getInstance().getNetworkInterfaces();
        System.out.println(results);
        assertNotNull(results, "Results is null");
        assertTrue(results.size() > 0, "Wrong results");

        for (final Entry<String, NetworkInterface> item : results.entrySet()) {
            assertTrue(StringUtils.isNotEmpty(item.getValue().macAddress()), "Wrong MAC address");
            assertTrue(StringUtils.isNotEmpty(item.getValue().name()), "Wrong interface description");
        }
    }
}
