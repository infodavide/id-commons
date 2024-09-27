package org.infodavid.commons.restapi.security.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;

/**
 * The Class RewrittenServletRequestWrapper.
 */
class RewrittenServletRequestWrapper extends ServletRequestWrapper {

    /** The content. */
    private byte[] content = null;

    /**
     * Instantiates a new rewritten servlet request wrapper.
     * @param request the request
     * @param content the content
     */
    RewrittenServletRequestWrapper(final ServletRequest request, final byte[] content) {
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
