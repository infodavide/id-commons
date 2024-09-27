package org.infodavid.commons.restapi.security.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * The Class RewrittenHttpServletRequestWrapper.
 */
class RewrittenHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /** The content. */
    private byte[] content = null;

    /**
     * Instantiates a new rewritten http servlet request wrapper.
     * @param request the request
     * @param content the content
     */
    RewrittenHttpServletRequestWrapper(final HttpServletRequest request, final byte[] content) {
        super(request);
        this.content = content;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequestWrapper#getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ByteArrayServletInputStream(content);// NOSONAR Already improved
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequestWrapper#getReader()
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}
