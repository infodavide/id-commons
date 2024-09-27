package org.infodavid.commons.impl.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * The Class ApplicationContextProvider.
 */
@Service("ServicesApplicationContextProvider")
public class ApplicationContextProvider implements ApplicationContextAware {

    /** The context. */
    private static ApplicationContext context;

    /**
     * Gets the application context.
     * @return the application context
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework
     * .context. ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext ac) {
        context = ac; // NOSONAR Works as expected to retrieve the context from components not handled by Spring
    }
}
