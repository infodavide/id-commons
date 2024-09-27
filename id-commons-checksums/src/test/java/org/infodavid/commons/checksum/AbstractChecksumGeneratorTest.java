package org.infodavid.commons.checksum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The Class AbstractChecksumGeneratorTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
abstract class AbstractChecksumGeneratorTest extends TestCase {

    /** The algorithm. */
    private final String algorithm;

    /** The expected checksum. */
    private final String expectedChecksum;

    /** The generator. */
    private final ChecksumGenerator generator;

    /**
     * Instantiates a new abstract checksum generator test.
     * @param algorithm        the algorithm
     * @param expectedChecksum the expected checksum
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    AbstractChecksumGeneratorTest(final String algorithm, final String expectedChecksum) throws NoSuchAlgorithmException {
        this.algorithm = algorithm;
        this.expectedChecksum = expectedChecksum;
        generator = ChecksumGeneratorRegistry.getInstance().getGenerator(algorithm);
    }

    /**
     * Test get algorithm.
     * @throws Exception the exception
     */
    @Test
    void testGetAlgorithm() throws Exception {
        assertEquals(algorithm, generator.getAlgorithm(), "Unexpected algorithm");
    }

    /**
     * Test get checksum.
     * @throws Exception the exception
     */
    @Test
    void testGetChecksumOnContent() throws Exception {
        final String computed = generator.getChecksum(Paths.get("target/test-classes/checksum_tests.png"));

        System.out.println(expectedChecksum);
        System.out.println(computed);
        assertEquals(expectedChecksum, computed, "Wrong checksum");
    }

    /**
     * Test get checksum null.
     * @throws Exception the exception
     */
    @Test
    void testGetChecksumOnContentNull() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> generator.getChecksum((String) null), "Exception not raised or has a wrong type");
    }

    /**
     * Test get checksum.
     * @throws Exception the exception
     */
    @Test
    void testGetChecksumOnFile() throws Exception {
        final File file = new File("target/test-classes/checksum_tests.png");
        final String computed = generator.getChecksum(file.toPath());

        System.out.println(expectedChecksum);
        System.out.println(computed);
        assertEquals(expectedChecksum, computed, "Wrong checksum");
    }

    /**
     * Test get checksum null.
     * @throws Exception the exception
     */
    @Test
    void testGetChecksumOnFileNull() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> generator.getChecksum((Path) null), "Exception not raised or has a wrong type");
    }

    /**
     * Test get checksum missing.
     * @throws Exception the exception
     */
    @Test
    void testGetChecksumOnMissingFile() throws Exception {
        final File file = new File("target/test-classes/missing_image.png");

        assertThrows(NoSuchFileException.class, () -> generator.getChecksum(file.toPath()), "Exception not raised or has a wrong type");
    }
}
