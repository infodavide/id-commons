package org.infodavid.commons.restapi.security.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class JsonContentCachingFilter.
 */
public class JsonContentCachingFilter extends OncePerRequestFilter {

    /**
     * The Class CachedBodyServletInputStream.
     */
    @Slf4j
    private static class CachedBodyServletInputStream extends ServletInputStream {

        /** The cached body input stream. */
        private final InputStream cachedBodyInputStream;

        /**
         * Instantiates a new servlet input stream.
         * @param cachedBody the cached body
         */
        public CachedBodyServletInputStream(final byte[] cachedBody) {
            cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletInputStream#isFinished()
         */
        @Override
        public boolean isFinished() {
            try {
                return cachedBodyInputStream.available() == 0;
            } catch (final IOException e) {
                LOGGER.error("Error checking if the input stream is finished", e);
            }

            return false;
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletInputStream#isReady()
         */
        @Override
        public boolean isReady() {
            return true;
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletInputStream#setReadListener(javax.servlet.ReadListener)
         */
        @Override
        public void setReadListener(final ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see java.io.InputStream#read()
         */
        @Override
        public int read() throws IOException {
            return cachedBodyInputStream.read();
        }
    }

    /**
     * The Class CachedBodyHttpServletRequest.
     */
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

        /** The cached body. */
        private final byte[] cachedBody;

        /**
         * Instantiates a new HTTP servlet request.
         * @param request the request
         * @throws IOException Signals that an I/O exception has occurred.
         */
        @SuppressWarnings("resource")
        public CachedBodyHttpServletRequest(final HttpServletRequest request) throws IOException {
            super(request);
            cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequestWrapper#getInputStream()
         */
        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new CachedBodyServletInputStream(cachedBody);
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequestWrapper#getReader()
         */
        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(cachedBody)));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    protected void doFilterInternal(final @NonNull HttpServletRequest request, final @NonNull HttpServletResponse response, final @NonNull FilterChain chain) throws ServletException, IOException {
        if (request.getContentLength() <= Integer.MAX_VALUE && request.getContentType() != null && request.getContentType().startsWith("application/json")) {
            chain.doFilter(new CachedBodyHttpServletRequest(request), response);

            return;
        }

        chain.doFilter(request, response);
    }
}
