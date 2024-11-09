package org.infodavid.commons.checksum;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;

import lombok.NoArgsConstructor;

/**
 * The Class SHA512ChecksumGenerator.
 */
@NoArgsConstructor
public final class SHA512ChecksumGenerator extends AbstractChecksumGenerator {

    /** The Constant ALGORITHM. */
    public static final String ALGORITHM = "SHA-512";

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

        return DigestUtils.sha512Hex(content);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.checksum.AbstractChecksumGenerator#getChecksum(java.io.InputStream)
     */
    @Override
    protected String getChecksum(final InputStream in) throws IOException {
        return DigestUtils.sha512Hex(in);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.checksum.AbstractChecksumGenerator#getCommand()
     */
    @Override
    protected String getCommand() {
        return "sha512sum";
    }
}
