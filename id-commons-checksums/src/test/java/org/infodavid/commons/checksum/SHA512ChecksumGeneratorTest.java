package org.infodavid.commons.checksum;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The Class SHA512ChecksumGeneratorTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class SHA512ChecksumGeneratorTest extends AbstractChecksumGeneratorTest { // NOSONAR Test methods are in parent class

    /**
     * Instantiates a new SHA 512 checksum generator test.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    SHA512ChecksumGeneratorTest() throws NoSuchAlgorithmException {
        super(SHA512ChecksumGenerator.ALGORITHM, "245478dc2ce08d14e3922ccc78c274dca802f1bb6e5ef8ee9169f2d1cdcab0ec3a7d7dbcf0739c306de0b1d26b43efebdb49d1e00b3ceb2ff2e6d4109149eb39");
    }
}
