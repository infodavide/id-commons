package org.infodavid.commons.impl.service;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * The Class AbstractService.
 */
public abstract class AbstractService {

    /** The application context. */
    private final ApplicationContext applicationContext;

    /** The logger. */
    private Logger logger;

    /**
     * Instantiates a new abstract service.
     * @param logger             the logger
     * @param applicationContext the application context
     */
    protected AbstractService(final Logger logger, final ApplicationContext applicationContext) {
        this.logger = logger;
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the application context.
     * @return the application context
     */
    public final ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    public final Logger getLogger() {
        return logger;
    }
}
