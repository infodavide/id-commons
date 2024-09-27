package org.infodavid.commons.jdk;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The Class DiagnosticUtilsTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class DiagnosticUtilsTest extends TestCase {

    /**
     * Test build head dump.
     * @throws Exception the exception
     */
    @Test
    void testBuildHeadDump() throws Exception { // NOSONAR No error
        final File path = new File("target/heapdump.hprof");

        if (path.exists()) {
            path.delete();
        }

        DiagnosticUtils.getInstance().buildHeadDump(path.getAbsolutePath());

        assertTrue( path.exists(),"File not found");
    }

    /**
     * Test collect diagnostics.
     */
    @Test
    void testCollectDiagnostics() { // NOSONAR No error
        final StringBuilder buffer = new StringBuilder();

        DiagnosticUtils.getInstance().collectDiagnostics(buffer);
    }

    /**
     * Test gc.
     * @throws Exception the exception
     */
    @Test
    void testGc() throws Exception { // NOSONAR No error
        DiagnosticUtils.getInstance().gc();
    }

    /**
     * Test get active live threads.
     */
    @Test
    void testGetActiveLiveThreads() { // NOSONAR No error
        DiagnosticUtils.getInstance().getActiveLiveThreads(5);
    }

    /**
     * Test get java heap histogram.
     */
    @Test
    void testGetJavaHeapHistogram() { // NOSONAR No error
        DiagnosticUtils.getInstance().getJavaHeapHistogram("", "");
    }
}
