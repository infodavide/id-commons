package org.infodavid.commons.restapi.security.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thetransactioncompany.cors.CORSConfiguration;
import com.thetransactioncompany.cors.CORSConfigurationLoader;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;

/**
 * The Class CrossOriginResourceSharingFilter.
 */
public class CrossOriginResourceSharingFilter extends com.thetransactioncompany.cors.CORSFilter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CrossOriginResourceSharingFilter.class);

    /**
     * Instantiates a new filter.
     */
    public CrossOriginResourceSharingFilter() {
    }

    /**
     * Instantiates a new filter.
     * @param config the config
     */
    public CrossOriginResourceSharingFilter(final CORSConfiguration config) {
        super(config);
    }

    /* (non-Javadoc)
     * @see com.thetransactioncompany.cors.CORSFilter#init(jakarta.servlet.FilterConfig)
     */
    @Override
    public void init(final FilterConfig config) throws ServletException {
        final Properties props = new Properties();
        final Enumeration<String> params = config.getInitParameterNames();

        // default in web.xml
        while (params.hasMoreElements()) {
            final String key = params.nextElement();
            final String value = config.getInitParameter(key);
            props.setProperty(key, value);
        }

        try {
            // can be overridden by system properties
            for (final Entry<Object, Object> item : System.getProperties().entrySet()) {
                final String key = item.getKey().toString();
                String value = null;

                if (item.getValue() != null) {
                    value = item.getValue().toString();
                }

                if (key.toLowerCase().startsWith("cors.")) {
                    props.put(key, value);
                }
            }

            // Try to get the config file from the sys environment
            final String configFile = props.getProperty(CORSConfigurationLoader.CONFIG_FILE_PARAM_NAME);

            if (configFile != null) {
                loadFileIntoProperties(config, props, configFile);
            }

            final StringBuilder buffer = new StringBuilder();
            buffer.append("CORS configuration:");
            props.entrySet().forEach(e -> {
                buffer.append('\n');
                buffer.append(e.getKey());
                buffer.append('=');
                buffer.append(e.getValue());
            });
            LOGGER.debug(buffer.toString()); // NOSONAR
            setConfiguration(new CORSConfiguration(props));
        } catch (final Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Load file into properties.
     * @param config   the configuration
     * @param props    the props
     * @param filename the filename
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void loadFileIntoProperties(final FilterConfig config, final Properties props, final String filename) throws IOException {
        final String correctedFilename = filename.charAt(0) == '/' ? filename : '/' + filename;
        InputStream is = null;

        try {
            is = config.getServletContext().getResourceAsStream(correctedFilename);

            if (is == null) {
                is = getClass().getResourceAsStream(correctedFilename);
            }

            if (is != null) {
                props.load(is);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (final IOException e2) {
                    LOGGER.trace("Close error", e2);
                }
            }
        }
    }
}
