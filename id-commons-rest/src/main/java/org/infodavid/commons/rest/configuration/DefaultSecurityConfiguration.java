package org.infodavid.commons.rest.configuration;

import org.infodavid.commons.rest.security.filter.JsonContentCachingFilter;
import org.infodavid.commons.rest.security.filter.SecurityHeadersFilter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;

/**
 * The Class DefaultSecurityConfiguration.
 */
public class DefaultSecurityConfiguration implements ApplicationContextAware { // @Configuration annotation must be set only by application and not in this common utilities class

    /** The Constant DEFAULT_LOGIN_PATH. */
    protected static final String DEFAULT_LOGIN_PATH = "/rest/login"; // NOSONAR

    /** The Constant DEFAULT_LOGOUT_PATH. */
    protected static final String DEFAULT_LOGOUT_PATH = "/rest/logout"; // NOSONAR

    /**
     * Configure resource access.
     * @param requests the HTTP security object
     * @param path     the path
     */
    protected static void configureResourceAccess(final AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests, final String path) {
        requests.requestMatchers(HttpMethod.GET, path).permitAll().requestMatchers(HttpMethod.GET, path + "/*").permitAll().requestMatchers(HttpMethod.GET, path + "/**/*").permitAll().requestMatchers(HttpMethod.POST, path).authenticated().requestMatchers(HttpMethod.POST, path + "/*").authenticated().requestMatchers(HttpMethod.DELETE, path + "/*").authenticated();
    }

    /**
     * Configure STOMP access.
     * @param requests the HTTP security object
     * @param path     the path
     */
    protected static void configureStompAccess(final AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests, final String path) {
        requests.requestMatchers(HttpMethod.GET, path).permitAll().requestMatchers(HttpMethod.GET, path + "/*").permitAll().requestMatchers(HttpMethod.GET, path + "/**/*").permitAll().requestMatchers(HttpMethod.GET, path + "/**").permitAll().requestMatchers(HttpMethod.POST, path).permitAll().requestMatchers(HttpMethod.POST, path + "/*").permitAll().requestMatchers(HttpMethod.POST, path + "/**").permitAll().requestMatchers(HttpMethod.DELETE, path + "/*").permitAll();
    }

    /** The application context. */
    protected ApplicationContext applicationContext;

    /**
     * Security filter chain.
     * @param http the configuration
     * @throws Exception the exception
     */
    protected void configureSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.httpBasic(basic -> basic.disable()).cors(cors -> cors.disable()).csrf(csrf -> csrf.disable()).authorizeHttpRequests(requests -> {
            configureStompAccess(requests, "/stomp");
            configure(requests);
            requests.requestMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
            .requestMatchers(HttpMethod.GET, "/resources/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/css/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/fonts/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/images/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/js/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/templates/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/swagger-docs/**").permitAll()
            .requestMatchers("/swagger-ui/*", "/swagger-ui.html", "/webjars/**", "/v2/**", "/swagger-resources/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/rest/app/about").permitAll()
            .requestMatchers(HttpMethod.GET, "/rest/app/health").permitAll()
            .requestMatchers(HttpMethod.GET, "/rest/app/release_note").permitAll()
            .requestMatchers(HttpMethod.GET, "/rest/license").permitAll()
            .requestMatchers("/rest/**/*").authenticated().requestMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().authenticated();
        })
        // Session management
        .sessionManagement(management -> management.sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
                .sessionCreationPolicy(SessionCreationPolicy.NEVER))
        .logout(logout -> logout.clearAuthentication(true).invalidateHttpSession(true))
        .addFilterBefore(new JsonContentCachingFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new SecurityHeadersFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Configure.
     * @param requests the requests
     */
    protected void configure(final AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
