package org.infodavid.commons.security;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.infodavid.commons.model.User;
import org.infodavid.commons.service.Constants;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * The Class AnonymousAuthenticationImpl.
 */
public class AnonymousAuthenticationImpl extends AnonymousAuthenticationToken { // NOSONAR Singleton

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -175776633936615595L;

    /** The Constant INSTANCE. */
    public static final AnonymousAuthenticationImpl INSTANCE = new AnonymousAuthenticationImpl();

    /**
     * Instantiates a new authentication token.
     * @param authorities the authorities
     * @param token       the token
     */
    private AnonymousAuthenticationImpl() {
        super(org.infodavid.commons.model.Constants.ANONYMOUS, Constants.ANONYMOUS, AuthorityUtils.createAuthorityList(Constants.ANONYMOUS.getRole()));
        setDetails(Constants.ANONYMOUS);
    }

    /*
     * (non-javadoc)
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

        if (!(obj instanceof final AnonymousAuthenticationImpl other)) {
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
     * (non-javadoc)
     * @see org.springframework.security.authentication.AbstractAuthenticationToken#getName()
     */
    @Override
    public String getName() {
        return getUser() == null ? "" : getUser().getName();
    }

    /*
     * (non-javadoc)
     * @see org.springframework.security.core.Authentication#getPrincipal()
     */
    @Override
    public Object getPrincipal() {
        return getUser();
    }

    /**
     * Gets the user.
     * @return the user
     */
    public User getUser() {
        if (getDetails() instanceof final User user) {
            return user;
        }

        return null;
    }

    /*
     * (non-javadoc)
     * @see org.springframework.security.authentication.AbstractAuthenticationToken#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getCredentials()).toHashCode();
    }
}
