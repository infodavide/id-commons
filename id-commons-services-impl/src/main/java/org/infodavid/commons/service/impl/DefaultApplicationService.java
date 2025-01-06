package org.infodavid.commons.service.impl;

import java.util.Collections;
import java.util.Map;

import org.infodavid.commons.service.ApplicationService;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class DefaultApplicationService.<br>
 * Keep this class abstract to make it optional for the projects using this module.<br>
 * To use this service, the project must extends this class and add the Spring annotation(s).
 */
@Transactional(readOnly = true)
public abstract class DefaultApplicationService extends AbstractService implements ApplicationService {

    /** The start time. */
    private final long startTime = System.currentTimeMillis();

    /**
     * Instantiates a new application service.
     * @param logger             the logger
     * @param applicationContext the application context
     */
    protected DefaultApplicationService(final Logger logger, final ApplicationContext applicationContext) {
        super(logger, applicationContext);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ApplicationService#getInformation()
     */
    @Override
    public Map<String, String[]> getInformation() {
        return Collections.emptyMap();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ApplicationService#getUpTime()
     */
    @Override
    public long getUpTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
}
