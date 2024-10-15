package org.infodavid.commons.restapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.util.jackson.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The Class RequestUtils.
 */
public class RequestUtils {

    /** The singleton. */
    private static WeakReference<RequestUtils> instance = null;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized RequestUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new RequestUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new utilities.
     */
    private RequestUtils() {
    }

    /**
     * Append body.
     * @param request the request
     * @param buffer  the buffer
     */
    public void appendBody(final HttpServletRequest request, final StringBuilder buffer) {
        final JsonUtils utils = JsonUtils.getInstance();
        buffer.append(";body=\n");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), request.getCharacterEncoding()))) {
            if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
                buffer.append(utils.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(utils.fromJson(reader.lines().collect(Collectors.joining(System.lineSeparator())))));
            } else {
                buffer.append(reader.lines().collect(Collectors.joining(System.lineSeparator())));
            }
        } catch (final IOException e) {
            LOGGER.debug("Cannot read request body: {}", e.getMessage());
        }
    }

    /**
     * Append cookies.
     * @param request the request
     * @param buffer  the buffer
     */
    public void appendCookies(final HttpServletRequest request, final StringBuilder buffer) {
        if (request.getCookies() != null) {
            buffer.append(";cookies=\n");

            for (final Cookie cookie : request.getCookies()) {
                buffer.append('\t');

                if (StringUtils.isNotEmpty(cookie.getDomain())) {
                    buffer.append(cookie.getDomain());
                    buffer.append(':');
                }

                buffer.append(cookie.getName());

                if (StringUtils.isNotEmpty(cookie.getPath())) {
                    buffer.append('@');
                    buffer.append(cookie.getPath());
                }

                buffer.append('=');
                buffer.append(cookie.getValue());
                buffer.append('\n');
            }
        }
    }

    /**
     * Append headers.
     * @param request the request
     * @param buffer  the buffer
     */
    public void appendHeaders(final HttpServletRequest request, final StringBuilder buffer) {
        final List<String> names = Collections.list(request.getHeaderNames());

        if (!names.isEmpty()) {
            buffer.append(";header=\n");

            for (final String name : names) {
                buffer.append('\t');
                buffer.append(name);
                buffer.append('=');
                buffer.append(request.getHeader(name));
                buffer.append('\n');
            }
        }
    }

    /**
     * Append parameters.
     * @param request the request
     * @param buffer  the buffer
     */
    public void appendParameters(final HttpServletRequest request, final StringBuilder buffer) {
        final List<String> names = Collections.list(request.getParameterNames());

        if (!names.isEmpty()) {
            buffer.append(";parameters=\n");

            for (final String name : names) {
                buffer.append('\t');
                buffer.append(name);
                buffer.append('=');
                buffer.append(request.getParameter(name));
                buffer.append('\n');
            }
        }
    }

    /**
     * Gets the description.
     * @param request the request
     * @return the description
     */
    public String getDescription(final HttpServletRequest request) {
        final StringBuilder buffer = new StringBuilder();
        getDescription(request, buffer);

        return buffer.toString();
    }

    /**
     * Gets the description.
     * @param request the request
     * @param buffer  the buffer
     * @return the description
     */
    public void getDescription(final HttpServletRequest request, final StringBuilder buffer) {
        appendHeaders(request, buffer);
        appendParameters(request, buffer);

        if (StringUtils.isNotEmpty(request.getLocalAddr())) {
            buffer.append(";localaddr=");
            buffer.append(request.getLocalAddr());
        }

        buffer.append(";localport=");
        buffer.append(request.getLocalPort());

        if (StringUtils.isNotEmpty(request.getRemoteAddr())) {
            buffer.append(";remoteaddr=");
            buffer.append(request.getRemoteAddr());
        }

        buffer.append(";remoteport=");
        buffer.append(request.getRemotePort());

        if (StringUtils.isNotEmpty(request.getAuthType())) {
            buffer.append(";authtype=");
            buffer.append(request.getAuthType());
        }

        if (StringUtils.isNotEmpty(request.getContentType())) {
            buffer.append(";contenttype=");
            buffer.append(request.getContentType());
        }

        if (StringUtils.isNotEmpty(request.getCharacterEncoding())) {
            buffer.append(";encoding=");
            buffer.append(request.getCharacterEncoding());
        }

        if (StringUtils.isNotEmpty(request.getMethod())) {
            buffer.append(";method=");
            buffer.append(request.getMethod());
        }

        if (StringUtils.isNotEmpty(request.getProtocol())) {
            buffer.append(";protocol=");
            buffer.append(request.getProtocol());
        }

        appendCookies(request, buffer);
        buffer.append(";length=");
        buffer.append(request.getContentLengthLong());
        appendBody(request, buffer);
    }

    /**
     * Gets the description.
     * @param request the request
     * @return the description
     */
    public String getDescription(final WebRequest request) {
        final StringBuilder buffer = new StringBuilder();
        getDescription(request, buffer);

        return buffer.toString();
    }

    /**
     * Gets the description.
     * @param request the request
     * @param buffer  the buffer
     * @return the description
     */
    public void getDescription(final WebRequest request, final StringBuilder buffer) {
        buffer.append(request.getDescription(true));

        if (request instanceof final NativeWebRequest nativeRequest) {
            if (nativeRequest.getNativeRequest() instanceof HttpServletRequest) {
                final HttpServletRequest httpServletRequest = (HttpServletRequest) nativeRequest.getNativeRequest();
                appendHeaders(httpServletRequest, buffer);
                appendParameters(httpServletRequest, buffer);

                if (StringUtils.isNotEmpty(httpServletRequest.getLocalAddr())) {
                    buffer.append(";localaddr=");
                    buffer.append(httpServletRequest.getLocalAddr());
                }

                buffer.append(";localport=");
                buffer.append(httpServletRequest.getLocalPort());

                if (StringUtils.isNotEmpty(httpServletRequest.getRemoteAddr())) {
                    buffer.append(";remoteaddr=");
                    buffer.append(httpServletRequest.getRemoteAddr());
                }

                buffer.append(";remoteport=");
                buffer.append(httpServletRequest.getRemotePort());

                if (StringUtils.isNotEmpty(httpServletRequest.getAuthType())) {
                    buffer.append(";authtype=");
                    buffer.append(httpServletRequest.getAuthType());
                }

                if (StringUtils.isNotEmpty(httpServletRequest.getContentType())) {
                    buffer.append(";contenttype=");
                    buffer.append(httpServletRequest.getContentType());
                }

                if (StringUtils.isNotEmpty(httpServletRequest.getCharacterEncoding())) {
                    buffer.append(";encoding=");
                    buffer.append(httpServletRequest.getCharacterEncoding());
                }

                if (StringUtils.isNotEmpty(httpServletRequest.getMethod())) {
                    buffer.append(";method=");
                    buffer.append(httpServletRequest.getMethod());
                }

                if (StringUtils.isNotEmpty(httpServletRequest.getProtocol())) {
                    buffer.append(";protocol=");
                    buffer.append(httpServletRequest.getProtocol());
                }

                appendCookies(httpServletRequest, buffer);
                buffer.append(";length=");
                buffer.append(httpServletRequest.getContentLengthLong());
                appendBody(httpServletRequest, buffer);
            }
        }
    }
}
