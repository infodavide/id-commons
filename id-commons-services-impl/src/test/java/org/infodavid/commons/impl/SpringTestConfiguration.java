package org.infodavid.commons.impl;

import org.infodavid.commons.impl.security.DefaultAuthenticationCache;
import org.infodavid.commons.impl.security.DefaultAuthenticationServiceImpl;
import org.infodavid.commons.impl.service.DefaultSchedulerService;
import org.infodavid.commons.impl.service.DefaultUserService;
import org.infodavid.commons.impl.service.ValidationHelper;
import org.infodavid.commons.persistence.dao.ApplicationPropertyDao;
import org.infodavid.commons.persistence.dao.UserDao;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.service.SchedulerService;
import org.infodavid.commons.service.UserService;
import org.infodavid.commons.test.persistence.dao.AbstractDefaultDaoMock;
import org.infodavid.commons.test.persistence.dao.ApplicationPropertyDaoMock;
import org.infodavid.commons.test.persistence.dao.UserDaoMock;
import org.springframework.beans.factory.FactoryBean;
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
     * ApplicationProperty data access object.
     * @return the data access object
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ApplicationPropertyDao applicationPropertyDao() throws Exception {
        return new ApplicationPropertyDaoMock();
    }

    /**
     * Authentication service.
     * @param applicationContext the application context
     * @param userDao            the user data access object
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FactoryBean<AuthenticationService> authenticationService(final ApplicationContext applicationContext, final UserDao userDao) throws Exception {
        return new FactoryBean<>() {
            @Override
            public AuthenticationService getObject() throws Exception {
                return new DefaultAuthenticationServiceImpl(applicationContext, userDao, new DefaultAuthenticationCache(userDao));
            }

            @Override
            public Class<?> getObjectType() {
                return AuthenticationService.class;
            }
        };
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
        return new DefaultSchedulerService(applicationContext);
    }

    /**
     * Transaction manager.
     * @param applicationPropertyDao the application property data access object
     * @param userDao                the user data access object
     * @return the transaction manager
     */
    @SuppressWarnings("rawtypes")
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public PlatformTransactionManager transactionManager(final ApplicationPropertyDao applicationPropertyDao, final UserDao userDao) {
        final AbstractDefaultDaoMock[] mocks = new AbstractDefaultDaoMock[2];
        mocks[0] = (AbstractDefaultDaoMock) applicationPropertyDao;
        mocks[1] = (AbstractDefaultDaoMock) userDao;

        return new PlatformTransactionManagerMock(mocks);
    }

    /**
     * User data access object.
     * @return the data access object
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UserDao userDao() throws Exception {
        return new UserDaoMock();
    }

    /**
     * User service.
     * @param applicationContext the application context
     * @param validationHelper   the validation helper
     * @param dao                the data access object
     * @return the user service
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UserService userService(final ApplicationContext applicationContext, final ValidationHelper validationHelper, final UserDao dao) throws Exception {
        return new DefaultUserService(applicationContext, validationHelper, dao);
    }
}
