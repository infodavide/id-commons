package org.infodavid.commons.rest;

import org.infodavid.commons.rest.configuration.UniqueNameGenerator;
import org.infodavid.commons.rest.v1.controller.DefaultApplicationController;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.security.AuthenticationService;
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
}
