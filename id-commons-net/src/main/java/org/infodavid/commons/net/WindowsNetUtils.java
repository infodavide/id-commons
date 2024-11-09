package org.infodavid.commons.net;

import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class WindowsNetUtils.
 */
@Slf4j
final class WindowsNetUtils extends NetUtils {

    /*
     * (non-javadoc)
     * @see org.infodavid.util.net.NetUtils#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
