package org.infodavid.commons.authentication.rest.configuration;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.infodavid.commons.authentication.rest.security.AuthenticationJwtToken;
import org.infodavid.commons.authentication.rest.security.LoginAuthenticationProvider;
import org.infodavid.commons.authentication.rest.security.RestAuthenticationEntryPoint;
import org.infodavid.commons.authentication.rest.security.filter.BasicAuthenticationFilter;
import org.infodavid.commons.authentication.rest.security.filter.JwtTokenAuthenticationFilter;
import org.infodavid.commons.authentication.rest.security.filter.JwtTokenLogoutFilter;
import org.infodavid.commons.authentication.rest.security.filter.RestLoginAuthenticationFilter;
import org.infodavid.commons.rest.configuration.DefaultSecurityConfiguration;
import org.infodavid.commons.rest.security.handler.RequestAwareAuthenticationFailureHandler;
import org.infodavid.commons.rest.security.handler.RequestAwareAuthenticationSuccessHandler;
import org.infodavid.commons.service.security.AuthenticationBuilder;
import org.infodavid.commons.service.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * The Class AuthenticationSecurityConfiguration.
 */
public class AuthenticationSecurityConfiguration extends DefaultSecurityConfiguration { // @Configuration annotation must be set only by application and not in this common utilities class

    /** The authentication entry point. */
    protected AuthenticationEntryPoint authenticationEntryPoint;

    /** The authentication failure handler. */
    protected AuthenticationFailureHandler authenticationFailureHandler;

    /** The authentication secret. */
    @Value("${authentication.secret:secret}")
    protected String authenticationSecret;

    /** The authentication success handler. */
    protected AuthenticationSuccessHandler authenticationSuccessHandler;

    /** The basic authentication allowed. */
    @Value("${authentication.basic.allowed:true}")
    protected boolean basicAuthenticationAllowed;

    /** The login path. */
    @Value("${authentication.loginPath:/rest/login}")
    protected String loginPath;

    @Value("${authentication.logoutPath:/rest/logout}")
    protected String logoutPath;

    /**
     * Authentication entry point.
     * @return the authentication entry point
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    synchronized AuthenticationEntryPoint authenticationEntryPoint() {
        if (authenticationEntryPoint == null) {
            authenticationEntryPoint = new RestAuthenticationEntryPoint();
        }

        return authenticationEntryPoint;
    }

    /**
     * Authentication builder.
     * @return the authentication builder
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    synchronized AuthenticationBuilder authenticationTokenBuilder() {
        return new AuthenticationJwtToken.AuthenticationBuilderImpl(authenticationSecret);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.configuration.DefaultSecurityConfiguration#configure(org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry)
     */
    @Override
    protected void configure(final AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {
        super.configure(requests);
        requests.requestMatchers(antMatcher(HttpMethod.POST, loginPath)).permitAll() // Allow login for everyone
        .requestMatchers(antMatcher(HttpMethod.POST, logoutPath)).permitAll(); // Allow logout from everyone
        configureResourceAccess(requests, "/rest/user");
    }

    /**
     * Request aware authentication failure handler.
     * @return the authentication failure handler
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    synchronized AuthenticationFailureHandler requestAwareAuthenticationFailureHandler() {
        if (authenticationFailureHandler == null) {
            authenticationFailureHandler = new RequestAwareAuthenticationFailureHandler();
        }

        return authenticationFailureHandler;
    }

    /**
     * Request aware authentication success handler.
     * @return the authentication success handler
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    synchronized AuthenticationSuccessHandler requestAwareAuthenticationSuccessHandler() {
        if (authenticationSuccessHandler == null) {
            authenticationSuccessHandler = new RequestAwareAuthenticationSuccessHandler();
        }

        return authenticationSuccessHandler;
    }

    /**
     * Security filter chain.
     * @param http                  the configuration
     * @param authenticationService the authentication service
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    protected SecurityFilterChain securityFilterChain(final HttpSecurity http, final AuthenticationService authenticationService) throws Exception {
        final AuthenticationProvider authenticationProvider = new LoginAuthenticationProvider(authenticationService);
        final AuthenticationManager authenticationManager = new ProviderManager(authenticationProvider);
        final RestLoginAuthenticationFilter loginAuthenticationFilter = new RestLoginAuthenticationFilter(applicationContext);
        loginAuthenticationFilter.setAuthenticationManager(authenticationManager);
        final BasicAuthenticationFilter basicAuthenticationFilter = new BasicAuthenticationFilter(authenticationManager, applicationContext);
        basicAuthenticationFilter.setAllowed(basicAuthenticationAllowed);
        configureSecurityFilterChain(http);
        // Session and authentication management
        http.authenticationProvider(authenticationProvider).sessionManagement(management -> management.sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
                .sessionCreationPolicy(SessionCreationPolicy.NEVER))
        .exceptionHandling(handling -> handling.authenticationEntryPoint(authenticationEntryPoint()))
        .addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(basicAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new JwtTokenAuthenticationFilter(applicationContext, new AntPathRequestMatcher("/rest/**/*")), UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(new JwtTokenLogoutFilter(authenticationService, new AntPathRequestMatcher(logoutPath), new SecurityContextLogoutHandler()), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
