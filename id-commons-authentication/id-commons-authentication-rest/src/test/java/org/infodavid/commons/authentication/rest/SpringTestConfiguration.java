package org.infodavid.commons.authentication.rest;

import org.infodavid.commons.authentication.rest.security.LoginAuthenticationProvider;
import org.infodavid.commons.authentication.rest.v1.controller.DefaultGroupController;
import org.infodavid.commons.authentication.rest.v1.controller.DefaultUserController;
import org.infodavid.commons.authentication.service.GroupService;
import org.infodavid.commons.authentication.service.UserService;
import org.infodavid.commons.rest.configuration.UniqueNameGenerator;
import org.infodavid.commons.rest.v1.controller.DefaultApplicationController;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.service.security.AuthorizationService;
import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

/**
 * The Class SpringTestConfiguration.
 */
@Configuration
@ComponentScan(basePackages = {
        "org.infodavid"
}, nameGenerator = UniqueNameGenerator.class)
@PropertySource("classpath:application-test.properties")
@TestPropertySource("classpath:application-test.properties")
public class SpringTestConfiguration {

    /**
     * Application controller.
     * @param manager the manager
     * @return the application controller
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    DefaultApplicationController applicationController(final ApplicationService service) {
        return new DefaultApplicationController(service);
    }

    /**
     * Application manager.
     * @return the application manager
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    ApplicationService applicationService() {
        return Mockito.mock(ApplicationService.class);
    }

    /**
     * Authentication manager.
     * @return the authentication manager
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    FactoryBean<AuthenticationService> authenticationService() {
        return new FactoryBean<>() {
            @Override
            public AuthenticationService getObject() throws Exception {
                return Mockito.mock(AuthenticationService.class);
            }

            @Override
            public Class<?> getObjectType() {
                return AuthenticationService.class;
            }
        };
    }

    /**
     * Authorization manager.
     * @return the authorization manager
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    FactoryBean<AuthorizationService> authorizationService() {
        return new FactoryBean<>() {
            @Override
            public AuthorizationService getObject() throws Exception {
                return Mockito.mock(AuthorizationService.class);
            }

            @Override
            public Class<?> getObjectType() {
                return AuthorizationService.class;
            }
        };
    }

    /**
     * Group controller.
     * @param authorizationService the authorization manager
     * @param manager              the manager
     * @return the default group controller
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    DefaultGroupController groupController(final AuthorizationService authorizationService, final GroupService service) {
        return new DefaultGroupController(authorizationService, service);
    }

    /**
     * Group manager.
     * @return the group manager
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    GroupService groupService() {
        return Mockito.mock(GroupService.class);
    }

    /**
     * Login authentication provider.
     * @param manager the manager
     * @return the login authentication provider
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    LoginAuthenticationProvider loginAuthenticationProvider(final AuthenticationService service) {
        return new LoginAuthenticationProvider(service);
    }

    /**
     * Gets the rest template.
     * @return the rest template
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * User controller.
     * @param authorizationService  the authorization manager
     * @param authenticationService the authentication service
     * @param service               the service
     * @return the user controller
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    DefaultUserController userController(final AuthorizationService authorizationService, final AuthenticationService authenticationService, final UserService service) {
        return new DefaultUserController(authorizationService, authenticationService, service);
    }

    /**
     * User manager.
     * @return the user manager
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    UserService userService() {
        return Mockito.mock(UserService.class);
    }
}
