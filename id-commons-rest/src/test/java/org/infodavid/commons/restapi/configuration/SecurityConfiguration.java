package org.infodavid.commons.restapi.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * The Class SecurityConfiguration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends AbstractSecurityConfiguration {
    // noop
}
