package org.infodavid.commons.impl.service;

import org.infodavid.commons.persistence.dao.ApplicationPropertyDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * The Class ApplicationServiceImpl.
 */
@Service
public class ApplicationServiceImpl extends AbstractApplicationService {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    /**
     * Instantiates a new application service.
     * @param applicationContext the application context
     * @param validationHelper   the validation helper
     * @param dao                the data access object
     */
    @Autowired
    protected ApplicationServiceImpl(final ApplicationContext applicationContext, final ValidationHelper validationHelper, final ApplicationPropertyDao dao) {
        super(applicationContext, validationHelper, dao);
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
     * @see org.infodavid.commons.impl.service.AbstractService#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
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
}
