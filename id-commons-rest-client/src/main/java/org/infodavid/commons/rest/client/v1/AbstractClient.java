package org.infodavid.commons.rest.client.v1;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import lombok.Getter;

/**
 * The Class AbstractClient.
 */
public abstract class AbstractClient {

    /** The application context. */
    @Getter
    private final ApplicationContext applicationContext;

    /** The logger. */
    @Getter
    private final Logger logger;

    /**
     * Instantiates a new abstract client.
     * @param logger             the logger
     * @param applicationContext the application context
     */
    protected AbstractClient(final Logger logger, final ApplicationContext applicationContext) {
        this.logger = logger;
        this.applicationContext = applicationContext;
    }
}
