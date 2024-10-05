package org.infodavid.commons.checksum;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * The Class MD5ChecksumGenerator.
 */
public final class MD5ChecksumGenerator extends AbstractChecksumGenerator {

    /** The Constant ALGORITHM. */
    public static final String ALGORITHM = "MD5";

    /**
     * Instantiates a new MD 5 checksum generator.
     */
    public MD5ChecksumGenerator() {
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

        return DigestUtils.md5Hex(content);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.checksum.AbstractChecksumGenerator#getChecksum(java.io.InputStream)
     */
    @Override
    protected String getChecksum(final InputStream in) throws IOException {
        return DigestUtils.md5Hex(in);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.util.checksum.AbstractChecksumGenerator#getCommand()
     */
    @Override
    protected String getCommand() {
        return "md5sum";
    }
}
