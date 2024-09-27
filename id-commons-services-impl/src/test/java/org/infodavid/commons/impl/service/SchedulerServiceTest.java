package org.infodavid.commons.impl.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infodavid.commons.impl.AbstractSpringTest;
import org.infodavid.commons.service.SchedulerService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class SchedulerServiceTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class SchedulerServiceTest extends AbstractSpringTest {

    /** The service. */
    @Autowired
    private SchedulerService service;

    /**
     * Test submit.
     * @throws Exception the exception
     */
    @Test
    void testSubmit() throws Exception {
        final AtomicBoolean flag = new AtomicBoolean(false);
        final Future<?> future = service.submit(() -> {
            flag.set(true);
        });

        assertNotNull( future,"Future is null");

        future.get();

        assertTrue( flag.get(),"Wrong data");
    }
}
