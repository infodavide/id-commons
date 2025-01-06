package org.infodavid.commons.service.impl;

import org.infodavid.commons.service.impl.DefaultApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * The Class ApplicationServiceImpl.
 */
@Service
public class ApplicationServiceImpl extends DefaultApplicationService {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    /**
     * Instantiates a new application service.
     * @param applicationContext the application context
     */
    @Autowired
    protected ApplicationServiceImpl(final ApplicationContext applicationContext) {
        super(LOGGER, applicationContext);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ApplicationService#getBuild()
     */
    @Override
    public String getBuild() {
        return "1";
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ApplicationService#getHealthValue()
     */
    @Override
    public String getHealthValue() {
        return "0k";
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ApplicationService#getName()
     */
    @Override
    public String getName() {
        return "TestApplication";
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ApplicationService#getVersion()
     */
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ApplicationService#isProduction()
     */
    @Override
    public boolean isProduction() {
        return false;
    }
}
