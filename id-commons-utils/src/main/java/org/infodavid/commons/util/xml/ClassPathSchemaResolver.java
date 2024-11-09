package org.infodavid.commons.util.xml;

import java.util.Map;

import org.infodavid.commons.util.collection.NullSafeConcurrentHashMap;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class ClassPathSchemaResolver.
 */
@Slf4j
public class ClassPathSchemaResolver implements LSResourceResolver {

    /** The cache. */
    private static final Map<String, LSInput> CACHE = new NullSafeConcurrentHashMap<>();

    /** The locations. */
    private final String[] locations;

    /**
     * Instantiates a new resolver.
     */
    public ClassPathSchemaResolver() {
        locations = null;
    }

    /**
     * Instantiates a new resolver.
     * @param locations the locations
     */
    public ClassPathSchemaResolver(final String... locations) {
        this.locations = locations;
    }

    /*
     * (non-Javadoc)
     * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
        LOGGER.debug("Resolving schema: {} (publicId: {})", systemId, publicId);
        final String key = publicId + '|' + systemId;

        return CACHE.computeIfAbsent(key, k -> new LSResourceInput(locations, publicId, systemId));
    }
}
