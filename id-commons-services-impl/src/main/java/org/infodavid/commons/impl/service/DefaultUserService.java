package org.infodavid.commons.impl.service;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.UserDao;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.UserService;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.util.jackson.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import groovy.text.SimpleTemplateEngine;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ValidationException;

/**
 * The Class DefaultUserService.<br>
 */
/* If necessary, declare the bean in the Spring configuration. */
public class DefaultUserService extends AbstractEntityService<Long, User> implements UserService, InitializingBean {

    /** The Constant ANONYMOUS. */
    public static final User ANONYMOUS;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserService.class);

    /** The Constant SUPPORTED_ROLES. */
    protected static final String[] SUPPORTED_ROLES;

    static {
        SUPPORTED_ROLES = new String[] {
                org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE,
                org.infodavid.commons.model.Constants.ANONYMOUS_ROLE,
                org.infodavid.commons.model.Constants.USER_ROLE
        };

        Arrays.sort(SUPPORTED_ROLES);
        ANONYMOUS = new User();
        ANONYMOUS.setName(org.infodavid.commons.model.Constants.ANONYMOUS);
        ANONYMOUS.setRole(org.infodavid.commons.model.Constants.ANONYMOUS_ROLE);
        ANONYMOUS.setDeletable(false);
        ANONYMOUS.setCreationDate(new Date());
        ANONYMOUS.setModificationDate(ANONYMOUS.getCreationDate());
        ANONYMOUS.setDisplayName(ANONYMOUS.getName());
        ANONYMOUS.setId(Long.valueOf(-1));
    }

    /** The application service. */
    @Autowired // NOSONAR Do not inject the service on the constructor
    @Lazy
    private ApplicationService applicationService;

    /** The DAO. */
    private final UserDao dao;

    /** The template engine. */
    private final SimpleTemplateEngine templateEngine;

    /**
     * Instantiates a new user service.
     * @param applicationContextProvider the application context provider
     * @param validationHelper           the validation helper
     * @param dao                        the DAO
     */
    public DefaultUserService(final ApplicationContextProvider applicationContextProvider, final ValidationHelper validationHelper, final UserDao dao) {
        super(applicationContextProvider, Long.class, User.class, validationHelper);
        this.dao = dao;
        templateEngine = new SimpleTemplateEngine();
        templateEngine.setEscapeBackslash(true);
    }

    /*
     * (non-javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws ServiceException {
        getLogger().debug("Initializing...");

        try {
            Optional<User> optional = dao.findByName(org.infodavid.commons.model.Constants.DEFAULT_ADMINISTRATOR);

            if (!optional.isPresent()) {
                final User user = new User();
                user.setDeletable(false);
                user.setName(org.infodavid.commons.model.Constants.DEFAULT_ADMINISTRATOR);
                user.setDisplayName("Administrator");
                user.setEmail("support@infodavid.org");
                user.setPassword(DigestUtils.md5Hex(org.infodavid.commons.model.Constants.DEFAULT_PASSWORD));
                user.setRole(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
                dao.insert(user);
            }

            optional = dao.findByName(org.infodavid.commons.model.Constants.ANONYMOUS);

            if (!optional.isPresent()) {
                final User user = new User();
                user.setDeletable(false);
                user.setName(org.infodavid.commons.model.Constants.ANONYMOUS);
                user.setDisplayName("Anonymous");
                user.setEmail("support@infodavid.org");
                user.setPassword(org.infodavid.commons.model.Constants.ANONYMOUS);
                user.setRole(org.infodavid.commons.model.Constants.ANONYMOUS_ROLE);
                dao.insert(user);
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        getLogger().debug("Initialized");
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.UserService#export(java.io.OutputStream)
     */
    @Override
    public void export(final OutputStream out) throws ServiceException { // NOSONAR No complexity
        final JsonUtils utils = JsonUtils.getInstance();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            final Page<User> users = getDataAccessObject().findAll(Pageable.unpaged());
            writer.write("Name");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Creation cate");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Modification date");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Deletable");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Display name");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Email");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Role");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Expiration date");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Last connection date");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Lasp IP");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Locked");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Connections");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Password");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Properties");
            writer.write(Constants.EOL);

            for (final User user : users) {
                writer.write(user.getName());
                writer.write(Constants.CSV_SEPARATOR);

                if (user.getCreationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(user.getCreationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (user.getModificationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(user.getModificationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (user.isDeletable()) {
                    writer.write("yes");
                } else {
                    writer.write("no");
                }

                writer.write(Constants.CSV_SEPARATOR);
                writer.write(user.getDisplayName());
                writer.write(Constants.CSV_SEPARATOR);

                if (user.getEmail() != null) {
                    writer.write(user.getEmail());
                }

                writer.write(Constants.CSV_SEPARATOR);
                writer.write(user.getRole());
                writer.write(Constants.CSV_SEPARATOR);

                if (user.getExpirationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(user.getExpirationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (user.getLastConnectionDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(user.getLastConnectionDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (user.getLastIp() != null) {
                    writer.write(user.getLastIp());
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (user.isLocked()) {
                    writer.write("yes");
                } else {
                    writer.write("no");
                }

                writer.write(Constants.CSV_SEPARATOR);
                writer.write(String.valueOf(user.getConnectionsCount()));
                writer.write(Constants.CSV_SEPARATOR);
                writer.write(user.getPassword());
                writer.write(Constants.CSV_SEPARATOR);

                if (user.getProperties() != null) {
                    writer.write(utils.toJson(user.getProperties()));
                }

                writer.write(Constants.EOL);
            }

            writer.flush();
        } catch (final Exception e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.UserService#findByEmail(java.lang.String)
     */
    @Override
    public Optional<User> findByEmail(final String value) throws ServiceException {
        if (value == null) {
            throw new IllegalArgumentException(org.infodavid.commons.impl.service.Constants.ARGUMENT_EMAIL_IS_NULL_OR_EMPTY);
        }

        User result = null;

        try {
            final Optional<User> optional = dao.findByEmail(value);

            if (optional.isPresent()) {
                result = filter(optional.get());
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        return Optional.ofNullable(result);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.UserService#findByName(java.lang.String)
     */
    @Override
    public Optional<User> findByName(final String value) throws ServiceException {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(org.infodavid.commons.impl.service.Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        User result = null;

        try {
            final Optional<User> optional = dao.findByName(value);

            if (optional.isPresent()) {
                result = filter(optional.get());
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        return Optional.ofNullable(result);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.UserService#findByRoles(java.util.Collection, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<User> findByRole(final Collection<String> roles, final Pageable pageable) throws ServiceException {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException(org.infodavid.commons.impl.service.Constants.ARGUMENT_ROLE_IS_NULL_OR_EMPTY);
        }

        try {
            return filter(dao.findByRoleIn(roles, pageable));
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.UserService#findByStatus(boolean)
     */
    @Override
    public List<User> findByStatus(final boolean connected) throws ServiceException {
        final AuthenticationService authenticationService = getAuthenticationService();

        if (authenticationService == null) {
            throw new IllegalAccessError("Cannot search users by status due to unsupported authentication service");
        }

        final Collection<User> connectedUsers = authenticationService.getAuthenticatedUsers();
        final List<User> results = new ArrayList<>();

        if (connected) {
            // reload users from database to get up to date data
            try {
                for (final User user : connectedUsers) {
                    final Optional<User> optional = dao.findById(user.getId());

                    if (optional.isPresent()) {
                        results.add(filter(optional.get()));
                    }
                }

                return results;
            } catch (final PersistenceException e) {
                throw new ServiceException(ExceptionUtils.getRootCause(e));
            }
        }

        try {
            filter(dao.findAll(Pageable.unpaged())).stream().filter(u -> !connectedUsers.contains(u)).forEach(results::add);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        return results;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#findByUniqueConstraints(org.infodavid.model.PersistentObject)
     */
    @Override
    protected Optional<User> findByUniqueConstraints(final User value) throws ServiceException {
        return findByName(value.getName());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.UserService#getReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<DefaultEntityReference> findReferences(final Pageable pageable) throws ServiceException {
        try {
            return dao.findReferences(pageable);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#getDataAccessObject()
     */
    @Override
    protected UserDao getDataAccessObject() {
        return dao;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractService#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.UserService#getSupportedRoles()
     */
    @Override
    public String[] getSupportedRoles() {
        return SUPPORTED_ROLES;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.UserService#isConnected(java.lang.String)
     */
    @Override
    public boolean isConnected(final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(org.infodavid.commons.impl.service.Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        final AuthenticationService authenticationService = getAuthenticationService();

        if (authenticationService == null) {
            throw new IllegalAccessError("Cannot check user status due to unsupported authentication service");
        }

        User user = null;

        try {
            final Optional<User> optional = dao.findByName(name);

            if (optional.isPresent()) {
                user = optional.get();
            }
        } catch (final PersistenceException e) {
            getLogger().warn("An error occured while retrieving the user: " + name, e); // NOSONAR Templating not available with Throwable argument

            return false;
        }

        if (user == null) {
            return false;
        }

        return authenticationService.isAuthenticated(user);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#assertDelete(java.lang.Long)
     */
    @Override
    protected void preDelete(final Long id) throws IllegalAccessException, ServiceException {
        if (id == null) {
            return;
        }

        final AuthenticationService authenticationService = getAuthenticationService();

        if (authenticationService != null) {
            authenticationService.checkRole(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
        }

        validationHelper.validateId(id);
        User user = null;

        try {
            final Optional<User> optional = getDataAccessObject().findById(id);

            if (optional.isPresent()) {
                user = optional.get();
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (user != null && !user.isDeletable()) {
            throw new IllegalAccessException("User '" + user.getDisplayName() + "' is protected, deletion is not allowed");
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#preDeletion(java.lang.Long)
     */
    @Override
    protected void preDelete(final User entity) throws IllegalAccessException, ServiceException {
        super.preDelete(entity);

        if (entity == null) {
            return;
        }

        final AuthenticationService authenticationService = getAuthenticationService();

        if (authenticationService != null) {
            authenticationService.invalidate(entity, Collections.emptyMap());
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#preInsert(org.infodavid.model.PersistentObject)
     */
    @Override
    protected User preInsert(final User value) throws IllegalAccessException, ServiceException {
        // user can read its data
        final AuthenticationService authenticationService = getAuthenticationService();

        if (value.getId() != null && authenticationService != null && authenticationService.getUser().getId().equals(value.getId())) {
            return value;
        }

        return super.preInsert(value);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#preUpdate(java.util.Optional, org.infodavid.model.PersistentObject)
     */
    @Override
    protected User preUpdate(final Optional<User> existing, final User value) throws IllegalAccessException, ServiceException {
        final AuthenticationService authenticationService = getAuthenticationService();

        if (existing.isPresent() && authenticationService != null) {
            if (!authenticationService.hasRole(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE) && !existing.get().getRole().equals(value.getRole())) {
                throw new IllegalAccessException("User '" + existing.get().getDisplayName() + "' is protected, role update is not allowed");
            }

            // user can write its data
            if (authenticationService.getUser().getId().equals(existing.get().getId())) {
                return value;
            }
        }

        return super.preUpdate(existing, value);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#update(org.infodavid.model .PersistentObject)
     */
    @Override
    public User update(final User value) throws ServiceException, IllegalAccessException {
        if (value == null) {
            throw new IllegalArgumentException(org.infodavid.commons.impl.service.Constants.ARGUMENT_IS_NULL_OR_EMPTY);
        }

        final User valueToUpdate = new User(value);

        // if password value is empty (not provided by presentation layer), existing value is set
        if (StringUtils.isEmpty(valueToUpdate.getPassword()) && valueToUpdate.getId() != null && valueToUpdate.getId().longValue() > 0) {
            try {
                final Optional<User> optional = getDataAccessObject().findById(valueToUpdate.getId());

                if (optional.isPresent()) {
                    // back to existing value
                    valueToUpdate.setPassword(optional.get().getPassword());
                }
            } catch (final PersistenceException e) {
                throw new ServiceException(ExceptionUtils.getRootCause(e));
            }
        }

        final User existing;

        try {
            final Optional<User> optional = dao.findById(valueToUpdate.getId());

            if (!optional.isPresent()) {
                return null;
            }

            existing = optional.get();
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        final AuthenticationService authenticationService = getAuthenticationService();

        // User can change its email, displayName and password
        if (authenticationService != null && authenticationService.getUser().getId().equals(valueToUpdate.getId())) {
            // only some data are allowed for update
            existing.setDisplayName(valueToUpdate.getDisplayName());
            existing.setEmail(valueToUpdate.getEmail());
            existing.setPassword(valueToUpdate.getPassword());
            valueToUpdate.getProperties().forEach(p -> existing.getProperties().add(p));

            return super.update(Optional.of(existing), existing);
        }

        valueToUpdate.setDeletable(false);

        if (!existing.isDeletable()) {
            valueToUpdate.setName(existing.getName());
            valueToUpdate.setRole(existing.getRole());
            valueToUpdate.setLocked(existing.isLocked());
            valueToUpdate.setExpirationDate(existing.getExpirationDate());
        }

        return super.update(Optional.of(existing), valueToUpdate);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#validate(org.infodavid. model.PersistentObject)
     */
    @Override
    public void validate(final User value) throws ServiceException {
        if (value == null) {
            return;
        }

        if (value.getRole() == null) {
            value.setRole(org.infodavid.commons.model.Constants.ANONYMOUS_ROLE);
        } else if (Arrays.binarySearch(getSupportedRoles(), value.getRole()) < 0) {
            throw new ValidationException("Role is not supported: " + value.getRole() + " (" + Arrays.toString(getSupportedRoles()) + ')');
        }

        StringUtils.trim(value.getDisplayName());
        StringUtils.trim(value.getName());
        StringUtils.trim(value.getEmail());

        super.validate(value);
    }
}
