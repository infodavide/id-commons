package org.infodavid.commons.authentication.rest.security;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.infodavid.commons.service.security.AuthenticationBuilder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AuthenticationToken.
 */
public class AuthenticationJwtToken extends AbstractAuthenticationToken {

    /**
     * The Class Builder.
     */
    @Slf4j
    public static class AuthenticationBuilderImpl implements AuthenticationBuilder {

        /** The secret. */
        private final String secret;

        /**
         * Instantiates a new builder.
         * @param secret the secret
         */
        public AuthenticationBuilderImpl(final String secret) {
            this.secret = Encoders.BASE64.encode(secret.getBytes());
        }

        @Override
        public AuthenticationJwtToken build(final Principal principal, final Collection<GrantedAuthority> authorities, final Date expirationDate) {
            if (principal == null) {
                return null;
            }

            final JwtBuilder builder = Jwts.builder().subject(String.valueOf(principal.getName())).issuedAt(new Date());

            if (expirationDate != null) {
                builder.expiration(expirationDate);
            }

            return new AuthenticationJwtToken(principal, authorities, builder.signWith(getSigningKey()).compact());
        }

        /**
         * Gets the signing key.
         * @return the signing key
         */
        private SecretKey getSigningKey() {
            final byte[] keyBytes = Decoders.BASE64.decode(secret);

            return Keys.hmacShaKeyFor(keyBytes);
        }

        /*
         * (non-Javadoc)
         * @see org.infodavid.commons.service.security.AuthenticationBuilder#isExpired(org.springframework.security.core.Authentication)
         */
        @Override
        public boolean isExpired(final Authentication authentication) {
            if (authentication instanceof final AuthenticationJwtToken a) {
                try {
                    Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(a.token).getPayload().getSubject();

                    return false;
                } catch (final SignatureException e) {
                    LOGGER.error("Invalid JWT signature: {}", e.getMessage());
                } catch (final MalformedJwtException e) {
                    LOGGER.error("Invalid JWT token: {}", e.getMessage());
                } catch (final ExpiredJwtException e) {
                    LOGGER.error("JWT token is expired: {}", e.getMessage());
                } catch (final UnsupportedJwtException e) {
                    LOGGER.error("JWT token is unsupported: {}", e.getMessage());
                } catch (final IllegalArgumentException e) {
                    LOGGER.error("JWT claims string is empty: {}", e.getMessage());
                }
            }

            return true;
        }

        /**
         * Parses the user id.
         * @param token the token
         * @return the long
         */
        public Long parseUserId(final String token) {
            try {
                final String value = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();

                if (StringUtils.isNumeric(value)) {
                    return Long.valueOf(value);
                }
            } catch (final SignatureException e) {
                LOGGER.error("Invalid JWT signature: {}", e.getMessage());
            } catch (final MalformedJwtException e) {
                LOGGER.error("Invalid JWT token: {}", e.getMessage());
            } catch (final ExpiredJwtException e) {
                LOGGER.error("JWT token is expired: {}", e.getMessage());
            } catch (final UnsupportedJwtException e) {
                LOGGER.error("JWT token is unsupported: {}", e.getMessage());
            } catch (final IllegalArgumentException e) {
                LOGGER.error("JWT claims string is empty: {}", e.getMessage());
            }

            return null;
        }

    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -175776633936615595L;

    /** The token. */
    @Getter
    private final String token;

    /**
     * Instantiates a new authentication token.
     * @param authorities the authorities
     * @param token       the token
     */
    protected AuthenticationJwtToken(final Collection<GrantedAuthority> authorities, final String token) {
        super(authorities);
        this.token = token;
        setAuthenticated(StringUtils.isNotEmpty(token));
    }

    /**
     * Instantiates a new authentication token.
     * @param principal   the principal
     * @param authorities the authorities
     * @param token       the token
     */
    protected AuthenticationJwtToken(final Principal principal, final Collection<GrantedAuthority> authorities, final String token) {
        this(authorities, token);
        setDetails(principal);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.authentication.AbstractAuthenticationToken#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (null == obj) {
            return false;
        }

        if (!(obj instanceof final AuthenticationJwtToken other)) {
            return false;
        }

        if (getCredentials() == null) { // NOSONAR
            if (other.getCredentials() != null) {
                return false;
            }
        } else if (!getCredentials().equals(other.getCredentials())) {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getCredentials()
     */
    @Override
    public Object getCredentials() {
        return token;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.authentication.AbstractAuthenticationToken#getName()
     */
    @Override
    public String getName() {
        if (getDetails() instanceof final Principal principal) {
            return principal.getName();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getPrincipal()
     */
    @Override
    public Object getPrincipal() {
        if (getDetails() instanceof final Principal principal) {
            return principal;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.authentication.AbstractAuthenticationToken#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getCredentials()).toHashCode();
    }
}
