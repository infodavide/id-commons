package org.infodavid.commons.service;

import java.util.Map;

/**
 * The Class ApplicationService.<br>
 */
public interface ApplicationService {

    /**
     * Gets the application build number.
     * @return the build number
     */
    String getBuild();

    /**
     * Gets the health value.
     * @return the health value
     */
    String getHealthValue();

    /**
     * Gets the information.
     * @return the information
     */
    Map<String, String[]> getInformation();

    /**
     * Gets the application name.
     * @return the name
     */
    String getName();

    /**
     * Gets the up time in seconds.
     * @return the up time
     */
    long getUpTime();

    /**
     * Gets the application version.
     * @return the version
     */
    String getVersion();

    /**
     * Checks if is production.
     * @return true, if is production
     */
    boolean isProduction();
}
