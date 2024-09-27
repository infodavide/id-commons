package org.infodavid.commons.restapi;

import org.infodavid.commons.restapi.configuration.UniqueNameGenerator;
import org.infodavid.commons.restapi.controller.DefaultApplicationController;
import org.infodavid.commons.restapi.controller.DefaultUserController;
import org.infodavid.commons.restapi.security.AuthenticationJwtToken;
import org.infodavid.commons.restapi.security.LoginAuthenticationProvider;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.UserService;
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
     * @param service the service
     * @return the application controller
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    DefaultApplicationController applicationController(final ApplicationService service) {
        return new DefaultApplicationController(service);
    }

    /**
     * Application service.
     * @return the application service
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    ApplicationService applicationService() {
        return Mockito.mock(ApplicationService.class);
    }

    /**
     * Authentication service.
     * @return the authentication service
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    FactoryBean<AuthenticationService> authenticationService() {
        return new FactoryBean<>() {
            @Override
            public AuthenticationService getObject() throws Exception { return Mockito.mock(AuthenticationService.class); }

            @Override
            public Class<?> getObjectType() { return AuthenticationService.class; }
        };
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
     * Login authentication provider.
     * @param service                    the service
     * @param authenticationTokenBuilder the authentication token builder
     * @return the login authentication provider
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    LoginAuthenticationProvider loginAuthenticationProvider(final AuthenticationService service, final AuthenticationJwtToken.Builder authenticationTokenBuilder) {
        return new LoginAuthenticationProvider(service, authenticationTokenBuilder);
    }

    /**
     * User controller.
     * @param service the service
     * @return the user controller
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    DefaultUserController userController(final UserService service) {
        return new DefaultUserController(service);
    }

    /**
     * User service.
     * @return the user service
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    UserService userService() {
        return Mockito.mock(UserService.class);
    }
}
