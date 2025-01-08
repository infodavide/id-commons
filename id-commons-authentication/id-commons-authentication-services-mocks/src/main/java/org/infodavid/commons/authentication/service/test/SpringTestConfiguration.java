package org.infodavid.commons.authentication.service.test;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.infodavid.commons.authentication.service.GroupService;
import org.infodavid.commons.authentication.service.UserService;
import org.infodavid.commons.service.ConfigurationManager;
import org.infodavid.commons.service.SchedulerService;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.service.test.ConfigurationManagerMock;
import org.infodavid.commons.service.test.persistence.PlatformTransactionManagerMock;
import org.mockito.Mockito;
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

    /** The principals. */
    protected Map<Principal, String> principals = new HashMap<>();

    /**
     * Application configuration manager.
     * @return the default configuration manager
     */
    @Bean("applicationConfigurationManager")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ConfigurationManager applicationConfigurationManager() {
        return new ConfigurationManagerMock(org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
    }

    /**
     * Authentication service.
     * @param userService the user service
     * @return the factory bean
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FactoryBean<AuthenticationService> authenticationService(final UserService userService) {
        return new FactoryBean<>() {

            /*
             * (non-Javadoc)
             * @see org.springframework.beans.factory.FactoryBean#getObject()
             */
            @Override
            public AuthenticationService getObject() throws Exception {
                return new AuthenticationServiceMock(userService);
            }

            @Override
            public Class<?> getObjectType() {
                return AuthenticationService.class;
            }
        };
    }

    /**
     * Authorization service.
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
                return new AuthorizationServiceMock(applicationContext);
            }

            @Override
            public Class<?> getObjectType() {
                return AuthorizationService.class;
            }
        };
    }

    /**
     * Group service.
     * @return the group service
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GroupService groupService() {
        return new GroupServiceMock();
    }

    /**
     * Scheduler service.
     * @param applicationContext the application context
     * @return the scheduler service
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SchedulerService schedulerService(final ApplicationContext applicationContext) {
        return Mockito.mock(SchedulerService.class);
    }

    /**
     * Transaction manager.
     * @return the transaction manager
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public PlatformTransactionManager transactionManager() {
        return new PlatformTransactionManagerMock();
    }

    /**
     * User service.
     * @return the user service
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UserService userService() {
        return new UserServiceMock();
    }
}
