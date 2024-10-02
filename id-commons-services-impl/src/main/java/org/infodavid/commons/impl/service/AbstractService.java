package org.infodavid.commons.impl.service;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * The Class AbstractService.
 */
public abstract class AbstractService {

    /** The application context. */
    private final ApplicationContext applicationContext;

    /**
     * Instantiates a new abstract service.
     */
    protected AbstractService(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the application context.
     * @return the application context
     */
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected abstract Logger getLogger();
}
