package org.infodavid.commons.impl.service;

import org.infodavid.commons.service.test.DataInitializer;
import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

/**
 * The Class AbstractSpringTest.
 */
@SpringBootTest
@ContextConfiguration(classes = {
        SpringTestConfiguration.class
})
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class AbstractSpringTest extends TestCase implements ApplicationContextAware {

    /** The application context. */
    protected ApplicationContext applicationContext;

    /** The initializer. */
    @Autowired
    private DataInitializer dataInitializer;

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework .context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.test.TestCase#setUp()
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        dataInitializer.initialize();
    }
}
