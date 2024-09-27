package org.infodavid.commons.checksum;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The Class SHA256ChecksumGeneratorTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class SHA256ChecksumGeneratorTest extends AbstractChecksumGeneratorTest { // NOSONAR Test methods are in parent class

    /**
     * Instantiates a new SHA 256 checksum generator test.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    SHA256ChecksumGeneratorTest() throws NoSuchAlgorithmException {
        super(SHA256ChecksumGenerator.ALGORITHM, "8878737fc1f51b78439f057ab330b8e5314a54a5bb3524601ded9690dd4e891e");
    }
}
