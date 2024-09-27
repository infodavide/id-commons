package org.infodavid.commons.checksum;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The Class SHA384ChecksumGeneratorTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class SHA384ChecksumGeneratorTest extends AbstractChecksumGeneratorTest { // NOSONAR Test methods are in parent class

    /**
     * Instantiates a new SHA 384 checksum generator test.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    SHA384ChecksumGeneratorTest() throws NoSuchAlgorithmException {
        super(SHA384ChecksumGenerator.ALGORITHM, "8b970076571b14177729600300d7cca559a535648c24797915b4529493c6770b83324de18cd8a1b8444a68ebf55b64f9");
    }
}
