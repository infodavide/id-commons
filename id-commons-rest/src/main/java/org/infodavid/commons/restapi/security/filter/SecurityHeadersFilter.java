package org.infodavid.commons.restapi.security.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class SecurityHeadersFilter.
 */
public class SecurityHeadersFilter implements Filter {

    /** The headers. */
    private final Map<String, String> headers;

    /**
     * Instantiates a new filter.
     */
    public SecurityHeadersFilter() {
        headers = new HashMap<>();
        headers.put("X-Frame-Options", "SAMEORIGIN");
        headers.put("X-XSS-Protection", "1; mode=block;");
        headers.put("X-Content-Type-Options", "nosniff");
        headers.put("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        headers.put("Referrer-Policy", "no-referrer");
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpServletResponse) {
            final Set<String> existing = new HashSet<>(httpServletResponse.getHeaderNames());
            headers.entrySet().stream().filter(h -> !existing.contains(h.getKey())).forEach(h -> httpServletResponse.addHeader(h.getKey(), h.getValue()));
        }

        chain.doFilter(request, response);
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    /*
     * (non-Javadoc)
     * @see com.thetransactioncompany.cors.CORSFilter#init(javax.servlet.FilterConfig)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void init(final FilterConfig config) throws ServletException {
        final Enumeration params = config.getInitParameterNames();

        // default in web.xml
        while (params.hasMoreElements()) {
            final String key = (String) params.nextElement();
            final String value = config.getInitParameter(key);

            if (StringUtils.isNotEmpty(value)) {
                headers.put(key, value);
            } else {
                headers.remove(key);
            }
        }
    }
}
