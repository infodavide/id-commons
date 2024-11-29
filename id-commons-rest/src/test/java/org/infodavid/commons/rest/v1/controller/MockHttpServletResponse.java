package org.infodavid.commons.rest.v1.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class MockHttpServletResponse.
 */
public class MockHttpServletResponse implements HttpServletResponse {

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(final Locale loc) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
     */
    @Override
    public void setContentType(final String type) {
        // noop

    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#setContentLengthLong(long)
     */
    @Override
    public void setContentLengthLong(final long len) {
        // noop

    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#setContentLength(int)
     */
    @Override
    public void setContentLength(final int len) {
        // noop

    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
     */
    @Override
    public void setCharacterEncoding(final String charset) {
        // noop

    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#setBufferSize(int)
     */
    @Override
    public void setBufferSize(final int size) {
        // noop

    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#resetBuffer()
     */
    @Override
    public void resetBuffer() {
        // noop

    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#reset()
     */
    @Override
    public void reset() {
        // noop

    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#isCommitted()
     */
    @Override
    public boolean isCommitted() {
        return false;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#getWriter()
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#getOutputStream()
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#getLocale()
     */
    @Override
    public Locale getLocale() {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#getContentType()
     */
    @Override
    public String getContentType() {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding() {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#getBufferSize()
     */
    @Override
    public int getBufferSize() {
        return 0;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.ServletResponse#flushBuffer()
     */
    @Override
    public void flushBuffer() throws IOException {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    @Override
    public void setStatus(final int sc) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
     */
    @Override
    public void setIntHeader(final String name, final int value) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void setHeader(final String name, final String value) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
     */
    @Override
    public void setDateHeader(final String name, final long date) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
     */
    @Override
    public void sendRedirect(final String location) throws IOException {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
     */
    @Override
    public void sendError(final int sc, final String msg) throws IOException {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    @Override
    public void sendError(final int sc) throws IOException {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#getStatus()
     */
    @Override
    public int getStatus() {
        return 0;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#getHeaders(java.lang.String)
     */
    @Override
    public Collection<String> getHeaders(final String name) {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#getHeaderNames()
     */
    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(final String name) {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
     */
    @Override
    public String encodeURL(final String url) {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
     */
    @Override
    public String encodeRedirectURL(final String url) {
        return null;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
     */
    @Override
    public boolean containsHeader(final String name) {
        return false;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
     */
    @Override
    public void addIntHeader(final String name, final int value) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void addHeader(final String name, final String value) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
     */
    @Override
    public void addDateHeader(final String name, final long date) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
     */
    @Override
    public void addCookie(final Cookie cookie) {
        // noop
    }
}
