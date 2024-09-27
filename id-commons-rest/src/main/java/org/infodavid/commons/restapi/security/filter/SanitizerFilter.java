package org.infodavid.commons.restapi.security.filter;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.google.json.JsonSanitizer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The Class SanitizerFilter.
 */
public class SanitizerFilter implements Filter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SanitizerFilter.class);

    /** The max content length. */
    private int maxContentLength = 100 * 1024;

    /**
     * Instantiates a new sanitizer filter.
     */
    public SanitizerFilter() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (!MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Sanitization skipped, content type is not {}", MediaType.APPLICATION_JSON_VALUE);
            }

            chain.doFilter(request, response);

            return;
        }

        if (request.getContentLength() > maxContentLength) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Sanitization skipped, length is greater than the maximum content length");
            }

            chain.doFilter(request, response);

            return;
        }

        Charset charset = StandardCharsets.UTF_8;
        byte[] sanitized;

        if (StringUtils.isNotEmpty(request.getCharacterEncoding())) {
            charset = Charset.forName(request.getCharacterEncoding());
        }

        try (Reader reader = request.getReader()) {
            sanitized = JsonSanitizer.sanitize(IOUtils.toString(reader)).getBytes(charset);
        }

        if (request instanceof HttpServletRequest httpServletRequest) {
            chain.doFilter(new RewrittenHttpServletRequestWrapper(httpServletRequest, sanitized), response);

            return;
        }

        chain.doFilter(new RewrittenServletRequestWrapper(request, sanitized), response);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String value = filterConfig.getInitParameter("sanitizer.maxContentLength");

        if (StringUtils.isNumeric(value)) {
            maxContentLength = Integer.parseInt(value);
        }
    }
}
