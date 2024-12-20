package org.infodavid.commons.authentication.rest.security.filter;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.rest.security.AuthenticationJwtToken;
import org.infodavid.commons.authentication.rest.v1.api.dto.LoginDto;
import org.infodavid.commons.authentication.rest.v1.api.dto.UserDto;
import org.infodavid.commons.authentication.rest.v1.mapper.UserMapper;
import org.infodavid.commons.rest.security.filter.FilterUtils;
import org.infodavid.commons.util.jackson.JsonUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class RestLoginAuthenticationFilter.
 */
@Slf4j
public class RestLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * Instantiates a new authentication filter.
     * @param applicationContext the application context
     */
    public RestLoginAuthenticationFilter(final ApplicationContext applicationContext) {
        super(new AntPathRequestMatcher("/rest/login", "POST"));

        if (getSuccessHandler() instanceof final SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler) {
            savedRequestAwareAuthenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(true);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter# attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("resource")
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        SecurityContextHolder.clearContext();

        if (LOGGER.isDebugEnabled()) {
            if (SecurityContextHolder.getContext() == null) {
                LOGGER.debug("No security context available");
            } else {
                LOGGER.debug("Security context has authentication: {}", SecurityContextHolder.getContext().getAuthentication());
            }
        }

        LOGGER.debug("Attempting authentication based on username and password passed as JSON in the body of the request");
        final LoginDto dto = JsonUtils.fromJson(IOUtils.toString(request.getReader()), LoginDto.class);
        final String name;

        if (dto.name() == null) {
            name = "";
        } else {
            name = dto.name().trim();
        }

        final String password;

        if (dto.password() == null) {
            password = "";
        } else {
            password = dto.password();
        }

        LOGGER.info("Login request from: {} for principal: {}", request.getRemoteAddr(), dto.name());
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request, response));
        final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(name, password);
        final Authentication result = getAuthenticationManager().authenticate(authRequest);
        LOGGER.debug("Authentication: {}", result);
        final Object principal = result.getPrincipal();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (result instanceof final AuthenticationJwtToken token) {
            response.addHeader(org.infodavid.commons.rest.Constants.HTTP_AUTHORIZATION_RESPONSE_HEADER, org.infodavid.commons.rest.Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX + token.getToken());
        }

        if (principal instanceof final User user) {
            final UserDto userDto = UserMapper.INSTANCE.map(user);
            // Do not return the password
            userDto.setPassword(null);
            JsonUtils.toJson(response.getOutputStream(), userDto);
        } else {
            LOGGER.warn("Principal is not a User: {}", principal);
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        Authentication authentication = null;

        if (SecurityContextHolder.getContext() != null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }

        if (authentication != null && authentication.isAuthenticated()) {
            LOGGER.debug("Already authenticated");
            chain.doFilter(request, response);

            return;
        }

        if (request instanceof final HttpServletRequest httpServletRequest && FilterUtils.isResource(httpServletRequest)) {
            chain.doFilter(request, response);

            return;
        }

        super.doFilter(request, response, chain);
    }
}
