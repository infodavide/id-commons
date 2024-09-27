package org.infodavid.commons.util.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.infodavid.commons.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

/**
 * The Class JsonUtils.
 */
@SuppressWarnings("static-method")
@JsonIgnoreType
public final class JsonUtils {

    /** The Constant ERROR_WHILE_PARSING_OBJECT_FROM_JSON. */
    private static final String ERROR_WHILE_PARSING_OBJECT_FROM_JSON = "Error while parsing object from JSON: ";

    /** The singleton. */
    private static WeakReference<JsonUtils> instance = null;

    /** The mapper. */
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
        MAPPER.setConfig(MAPPER.getDeserializationConfig().with(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)); // See Mantis issue: 0001773: Exception - JsonParseException: Unrecognized character escape '2'
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.setSerializationInclusion(Include.NON_NULL);
        MAPPER.setNodeFactory(new CustomNodeFactory());
        MAPPER.setTimeZone(TimeZone.getDefault());
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        MAPPER.addHandler(new DeserializationProblemHandler() {
            @SuppressWarnings("boxing")
            @Override
            public Object handleWeirdStringValue(final DeserializationContext context, final Class<?> targetType, final String value, final String message) throws IOException {
                if ("null".equalsIgnoreCase(value)) {
                    return null;
                }

                if (targetType == Boolean.class) {
                    return ObjectUtils.getInstance().toBoolean(value);
                }

                return super.handleWeirdStringValue(context, targetType, value, message);
            }
        });
        // make EOL not associated to system
        final PrettyPrinter printer = MAPPER.getSerializationConfig().getDefaultPrettyPrinter();

        if (printer instanceof DefaultPrettyPrinter prettyPrinter) {
            prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", System.lineSeparator()));
        }
    }

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized JsonUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new JsonUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new util.
     */
    private JsonUtils() {
    }

    /**
     * From JSON.
     * @param <T>         the generic type
     * @param inputStream the input stream
     * @param cls         the class
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public <T> T fromJson(final InputStream inputStream, final Class<T> cls) throws IOException {
        if (inputStream == null) {
            return null;
        }

        try {
            return MAPPER.readValue(inputStream, cls);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_JSON, e);
        }
    }

    /**
     * From JSON.
     * @param <T>         the generic type
     * @param inputStream the input stream
     * @param value       the value
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public <T> T fromJson(final InputStream inputStream, final T value) throws IOException {
        if (inputStream == null) {
            return null;
        }

        try {
            return MAPPER.readerForUpdating(value).readValue(inputStream);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_JSON, e);
        }
    }

    /**
     * From JSON.
     * @param <T>  the generic type
     * @param json the JSON node
     * @param cls  the class
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public <T> T fromJson(final JsonNode json, final Class<T> cls) throws IOException {
        if (json == null) {
            return null;
        }

        try {
            return MAPPER.convertValue(json, cls);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_JSON + json, e);
        }
    }

    /**
     * From JSON.
     * @param <T>   the generic type
     * @param json  the JSON node
     * @param value the value
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public <T> T fromJson(final JsonNode json, final T value) throws IOException {
        if (json == null) {
            return null;
        }

        try {
            return MAPPER.readerForUpdating(value).readValue(json);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_JSON + json, e);
        }
    }

    /**
     * From JSON.
     * @param <T>    the generic type
     * @param reader the reader
     * @param cls    the class
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public <T> T fromJson(final Reader reader, final Class<T> cls) throws IOException {
        if (reader == null) {
            return null;
        }

        try {
            return MAPPER.readValue(reader, cls);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_JSON, e);
        }
    }

    /**
     * From JSON.
     * @param <T>    the generic type
     * @param reader the reader
     * @param value  the value
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public <T> T fromJson(final Reader reader, final T value) throws IOException {
        if (reader == null) {
            return null;
        }

        try {
            return MAPPER.readerForUpdating(value).readValue(reader);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_JSON, e);
        }
    }

    /**
     * From JSON.
     * @param json the JSON value
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Object fromJson(final String json) throws IOException {
        if (json == null) {
            return null;
        }

        if ("true".equalsIgnoreCase(json)) {
            return Boolean.TRUE;
        }

        if ("false".equalsIgnoreCase(json)) {
            return Boolean.FALSE;
        }

        if ("null".equalsIgnoreCase(json)) {
            return null;
        }

        try {
            return MAPPER.readValue(json, Object.class);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_JSON + json, e);
        }
    }

    /**
     * From JSON.
     * @param <T>  the generic type
     * @param json the JSON value
     * @param cls  the class
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public <T> T fromJson(final String json, final Class<T> cls) throws IOException {
        if (json == null) {
            return null;
        }

        try {
            return MAPPER.readValue(json, cls);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_JSON + json, e);
        }
    }

    /**
     * From json.
     * @param <T>   the generic type
     * @param json  the json
     * @param value the value
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public <T> T fromJson(final String json, final T value) throws IOException {
        if (json == null) {
            return null;
        }

        try {
            return MAPPER.readerForUpdating(value).readValue(json);
        } catch (final Exception e) {
            throw new IOException(ERROR_WHILE_PARSING_OBJECT_FROM_JSON + json, e);
        }
    }

    /**
     * Gets the mapper.
     * @return the mapper
     */
    public ObjectMapper getMapper() {
        return MAPPER;
    }

    /**
     * Node to JSON.
     * @param node the node
     * @return the JSON
     * @throws JsonProcessingException the json processing exception
     */
    public String toJson(final JsonNode node) throws JsonProcessingException {
        return MAPPER.writeValueAsString(node);
    }

    /**
     * To JSON.
     * @param object the object
     * @return the JSON
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String toJson(final Object object) throws IOException {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new IOException("Error while formating object to JSON", e);
        }
    }

    /**
     * To JSON.
     * @param outputStream the output stream
     * @param object       the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void toJson(final OutputStream outputStream, final Object object) throws IOException {
        try {
            MAPPER.writeValue(outputStream, object);
        } catch (final JsonProcessingException e) {
            throw new IOException("Error while formating object to JSON", e);
        }
    }

    /**
     * To model.
     * @param o the object
     * @return the json node
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonNode toJsonNode(final Object o) throws IOException {
        return MAPPER.readTree(MAPPER.writeValueAsString(o));
    }
}
