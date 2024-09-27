package org.infodavid.commons.impl;

import org.infodavid.commons.impl.security.DefaultAuthenticationServiceImpl;
import org.infodavid.commons.impl.service.ApplicationContextProvider;
import org.infodavid.commons.impl.service.DefaultSchedulerService;
import org.infodavid.commons.impl.service.DefaultUserService;
import org.infodavid.commons.impl.service.ValidationHelper;
import org.infodavid.commons.persistence.dao.ApplicationPropertyDao;
import org.infodavid.commons.persistence.dao.UserDao;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.service.SchedulerService;
import org.infodavid.commons.service.UserService;
import org.infodavid.commons.test.persistence.dao.ApplicationPropertyDaoMock;
import org.infodavid.commons.test.persistence.dao.UserDaoMock;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

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
     * ApplicationProperty DAO.
     * @return the DAO
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ApplicationPropertyDao applicationPropertyDao() throws Exception {
        return new ApplicationPropertyDaoMock();
    }

    /**
     * Authentication service.
     * @param applicationContextProvider the application context provider
     * @param userDao                    the user data access object
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FactoryBean<AuthenticationService> authenticationService(final ApplicationContextProvider applicationContextProvider, final UserDao userDao) throws Exception {
        return new FactoryBean<>() {
            @Override
            public AuthenticationService getObject() throws Exception {
                return new DefaultAuthenticationServiceImpl(applicationContextProvider, userDao);
            }

            @Override
            public Class<?> getObjectType() {
                return AuthenticationService.class;
            }
        };
    }

    /**
     * Scheduler service.
     * @param applicationContextProvider the application context provider
     * @return the scheduler service
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SchedulerService schedulerService(final ApplicationContextProvider applicationContextProvider) throws Exception {
        return new DefaultSchedulerService(applicationContextProvider);
    }

    /**
     * User DAO.
     * @return the DAO
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UserDao userDao() throws Exception {
        return new UserDaoMock();
    }

    /**
     * User service.
     * @param applicationContextProvider the application context provider
     * @param validationHelper           the validation helper
     * @param dao                        the DAO
     * @return the user service
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UserService userService(final ApplicationContextProvider applicationContextProvider, final ValidationHelper validationHelper, final UserDao dao) throws Exception {
        return new DefaultUserService(applicationContextProvider, validationHelper, dao);
    }
}
