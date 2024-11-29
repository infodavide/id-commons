package org.infodavid.commons.impl.service;

import java.util.Collections;

import org.infodavid.commons.persistence.dao.ConfigurationPropertyDao;
import org.infodavid.commons.service.SchedulerService;
import org.infodavid.commons.service.test.persistence.PlatformTransactionManagerMock;
import org.infodavid.commons.service.test.persistence.dao.ConfigurationPropertyDaoMock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * The Class SpringTestConfiguration.
 */
@Configuration
@ComponentScan(basePackages = { "org.infodavid" })
@ContextConfiguration(value = "classpath:applicationContext-test.xml")
@PropertySource("classpath:application-test.properties")
@TestPropertySource("classpath:application-test.properties")
public class SpringTestConfiguration {

    /**
     * Application configuration manager.
     * @param applicationContext the application context
     * @param validationHelper   the validation helper
     * @param dao                the data access object
     * @return the default configuration manager
     */
    @Bean("applicationConfigurationManager")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DefaultConfigurationManager applicationConfigurationManager(final ApplicationContext applicationContext, final ValidationHelper validationHelper, final ConfigurationPropertyDao dao) {
        return new DefaultConfigurationManager(LoggerFactory.getLogger("ApplicationConfigurationManager"), applicationContext, validationHelper, dao, org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
    }

    /**
     * Configuration property data access object.
     * @return the data access object
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ConfigurationPropertyDao configurationPropertyDao() throws Exception {
        return new ConfigurationPropertyDaoMock();
    }

    /**
     * Scheduler service.
     * @param applicationContext the application context
     * @return the scheduler service
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SchedulerService schedulerService(final ApplicationContext applicationContext) throws Exception {
        return new DefaultSchedulerService(LoggerFactory.getLogger(DefaultSchedulerService.class), applicationContext);
    }

    /**
     * Transaction manager.
     * @param configurationPropertyDao the configuration property data access object
     * @return the transaction manager
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public PlatformTransactionManager transactionManager(final ConfigurationPropertyDao configurationPropertyDao) {
        return new PlatformTransactionManagerMock(Collections.singletonList(configurationPropertyDao));
    }
}
