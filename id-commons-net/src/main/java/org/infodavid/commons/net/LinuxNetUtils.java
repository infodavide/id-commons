package org.infodavid.commons.net;

import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class LinuxNetUtils.
 */
@Slf4j
final class LinuxNetUtils extends NetUtils {

    /*
     * (non-javadoc)
     * @see org.infodavid.util.net.NetUtils#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
