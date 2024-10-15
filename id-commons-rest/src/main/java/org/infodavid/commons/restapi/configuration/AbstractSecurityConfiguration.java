package org.infodavid.commons.restapi.configuration;

import org.infodavid.commons.restapi.security.AuthenticationJwtToken;
import org.infodavid.commons.restapi.security.LoginAuthenticationProvider;
import org.infodavid.commons.restapi.security.RestAuthenticationEntryPoint;
import org.infodavid.commons.restapi.security.filter.BasicAuthenticationFilter;
import org.infodavid.commons.restapi.security.filter.JsonContentCachingFilter;
import org.infodavid.commons.restapi.security.filter.JwtTokenAuthenticationFilter;
import org.infodavid.commons.restapi.security.filter.JwtTokenLogoutFilter;
import org.infodavid.commons.restapi.security.filter.RestLoginAuthenticationFilter;
import org.infodavid.commons.restapi.security.filter.SecurityHeadersFilter;
import org.infodavid.commons.restapi.security.handler.RequestAwareAuthenticationFailureHandler;
import org.infodavid.commons.restapi.security.handler.RequestAwareAuthenticationSuccessHandler;
import org.infodavid.commons.security.AuthenticationService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
 * The Class abstractSecurityConfiguration.
 */
public abstract class AbstractSecurityConfiguration implements ApplicationContextAware { // @Configuration annotation must be set only by application and not in this common utilities class

    /** The Constant LOGOUT_PATH. */
    protected static final String LOGOUT_PATH = "/rest/logout"; // NOSONAR

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
    private ApplicationContext applicationContext;

    /** The authentication entry point. */
    private AuthenticationEntryPoint authenticationEntryPoint;

    /** The authentication failure handler. */
    private AuthenticationFailureHandler authenticationFailureHandler;

    /** The authentication secret. */
    @Value("${authentication.secret}")
    private String authenticationSecret;

    /** The authentication success handler. */
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    /** The basic authentication allowed. */
    @Value("${basicAuthentication.allowed:true}")
    private boolean basicAuthenticationAllowed;

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
     * Authentication token builder.
     * @return the authentication builder
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    synchronized AuthenticationJwtToken.Builder authenticationTokenBuilder() {
        return new AuthenticationJwtToken.Builder(authenticationSecret);
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
     * @param http                       the configuration
     * @param authenticationService      the authentication service
     * @param authenticationTokenBuilder the authentication token builder
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity http, final AuthenticationService authenticationService, final AuthenticationJwtToken.Builder authenticationTokenBuilder) throws Exception {
        final AuthenticationProvider authenticationProvider = new LoginAuthenticationProvider(authenticationService, authenticationTokenBuilder);
        final AuthenticationManager authenticationManager = new ProviderManager(authenticationProvider);
        final RestLoginAuthenticationFilter loginAuthenticationFilter = new RestLoginAuthenticationFilter(applicationContext);
        loginAuthenticationFilter.setAuthenticationManager(authenticationManager);
        final BasicAuthenticationFilter basicAuthenticationFilter = new BasicAuthenticationFilter(authenticationManager, applicationContext);
        basicAuthenticationFilter.setAllowed(basicAuthenticationAllowed);
        http.httpBasic(basic -> basic.disable()).cors(cors -> cors.disable()).csrf(csrf -> csrf.disable()).authorizeHttpRequests(requests -> {
            configureStompAccess(requests, "/stomp");
            configureResourceAccess(requests, "/rest/app/param");
            requests.requestMatchers(HttpMethod.POST, "/rest/login").permitAll() // Allow login for everyone
            .requestMatchers(HttpMethod.POST, LOGOUT_PATH).permitAll() // Allow logout from everyone
            // permit all
            .requestMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
            .requestMatchers(HttpMethod.GET, "/resources/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/css/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/fonts/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/images/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/js/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/templates/**/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/swagger-docs/**")
            .permitAll().requestMatchers("/swagger-ui/*", "/swagger-ui.html", "/webjars/**", "/v2/**", "/swagger-resources/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/rest/app/about").permitAll()
            .requestMatchers(HttpMethod.GET, "/rest/app/health")
            .permitAll().requestMatchers(HttpMethod.GET, "/rest/app/release_note").permitAll()
            .requestMatchers(HttpMethod.GET, "/rest/license").permitAll()
            // Allow post from browser to log errors
            .requestMatchers(HttpMethod.POST, "/rest/log").permitAll()
            // Allow listing of users for everyone
            .requestMatchers(HttpMethod.GET, "/rest/user/references").permitAll()
            .requestMatchers(HttpMethod.GET, "/rest/user/generate").permitAll()
            // Allow only if authenticated
            .requestMatchers(HttpMethod.POST, "/rest/user/*").authenticated()
            .requestMatchers(HttpMethod.POST, "/rest/shutdown").authenticated()
            .requestMatchers(HttpMethod.POST, "/rest/network/*").authenticated()
            .requestMatchers(HttpMethod.POST, "/rest/system/*").authenticated()
            .requestMatchers("/rest/**/*").authenticated().requestMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().authenticated();
        })
        // Session and authentication management
        .authenticationProvider(authenticationProvider).sessionManagement(management -> management.sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy()).sessionCreationPolicy(SessionCreationPolicy.NEVER)).exceptionHandling(handling -> handling.authenticationEntryPoint(authenticationEntryPoint())).logout(logout -> logout.clearAuthentication(true).invalidateHttpSession(true)).addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new JsonContentCachingFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(basicAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).addFilterBefore(new JwtTokenAuthenticationFilter(applicationContext, new AntPathRequestMatcher("/rest/**/*"), authenticationTokenBuilder), UsernamePasswordAuthenticationFilter.class).addFilterAfter(new SecurityHeadersFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(new JwtTokenLogoutFilter(authenticationService, new AntPathRequestMatcher(LOGOUT_PATH), new SecurityContextLogoutHandler()), UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
