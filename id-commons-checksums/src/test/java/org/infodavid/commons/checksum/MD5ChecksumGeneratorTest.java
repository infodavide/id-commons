package org.infodavid.commons.checksum;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The Class MD5ChecksumGeneratorTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class MD5ChecksumGeneratorTest extends AbstractChecksumGeneratorTest { // NOSONAR Test methods are in parent class

    /**
     * Instantiates a new MD 5 checksum generator test.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    MD5ChecksumGeneratorTest() throws NoSuchAlgorithmException {
        super(MD5ChecksumGenerator.ALGORITHM, "9e2d3ae986b81d7be4b9753b35881360");
    }
}
