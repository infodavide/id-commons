package org.infodavid.commons.rest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * The Class ApplicationConfiguration.
 */
@Configuration
public class ApplicationConfiguration {

    /**
     * MVC handler mapping introspector.
     * @return the handler mapping introspector
     */
    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
}
