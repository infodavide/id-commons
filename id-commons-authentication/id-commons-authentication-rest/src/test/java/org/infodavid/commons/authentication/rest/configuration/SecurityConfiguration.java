package org.infodavid.commons.authentication.rest.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * The Class AuthenticationSecurityConfiguration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends AuthenticationSecurityConfiguration {
    // noop
}
