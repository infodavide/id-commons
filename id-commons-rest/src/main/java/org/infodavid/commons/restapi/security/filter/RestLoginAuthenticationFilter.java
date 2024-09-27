package org.infodavid.commons.restapi.security.filter;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.User;
import org.infodavid.commons.restapi.Constants;
import org.infodavid.commons.restapi.dto.LoginDto;
import org.infodavid.commons.restapi.dto.UserDto;
import org.infodavid.commons.restapi.mapper.UserMapper;
import org.infodavid.commons.security.AnonymousAuthenticationImpl;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.util.jackson.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
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

/**
 * The Class RestLoginAuthenticationFilter.
 */
public class RestLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RestLoginAuthenticationFilter.class);

    /** The application service. */
    private final ApplicationService applicationService;

    /**
     * Instantiates a new authentication filter.
     * @param applicationContext the application context
     */
    public RestLoginAuthenticationFilter(final ApplicationContext applicationContext) {
        super(new AntPathRequestMatcher("/rest/login", "POST"));
        applicationService = applicationContext.getBean(ApplicationService.class);

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
        SecurityContextHolder.getContext().setAuthentication(AnonymousAuthenticationImpl.INSTANCE);

        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LOGGER.debug("Attempting authentication based on username and password passed as JSON in the body of the request");
        final LoginDto dto = JsonUtils.getInstance().fromJson(IOUtils.toString(request.getReader()), LoginDto.class);
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

        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request, response));
        final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(name, password);
        final Authentication result = getAuthenticationManager().authenticate(authRequest);
        LOGGER.debug("Authentication: {}", result);
        final Object principal = result.getPrincipal();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (result instanceof final Authentication token) {
            response.addHeader(Constants.HTTP_AUTHORIZATION_RESPONSE_HEADER, Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX + token);
        }

        String inactivityTimeout = String.valueOf(org.infodavid.commons.service.Constants.DEFAULT_SESSION_INACTIVITY_TIMEOUT);

        try {
            final List<ApplicationProperty> found = applicationService.findByName(org.infodavid.commons.service.Constants.SESSION_INACTIVITY_TIMEOUT_PROPERTY, Pageable.unpaged()).getContent();

            if (!found.isEmpty()) {
                inactivityTimeout = found.get(0).getValue();
            }
        } catch (final ServiceException e) {
            LOGGER.warn("Cannot retrieve the property: " + org.infodavid.commons.service.Constants.SESSION_INACTIVITY_TIMEOUT_PROPERTY, e);
        }

        response.addHeader(Constants.HTTP_SESSION_INACTIVITY_TIMEOUT_HEADER, inactivityTimeout);

        if (principal instanceof final User user) {
            final UserDto userDto = UserMapper.INSTANCE.map(user);
            // Do not return the password
            userDto.setPassword(null);
            JsonUtils.getInstance().toJson(response.getOutputStream(), userDto);
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
        if (request instanceof final HttpServletRequest httpServletRequest && FilterUtils.getInstance().isResource(httpServletRequest)) {
            chain.doFilter(request, response);

            return;
        }

        super.doFilter(request, response, chain);
    }
}
