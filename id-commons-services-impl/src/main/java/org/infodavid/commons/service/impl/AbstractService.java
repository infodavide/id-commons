package org.infodavid.commons.service.impl;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import lombok.Getter;

/**
 * The Class AbstractService.
 */
public abstract class AbstractService {

    /** The application context. */
    @Getter
    private final ApplicationContext applicationContext;

    /** The logger. */
    @Getter
    private final Logger logger;

    /**
     * Instantiates a new abstract service.
     * @param logger             the logger
     * @param applicationContext the application context
     */
    protected AbstractService(final Logger logger, final ApplicationContext applicationContext) {
        this.logger = logger;
        this.applicationContext = applicationContext;
    }
}
