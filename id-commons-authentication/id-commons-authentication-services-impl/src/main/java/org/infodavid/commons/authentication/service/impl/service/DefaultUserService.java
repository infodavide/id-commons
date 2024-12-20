package org.infodavid.commons.authentication.service.impl.service;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.authentication.service.UserService;
import org.infodavid.commons.impl.service.AbstractEntityService;
import org.infodavid.commons.impl.service.Constants;
import org.infodavid.commons.impl.service.TransactionUtils;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.util.collection.CollectionUtils;
import org.infodavid.commons.util.jackson.JsonUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import groovy.text.SimpleTemplateEngine;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultUserService.<br>
 */
/* If necessary, declare the bean in the Spring configuration. */
@Transactional(readOnly = true)
@Slf4j
public class DefaultUserService extends AbstractEntityService<Long, User> implements UserService, InitializingBean {

    /** The Constant SUPPORTED_ROLES. */
    protected static final String[] SUPPORTED_ROLES;

    static {
        SUPPORTED_ROLES = new String[] {
                org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE,
                org.infodavid.commons.model.Constants.ANONYMOUS_ROLE,
                org.infodavid.commons.model.Constants.USER_ROLE
        };

        Arrays.sort(SUPPORTED_ROLES);
    }

    /** The authentication service. */
    private final AuthenticationService authenticationService;

    /** The authorization service. */
    private final AuthorizationService authorizationService;

    /** The data access object. */
    private final UserDao dao;

    /** The initialized. */
    private boolean initialized;

    /** The template engine. */
    private final SimpleTemplateEngine templateEngine;

