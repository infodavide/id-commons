package org.infodavid.commons.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Interface Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /** The administrator role. */
    public static final String ADMINISTRATOR_ROLE = "ROLE_ADMIN";

    /** The Constant ANONYMOUS. */
    public static final String ANONYMOUS = "anonymous";

    /** The anonymous (unauthenticated) role. */
    public static final String ANONYMOUS_ROLE = "ROLE_ANONYMOUS";

    /** The Constant GUEST. */
    public static final String GUEST = "guest";

    /** The mapper. */
    public static final ObjectMapper MAPPER;

    /** The Constant NAME_MUST_NOT_BE_NULL_OR_EMPTY. */
    public static final String NAME_MUST_NOT_BE_NULL_OR_EMPTY = "Name must not be null or empty";

    /** The Constant PROPERTY_LABEL_MAX_LENGTH. */
    public static final short PROPERTY_LABEL_MAX_LENGTH = 128;

    /** The Constant PROPERTY_NAME_MAX_LENGTH. */
    public static final byte PROPERTY_NAME_MAX_LENGTH = 48;

    /** The Constant PROPERTY_TYPE_MAX_LENGTH. */
    public static final byte PROPERTY_TYPE_MAX_LENGTH = 32;

    /** The constant PROPERTY_VALUE_MAX_LENGTH. */
    public static final short PROPERTY_VALUE_MAX_LENGTH = 1024;

    /** The user role. */
    public static final String USER_ROLE = "ROLE_USER";

    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
        MAPPER = new ObjectMapper();
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
        MAPPER.setConfig(MAPPER.getDeserializationConfig().with(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)); // See Mantis issue: 0001773: Exception - JsonParseException: Unrecognized character escape '2'
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.setSerializationInclusion(Include.NON_NULL);
        MAPPER.setTimeZone(TimeZone.getDefault());
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        MAPPER.addHandler(new DeserializationProblemHandler() {
            @SuppressWarnings("boxing")
            @Override
            public Object handleWeirdStringValue(final DeserializationContext context, final Class<?> targetType, final String value, final String message) throws IOException {
                if (targetType == Boolean.class) {
                    final String lower = StringUtils.lowerCase(value);

                    return lower != null && ("true".equals(lower) ||  "yes".equals(lower) ||  "on".equals(lower) || "1".equals(lower));
                }

                if ("null".equalsIgnoreCase(value)) {
                    return null;
                }

                return super.handleWeirdStringValue(context, targetType, value, message);
            }
        });
        // make EOL not associated to system
        final PrettyPrinter printer = MAPPER.getSerializationConfig().getDefaultPrettyPrinter();

        if (printer instanceof final DefaultPrettyPrinter prettyPrinter) {
            prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", System.lineSeparator()));
        }
    }
}
