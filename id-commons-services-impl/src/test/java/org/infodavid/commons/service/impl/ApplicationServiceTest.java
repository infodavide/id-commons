package org.infodavid.commons.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.infodavid.commons.service.ApplicationService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class ApplicationServiceTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class ApplicationServiceTest extends AbstractSpringTest {

    /** The service. */
    @Autowired
    private ApplicationService service;

    /**
     * Test get build.
     */
    @Test
    void testGetBuild() {
        assertNotNull(service.getBuild(), "Build is null");
    }

    /**
     * Test get health value.
     */
    @Test
    void testGetHealthValue() {
        assertNotNull(service.getHealthValue(), "Health value is null");
    }

    /**
     * Test get information.
     */
    @Test
    void testGetInformation() {
        assertNotNull(service.getInformation(), "Information is null");
    }

    /**
     * Test get name.
     */
    @Test
    void testGetName() {
        assertNotNull(service.getName(), "Name is null");
    }

    /**
     * Test get version.
     */
    @Test
    void testGetVersion() {
        assertNotNull(service.getVersion(), "Version is null");
    }
}