    /**
     * Instantiates a new user service.
     * @param logger                the logger
     * @param applicationContext    the application context
     * @param dao                   the data access object
     * @param authenticationService the authentication service
     * @param authorizationService  the authorization service
     */
    public DefaultUserService(final Logger logger, final ApplicationContext applicationContext, final UserDao dao, final AuthenticationService authenticationService, final AuthorizationService authorizationService) {
        super(logger, applicationContext, Long.class, User.class);
        this.dao = dao;
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        templateEngine = new SimpleTemplateEngine();
        templateEngine.setEscapeBackslash(true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public synchronized void afterPropertiesSet() throws ServiceException, SQLException {
        // Caution: @Transactionnal on afterPropertiesSet and PostConstruct method is not evaluated
        if (initialized) {
            return; // NOSONAR Already initialized
        }

        getLogger().debug("Initializing...");
        initialized = true;
        TransactionUtils.doInTransaction("Checking users", LOGGER, getApplicationContext(), () -> {
            Optional<User> optional = dao.findByName(org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATOR);

            if (!optional.isPresent()) {
                final User user = new User();
                user.setDeletable(false);
                user.setName(org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATOR);
                user.setDisplayName("Administrator");
                user.setEmail("support@infodavid.org");
                user.setPassword(DigestUtils.md5Hex(org.infodavid.commons.authentication.model.Constants.DEFAULT_PASSWORD));
                user.setRoles(CollectionUtils.of(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
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
                user.setRoles(CollectionUtils.of(org.infodavid.commons.model.Constants.ANONYMOUS_ROLE));
                dao.insert(user);
            }

            return null;
        });

        getLogger().debug("Initialized");
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#export(java.io.OutputStream)
     */
    @Override
    public void export(final OutputStream out) throws ServiceException { // NOSONAR No complexity
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
                writer.write(StringUtils.join(user.getRoles(), ','));
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
                    writer.write(JsonUtils.toJson(user.getProperties()));
                }

                writer.write(Constants.EOL);
            }

            writer.flush();
        } catch (final Exception e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#findByEmail(java.lang.String)
     */
    @Override
    public Optional<User> findByEmail(final String value) throws ServiceException {
        if (value == null) {
            throw new IllegalArgumentException(org.infodavid.commons.authentication.service.impl.Constants.ARGUMENT_EMAIL_IS_NULL_OR_EMPTY);
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
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#findByName(java.lang.String)
     */
    @Override
    public Optional<User> findByName(final String value) throws ServiceException {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
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
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#findByRole(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<User> findByRole(final String role, final Pageable pageable) throws ServiceException {
        if (StringUtils.isEmpty(role)) {
            throw new IllegalArgumentException(org.infodavid.commons.authentication.service.impl.Constants.ARGUMENT_ROLE_IS_NULL_OR_EMPTY);
        }

        try {
            return filter(dao.findByRole(role, pageable));
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#findByStatus(boolean)
     */
    @Override
    public List<User> findByStatus(final boolean connected) throws ServiceException {
        if (authenticationService == null) {
            throw new IllegalAccessError("Cannot search users by status due to unsupported authentication service");
        }

        final Collection<Principal> authenticated = authenticationService.getAuthenticated();
        final List<User> results = new ArrayList<>();

        if (connected) {
            // reload users from database to get up to date data
            try {
                for (final Principal user : authenticated) {
                    final Optional<User> optional = dao.findByName(user.getName());

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
            filter(dao.findAll(Pageable.unpaged())).stream().filter(u -> !authenticated.contains(u)).forEach(results::add);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        return results;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#findByUniqueConstraints(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected Optional<User> findByUniqueConstraints(final User value) throws ServiceException {
        return findByName(value.getName());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#findReferences(org.springframework.data.domain.Pageable)
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
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#getDataAccessObject()
     */
    @Override
    protected UserDao getDataAccessObject() {
        return dao;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#getSupportedRoles()
     */
    @Override
    public String[] getSupportedRoles() {
        return SUPPORTED_ROLES;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#isConnected(java.lang.String)
     */
    @Override
    public boolean isConnected(final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

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
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#preDelete(java.io.Serializable)
     */
    @Override
    protected void preDelete(final Long id) throws IllegalAccessException, ServiceException {
        if (id == null) {
            return;
        }

        authorizationService.assertDeleteAuthorization(authorizationService.getPrincipal(), getEntityClass(), id);
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
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#preDelete(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected void preDelete(final User entity) throws IllegalAccessException, ServiceException {
        super.preDelete(entity);

        if (entity == null) {
            return;
        }

        if (authenticationService != null) {
            authenticationService.invalidate(entity, Collections.emptyMap());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#preInsert(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected User preInsert(final User value) throws IllegalAccessException, ServiceException {
        if (value.getId() != null && authorizationService.getPrincipal().getName().equals(value.getName())) {
            return value;
        }

        return super.preInsert(value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#preUpdate(java.util.Optional, org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected User preUpdate(final Optional<User> existing, final User value) throws IllegalAccessException, ServiceException {
        if (existing.isPresent()) {
            if (!Objects.equals(existing.get().getRoles(), value.getRoles())) {
                authorizationService.assertRole(authorizationService.getPrincipal(), org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
            }

            // user can write its data
            if (authorizationService.getPrincipal().getName().equals(existing.get().getName())) {
                return value;
            }
        }

        return super.preUpdate(existing, value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#update(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    public void update(final User value) throws ServiceException, IllegalAccessException {
        if (value == null) {
            throw new IllegalArgumentException(Constants.ARGUMENT_IS_NULL_OR_EMPTY);
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
                return;
            }

            existing = optional.get();
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        // User can change its email, displayName and password
        if (authorizationService.getPrincipal().getName().equals(valueToUpdate.getName())) {
            // only some data are allowed for update
            existing.setDisplayName(valueToUpdate.getDisplayName());
            existing.setEmail(valueToUpdate.getEmail());
            existing.setPassword(valueToUpdate.getPassword());
            valueToUpdate.getProperties().forEach(p -> existing.getProperties().add(p));
            super.update(Optional.of(existing), existing);

            return;
        }

        valueToUpdate.setDeletable(false);

        if (!existing.isDeletable()) {
            valueToUpdate.setName(existing.getName());
            valueToUpdate.setRoles(existing.getRoles());
            valueToUpdate.setLocked(existing.isLocked());
            valueToUpdate.setExpirationDate(existing.getExpirationDate());
        }

        super.update(Optional.of(existing), valueToUpdate);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#validate(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    public void validate(final User value) throws ServiceException {
        if (value == null) {
            return;
        }

        if (value.getRoles() == null) {
            value.setRoles(CollectionUtils.of(org.infodavid.commons.model.Constants.ANONYMOUS_ROLE));
        } else {
            for (final String role : value.getRoles()) {
                if (Arrays.binarySearch(getSupportedRoles(), role) < 0) {
                    throw new ValidationException("Role is not supported: " + role + " (" + Arrays.toString(getSupportedRoles()) + ')');
                }
            }
        }

        StringUtils.trim(value.getDisplayName());
        StringUtils.trim(value.getName());
        StringUtils.trim(value.getEmail());

        super.validate(value);
    }
}
