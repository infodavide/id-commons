package org.infodavid.commons.util.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.ls.LSInput;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class LSResourceInput.
 */
@Slf4j
public class LSResourceInput implements LSInput {

    /** The base URI. */
    private String baseUri = null;

    /** The certified text. */
    @Setter
    private boolean certifiedText = false;

    /** The content. */
    private WeakReference<byte[]> content = null;

    /** The locations. */
    @Getter
    private final String[] locations;

    /** The public identifier. */
    @Getter
    @Setter
    private String publicId;

    /** The system identifier. */
    @Getter
    @Setter
    private String systemId;

    /**
     * Instantiates a new LS input.
     * @param locations the locations
     * @param publicId  the public identifier
     * @param systemId  the system identifier
     */
    public LSResourceInput(final String[] locations, final String publicId, final String systemId) {
        this.locations = locations;
        this.publicId = publicId;
        this.systemId = systemId;
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#getBaseURI()
     */
    @Override
    public String getBaseURI() {
        return baseUri;
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#getByteStream()
     */
    @SuppressWarnings("resource")
    @Override
    public synchronized InputStream getByteStream() {
        byte[] bytes = content == null ? new byte[0] : content.get();

        if (ArrayUtils.isEmpty(bytes)) {
            bytes = new byte[0];

            try (final InputStream is = getInputStream()) {
                if (is == null) {
                    LOGGER.debug("No schema found");
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Bytes : {}", String.valueOf(is.available()));
                    }

                    bytes = IOUtils.toByteArray(BOMInputStream.builder().setInputStream(is).get());
                }
            } catch (final IOException | URISyntaxException e) {
                LOGGER.warn("An error occured while reading schema", e);
            }
        }

        content = new WeakReference<>(bytes);

        return new ByteArrayInputStream(bytes);
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#getCertifiedText()
     */
    @Override
    public boolean getCertifiedText() {
        return certifiedText;
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#getCharacterStream()
     */
    @Override
    public Reader getCharacterStream() {
        final InputStream is = getByteStream();

        return is == null ? null : new InputStreamReader(is, Charset.forName(getEncoding()));
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#getEncoding()
     */
    @Override
    public String getEncoding() {
        return StandardCharsets.UTF_8.name();
    }

    /**
     * Gets the input stream.
     * @return the input stream
     * @throws IOException        Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    public InputStream getInputStream() throws IOException, URISyntaxException {
        if (StringUtils.isEmpty(systemId)) {
            LOGGER.debug("SystemId is null");

            return null;
        }

        final int position = systemId.lastIndexOf('/');
        baseUri = position < 0 ? systemId : systemId.substring(0, position);
        final String path = position < 0 ? systemId : systemId.substring(position + 1);
        LOGGER.debug("Trying to read schema using classpath resource: {}", path);
        InputStream result = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

        if (locations != null) {
            int i = 0;

            while (result == null && i < locations.length) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Trying to read schema using location: {}", locations[i] + '/' + path);
                }

                result = Thread.currentThread().getContextClassLoader().getResourceAsStream(locations[i] + '/' + path);
                i++;
            }
        }

        if (result == null && (StringUtils.startsWith(systemId, "http:") || StringUtils.startsWith(systemId, "https:") || StringUtils.startsWith(systemId, "file:"))) {
            LOGGER.debug("Reading schema {} from URL: {}", path, systemId);

            return new URI(systemId).toURL().openStream();
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#getStringData()
     */
    @SuppressWarnings("resource")
    @Override
    public String getStringData() {
        try {
            final InputStream is = getByteStream();

            return is == null ? null : IOUtils.toString(is, getEncoding());
        } catch (@SuppressWarnings("unused") final IOException e) {
            return null;
        }
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#setBaseURI(java.lang.String)
     */
    @Override
    public void setBaseURI(final String baseUri) {
        this.baseUri = baseUri;
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#setByteStream(java.io.InputStream)
     */
    @Override
    public synchronized void setByteStream(final InputStream byteStream) {
        // not used
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#setCharacterStream(java.io.Reader)
     */
    @Override
    public void setCharacterStream(final Reader characterStream) {
        // not used
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#setEncoding(java.lang.String)
     */
    @Override
    public void setEncoding(final String encoding) {
        // not used
    }

    /*
     * (non-javadoc)
     * @see org.w3c.dom.ls.LSInput#setStringData(java.lang.String)
     */
    @Override
    public void setStringData(final String stringData) {
        // not used
    }
}
