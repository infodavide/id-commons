package org.infodavid.commons.service.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * The Interface AuthenticationBuilder.
 */
public interface AuthenticationBuilder {

    /**
     * Builds the.
     * @param principal      the principal
     * @param authorities    the authorities
     * @param expirationDate the expiration date
     * @return the authentication
     */
    Authentication build(UserPrincipal principal, Collection<GrantedAuthority> authorities, Date expirationDate);

    /**
     * Deserialize.
     * @param data the data
     * @return the authentication
     * @throws IOException Signals that an I/O exception has occurred.
     */
    default Authentication deserialize(final String data) throws IOException {
        if (StringUtils.isEmpty(data)) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(data)))) {
            final Object object = ois.readObject();

            if (object instanceof final Authentication result) {
                return result;
            }
        } catch (final ClassNotFoundException e) {
            throw new IOException(e);
        }

        return null;
    }

    /**
     * Checks if is expired.
     * @param authentication the authentication
     * @return true, if is expired
     */
    boolean isExpired(Authentication authentication);

    /**
     * Serialize.
     * @param authentication the authentication
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    default String serialize(final Authentication authentication) throws IOException {
        if (authentication == null) {
            return null;
        }

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(authentication);
        }

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
