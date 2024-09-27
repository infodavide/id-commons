package org.infodavid.commons.service;

import java.util.Date;

import org.infodavid.commons.model.User;

/**
 * The Class Constants.
 */
public final class Constants {

    /** The Constant ALL. */
    public static final String ALL = "*";

    /** The Constant ANONYMOUS. */
    public static final User ANONYMOUS;

    /** The Constant ANTISPAM_ALLOWED_OCCURRENCES_PROPERTY. */
    public static final String ANTISPAM_ALLOWED_OCCURRENCES_PROPERTY = "antispam.allowedOccurences";

    /** The Constant ANTISPAM_EXPIRATION_PROPERTY. */
    public static final String ANTISPAM_EXPIRATION_PROPERTY = "antispam.expiration";

    /** The Constant DEFAULT_ANTISPAM_ALLOWED_OCCURENCES. */
    public static final byte DEFAULT_ANTISPAM_ALLOWED_OCCURENCES = 5;

    /** The Constant DEFAULT_ANTISPAM_EXPIRATION. */
    public static final byte DEFAULT_ANTISPAM_EXPIRATION = 60;

    /** The Constant DEFAULT_HOSTNAME. */
    public static final String DEFAULT_HOSTNAME = "localhost";

    /** The Constant DEFAULT_SESSION_INACTIVITY_TIMEOUT. */
    public static final int DEFAULT_SESSION_INACTIVITY_TIMEOUT = 10;

    /** The Constant DEFAULT_SMTP_FROM. */
    public static final String DEFAULT_SMTP_FROM = "root@localhost";

    /** The Constant IP_ADDRESS_PROPERTY. */
    public static final String IP_ADDRESS_PROPERTY = "ip-address";

    /** The Constant MAIL_PROPERTY_SCOPE. */
    public static final String MAIL_PROPERTY_SCOPE = "mail";

    /** The Constant SCHEDULER_THREADS_PROPERTY. */
    public static final String SCHEDULER_THREADS_PROPERTY = "scheduler.threrads";

    /** The Constant SESSION_INACTIVITY_TIMEOUT_PROPERTY. */
    public static final String SESSION_INACTIVITY_TIMEOUT_PROPERTY = "httpSessionInactivityTimeout";

    /** The Constant SMTP_FROM_PROPERTY. */
    public static final String SMTP_FROM_PROPERTY = "smtp.from";

    /** The Constant SMTP_HOSTNAME_PROPERTY. */
    public static final String SMTP_HOSTNAME_PROPERTY = "smtp.hostname";

    /** The Constant SMTP_PASSWORD_PROPERTY. */
    public static final String SMTP_PASSWORD_PROPERTY = "smtp.password"; // NOSONAR Name of the field

    /** The Constant SMTP_PORT_PROPERTY. */
    public static final String SMTP_PORT_PROPERTY = "smtp.port";

    /** The Constant SMTP_USER_PROPERTY. */
    public static final String SMTP_USER_PROPERTY = "smtp.user";

    static {
        ANONYMOUS = new User();
        ANONYMOUS.setName(org.infodavid.commons.model.Constants.ANONYMOUS);
        ANONYMOUS.setRole(org.infodavid.commons.model.Constants.ANONYMOUS_ROLE);
        ANONYMOUS.setDeletable(false);
        ANONYMOUS.setCreationDate(new Date());
        ANONYMOUS.setModificationDate(ANONYMOUS.getCreationDate());
        ANONYMOUS.setDisplayName(ANONYMOUS.getName());
        ANONYMOUS.setId(Long.valueOf(-1));
    }

    /**
     * Instantiates a new constants.
     */
    private Constants() {
    }
}
