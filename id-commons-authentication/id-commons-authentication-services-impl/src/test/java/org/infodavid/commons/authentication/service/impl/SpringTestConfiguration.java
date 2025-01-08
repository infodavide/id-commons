package org.infodavid.commons.authentication.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.infodavid.commons.authentication.persistence.dao.GroupDao;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.authentication.service.GroupService;
import org.infodavid.commons.authentication.service.UserService;
import org.infodavid.commons.authentication.service.impl.security.DefaultAuthenticationService;
import org.infodavid.commons.authentication.service.impl.security.DefaultAuthorizationService;
import org.infodavid.commons.authentication.service.test.persistence.dao.GroupDaoMock;
import org.infodavid.commons.authentication.service.test.persistence.dao.UserDaoMock;
import org.infodavid.commons.persistence.dao.ConfigurationPropertyDao;
import org.infodavid.commons.persistence.dao.DefaultDao;
import org.infodavid.commons.service.SchedulerService;
import org.infodavid.commons.service.impl.DefaultConfigurationManager;
import org.infodavid.commons.service.impl.DefaultSchedulerService;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.service.test.persistence.PlatformTransactionManagerMock;
import org.infodavid.commons.service.test.persistence.dao.ConfigurationPropertyDaoMock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
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
     * @param applicationContext   the application context
     * @param authorizationService the authorization service
     * @param dao                  the data access object
     * @return the default configuration manager
     */
    @Bean("applicationConfigurationManager")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DefaultConfigurationManager applicationConfigurationManager(final ApplicationContext applicationContext, final AuthorizationService authorizationService, final ConfigurationPropertyDao dao) {
        return new DefaultConfigurationManager(LoggerFactory.getLogger("ApplicationConfigurationManager"), applicationContext, authorizationService, dao, org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
    }

    /**
     * Authentication configuration manager.
     * @param applicationContext   the application context
     * @param authorizationService the authorization service
     * @param dao                  the data access object
     * @return the default configuration manager
     */
    @Bean("authenticationConfigurationManager")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DefaultConfigurationManager authenticationConfigurationManager(final ApplicationContext applicationContext, final AuthorizationService authorizationService, final ConfigurationPropertyDao dao) {
        return new DefaultConfigurationManager(LoggerFactory.getLogger("AuthenticationConfigurationManager"), applicationContext, authorizationService, dao, org.infodavid.commons.authentication.service.Constants.AUTHENTICATION_SCOPE);
    }

    /**
     * Authentication service.
     * @param applicationContext the application context
     * @param userDao            the user data access object
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FactoryBean<AuthenticationService> authenticationService(final ApplicationContext applicationContext, final UserDao userDao, @Qualifier("authenticationConfigurationManager") final DefaultConfigurationManager configurationManager) throws Exception {
        return new FactoryBean<>() {

            /*
             * (non-Javadoc)
             * @see org.springframework.beans.factory.FactoryBean#getObject()
             */
            @Override
            public AuthenticationService getObject() throws Exception {
                return new DefaultAuthenticationService(LoggerFactory.getLogger(AuthenticationService.class), applicationContext, userDao, null, configurationManager);
            }

            @Override
            public Class<?> getObjectType() {
                return AuthenticationService.class;
            }
        };
    }

    /**
     * Authorization service.
     * @param applicationContext the application context
     * @return the factory bean
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FactoryBean<AuthorizationService> authorizationService(final ApplicationContext applicationContext) {
        return new FactoryBean<>() {

            /*
             * (non-Javadoc)
             * @see org.springframework.beans.factory.FactoryBean#getObject()
             */
            @Override
            public AuthorizationService getObject() throws Exception {
                return new DefaultAuthorizationService(LoggerFactory.getLogger("AuthorizationService"), applicationContext);
            }

            @Override
            public Class<?> getObjectType() {
                return AuthorizationService.class;
            }
        };
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
     * Group data access object.
     * @return the data access object
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GroupDao groupDao() throws Exception {
        return new GroupDaoMock();
    }

    /**
     * Group service.
     * @param applicationContext   the application context
     * @param authorizationService the authorization service
     * @param dao                  the data access object
     * @return the group service
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GroupService groupService(final ApplicationContext applicationContext, final AuthorizationService authorizationService, final GroupDao dao) throws Exception {
        return new DefaultGroupService(LoggerFactory.getLogger(GroupService.class), applicationContext, authorizationService, dao);
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
     * @param userDao  the user data access object
     * @param groupDao the group data access object
     * @return the transaction manager
     */
    @SuppressWarnings("rawtypes")
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public PlatformTransactionManager transactionManager(final UserDao userDao, final GroupDao groupDao) {
        final Collection<DefaultDao> dao = new ArrayList<>();
        dao.add(groupDao);
        dao.add(userDao);

        return new PlatformTransactionManagerMock(dao);
    }

    /**
     * User data access object.
     * @return the data access object
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UserDao userDao(final GroupDao groupDao) throws Exception {
        return new UserDaoMock(groupDao);
    }

    /**
     * User service.
     * @param applicationContext    the application context
     * @param authorizationService  the authorization service
     * @param dao                   the data access object
     * @param authenticationService the authentication service
     * @param groupService          the group service
     * @return the user service
     * @throws Exception the exception
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UserService userService(final ApplicationContext applicationContext, final AuthorizationService authorizationService, final UserDao dao, final AuthenticationService authenticationService, final GroupService groupService) throws Exception {
        return new DefaultUserService(LoggerFactory.getLogger(UserService.class), applicationContext, authorizationService, dao, authenticationService, groupService);
    }
}
