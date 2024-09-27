package org.infodavid.commons.impl.service;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * The Class AbstractService.
 */
public abstract class AbstractService {

    /** The application context provider. */
    protected final ApplicationContextProvider applicationContextProvider;

    /**
     * Instantiates a new abstract service.
     */
    protected AbstractService(final ApplicationContextProvider applicationContextProvider) {
        this.applicationContextProvider = applicationContextProvider;
    }

    /**
     * Gets the application context.
     * @return the application context
     */
    protected ApplicationContext getApplicationContext() {
        return ApplicationContextProvider.getApplicationContext();
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected abstract Logger getLogger();
}
