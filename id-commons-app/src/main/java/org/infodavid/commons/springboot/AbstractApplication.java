package org.infodavid.commons.springboot;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractApplication.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class AbstractApplication {

    /**
     * The main method.
     * @param applicationClass the application class
     * @param args             the arguments
     * @return the spring application
     */
    public static SpringApplication main(final Class<?> applicationClass, final String[] args) {
        final SpringApplicationBuilder builder = new SpringApplicationBuilder(applicationClass);
        builder.bannerMode(Mode.OFF);
        builder.headless(true);
        builder.logStartupInfo(true);
        final SpringApplication application = builder.build();
        application.setAllowBeanDefinitionOverriding(true);

        if (args != null) {
            final Map<String, Object> proeprties = new HashMap<>();

            for (final String arg : args) {
                if (arg.contains("=")) {
                    final String[] parts = arg.split("=");
                    proeprties.put(parts[0], parts[1]);
                }
            }

            LOGGER.info("Using arguments: {}", proeprties);
            application.setDefaultProperties(proeprties);
        }

        return application;
    }
}
