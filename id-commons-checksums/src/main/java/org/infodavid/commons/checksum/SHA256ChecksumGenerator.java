package org.infodavid.commons.checksum;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * The Class SHA256ChecksumGenerator.
 */
public final class SHA256ChecksumGenerator extends AbstractChecksumGenerator {

    /** The Constant ALGORITHM. */
    public static final String ALGORITHM = "SHA-256";

    /**
     * Instantiates a new SHA 256 checksum generator.
     */
    public SHA256ChecksumGenerator() {
        // noop
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.checksum.ChecksumGenerator#getAlgorithm()
     */
    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.checksum.ChecksumGenerator#getChecksum(java.lang.String)
     */
    @Override
    public String getChecksum(final String content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("Content is null");
        }

        return DigestUtils.sha256Hex(content);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.checksum.AbstractChecksumGenerator#getChecksum(java.io.InputStream)
     */
    @Override
    protected String getChecksum(final InputStream in) throws IOException {
        return DigestUtils.sha256Hex(in);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.checksum.AbstractChecksumGenerator#getCommand()
     */
    @Override
    protected String getCommand() {
        return "sha256sum";
    }
}
