package org.infodavid.commons.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class LinuxNetUtils.
 */
final class LinuxNetUtils extends NetUtils {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxNetUtils.class);
    
    /*
     * (non-javadoc)
     * @see org.infodavid.util.net.NetUtils#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
