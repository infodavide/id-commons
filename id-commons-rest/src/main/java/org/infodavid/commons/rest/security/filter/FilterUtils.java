package org.infodavid.commons.rest.security.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class FilterUtils.
 */
@UtilityClass
@Slf4j
public final class FilterUtils {

    /** The Constant RESOURCE_MEDIA_TYPES. */
    private static final Set<MediaType> RESOURCE_MEDIA_TYPES;

    static {
        RESOURCE_MEDIA_TYPES = new HashSet<>();
        RESOURCE_MEDIA_TYPES.add(MediaType.IMAGE_GIF);
        RESOURCE_MEDIA_TYPES.add(MediaType.IMAGE_JPEG);
        RESOURCE_MEDIA_TYPES.add(MediaType.IMAGE_PNG);
    }

    /**
     * Checks if is resource.
     * @param request the request
     * @return true, if is resource
     */
    public boolean isResource(final HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        final String uri = request.getRequestURI().toLowerCase();

        if (uri.endsWith(".css") || uri.endsWith(".js")) {
            return true;
        }

        final String acceptHeader = request.getHeader("accept");

        if (acceptHeader != null) {
            final List<MediaType> mediaTypes = MediaType.parseMediaTypes(acceptHeader);

            for (final MediaType mediaType : mediaTypes) {
                if (RESOURCE_MEDIA_TYPES.contains(mediaType)) {
                    return true;
                }
            }
        }

        return false;
    }
}
