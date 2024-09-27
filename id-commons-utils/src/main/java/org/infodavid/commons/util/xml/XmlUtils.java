package org.infodavid.commons.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * The Class XmlUtils.
 */
@SuppressWarnings("static-method")
@JsonIgnoreType
public final class XmlUtils {

    /** The Constant ERROR_WHILE_PARSING_OBJECT_FROM_XML. */
    private static final String ERROR_WHILE_PARSING_OBJECT_FROM_XML = "Error while parsing object from XML: ";

    /** The mapper. */
    private static final XmlMapper MAPPER;

    static {
        MAPPER = new XmlMapper();
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.setSerializationInclusion(Include.NON_NULL);
        MAPPER.setTimeZone(TimeZone.getDefault());
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // make EOL not associated to system
        final PrettyPrinter printer = MAPPER.getSerializationConfig().getDefaultPrettyPrinter();

        if (printer instanceof DefaultPrettyPrinter prettyPrinter) {
            prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", System.lineSeparator()));
        }
    }

    /** The singleton. */
    private static WeakReference<XmlUtils> instance = null;

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized XmlUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new XmlUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new util.
     */
    private XmlUtils() {
    }

    /**
     * Gets the mapper.
     * @return the mapper
     */
    public XmlMapper getMapper() {
        return MAPPER;
    }

    /**
     * From XML.
     * @param <T>         the generic type
     * @param inputStream the input stream
     * @param cls         the class
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public <T> T fromXml(final InputStream inputStream, final Class<T> cls) throws IOException {
        try {
            return MAPPER.readValue(inputStream, cls);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_XML, e);
        }
    }

    /**
     * To XML.
     * @param object the object
     * @return the XML
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String toXml(final Object object) throws IOException {
        return toXml(object, null);
    }

    /**
     * To XML.
     * @param object the object
     * @param eol    the end of line delimiter
     * @return the XML
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String toXml(final Object object, final String eol) throws IOException {
        try {
            if (StringUtils.isEmpty(eol)) {
                return MAPPER.writeValueAsString(object);
            }

            final PrettyPrinter printer = MAPPER.getSerializationConfig().constructDefaultPrettyPrinter();

            if (printer instanceof DefaultPrettyPrinter prettyPrinter) {
                prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", Objects.toString(eol, "\r\n")));
            }

            return MAPPER.writer(printer).writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new IOException("Error while formating object to XML", e);
        }
    }
}
