package org.infodavid.commons.authentication.service.impl;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.authentication.service.GroupService;
import org.infodavid.commons.authentication.service.UserService;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.impl.AbstractEntityService;
import org.infodavid.commons.service.impl.Constants;
import org.infodavid.commons.service.impl.TransactionUtils;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.util.jackson.JsonUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import groovy.text.SimpleTemplateEngine;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultUserService.<br>
 */
/* If necessary, declare the bean in the Spring configuration. */
@Transactional(readOnly = true)
@Slf4j
public class DefaultUserService extends AbstractEntityService<Long, User> implements UserService, InitializingBean {

    /** The authentication service. */
    private final AuthenticationService authenticationService;

    /** The data access object. */
    private final UserDao dao;

    /** The group service. */
    private final GroupService groupService;

    /** The initialized. */
    private boolean initialized;

    /** The template engine. */
    private final SimpleTemplateEngine templateEngine;

    /**
     * Instantiates a new user service.
     * @param logger                the logger
     * @param applicationContext    the application context
     * @param authorizationService  the authorization service
     * @param dao                   the data access object
     * @param authenticationService the authentication service
     * @param groupService          the group service
     */
    public DefaultUserService(final Logger logger, final ApplicationContext applicationContext, final AuthorizationService authorizationService, final UserDao dao, final AuthenticationService authenticationService, final GroupService groupService) {
        super(logger, applicationContext, authorizationService, Long.class, User.class);
        this.dao = dao;
        this.authenticationService = authenticationService;
        this.groupService = groupService;
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
            DefaultGroupService.initialize(groupService);
            Optional<User> optional = dao.findByName(org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATOR);

            if (!optional.isPresent()) {
                final User user = new User();
                user.setName(org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATOR);
                user.setDisplayName("Administrator");
                user.setEmail("support@infodavid.org");
                user.setPassword(DigestUtils.md5Hex(org.infodavid.commons.authentication.model.Constants.DEFAULT_PASSWORD));
                user.getGroups().add(groupService.findByName(org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATORS).get());
                dao.insert(user);
            }

            optional = dao.findByName(org.infodavid.commons.model.Constants.ANONYMOUS);

            if (!optional.isPresent()) {
                final User user = new User();
                user.setName(org.infodavid.commons.model.Constants.ANONYMOUS);
                user.setDisplayName("Anonymous");
                user.setEmail("support@infodavid.org");
                user.setPassword(org.infodavid.commons.model.Constants.ANONYMOUS);
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
            final Page<User> page = getDataAccessObject().findAll(Pageable.unpaged());
            writer.write("Name");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Creation cate");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Modification date");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Display name");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Email");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Groups");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Expiration date");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Last connection date");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Lasp IP");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Locked");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Password");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Properties");
            writer.write(Constants.EOL);

            for (final User entity : page) {
                writer.write(entity.getName());
                writer.write(Constants.CSV_SEPARATOR);

                if (entity.getCreationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(entity.getCreationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (entity.getModificationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(entity.getModificationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);
                writer.write(entity.getDisplayName());
                writer.write(Constants.CSV_SEPARATOR);

                if (entity.getEmail() != null) {
                    writer.write(entity.getEmail());
                }

                writer.write(Constants.CSV_SEPARATOR);
                writer.write(StringUtils.join(entity.getGroups().stream().map(Group::getName).toArray(), ','));
                writer.write(Constants.CSV_SEPARATOR);

                if (entity.getExpirationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(entity.getExpirationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (entity.getLastConnectionDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(entity.getLastConnectionDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (entity.getLastIp() != null) {
                    writer.write(entity.getLastIp());
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (entity.isLocked()) {
                    writer.write("yes");
                } else {
                    writer.write("no");
                }

                writer.write(Constants.CSV_SEPARATOR);
                writer.write(entity.getPassword());
                writer.write(Constants.CSV_SEPARATOR);

                if (entity.getProperties() != null) {
                    writer.write(JsonUtils.toJson(entity.getProperties()));
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
     * @see org.infodavid.commons.service.impl.AbstractEntityService#findByUniqueConstraints(org.infodavid.commons.model.PersistentEntity)
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
     * @see org.infodavid.commons.service.impl.AbstractEntityService#getDataAccessObject()
     */
    @Override
    protected UserDao getDataAccessObject() {
        return dao;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#hasRole(org.infodavid.commons.authentication.model.User, java.lang.String)
     */
    @Override
    public boolean hasRole(final User user, final String value) throws ServiceException {
        if (user == null || user.getGroups() == null) {
            return false;
        }

        for (final Group group : user.getGroups()) {
            if (group.getRoles() != null && group.getRoles().contains(value)) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#preDelete(java.io.Serializable)
     */
    @Override
    protected void preDelete(final Long id) throws IllegalAccessException, ServiceException {
        if (id == null) {
            return;
        }

        getAuthorizationService().assertDeleteAuthorization(getAuthorizationService().getPrincipal(), getEntityClass(), id);
        validationHelper.validateId(id);
        User entity = null;

        try {
            final Optional<User> optional = getDataAccessObject().findById(id);

            if (optional.isPresent()) {
                entity = optional.get();
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (entity != null && org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATOR.equals(entity.getName())) {
            throw new IllegalAccessException("User '" + entity.getDisplayName() + "' is protected, deletion is not allowed");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#preDelete(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    protected void preDelete(final User value) throws IllegalAccessException, ServiceException {
        if (value != null && org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATOR.equals(value.getName())) {
            throw new IllegalAccessException("User '" + value.getDisplayName() + "' is protected, deletion is not allowed");
        }

        super.preDelete(value);

        if (value == null) {
            return;
        }

        if (authenticationService != null) {
            authenticationService.invalidate(value, Collections.emptyMap());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#preInsert(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    protected User preInsert(final User value) throws IllegalAccessException, ServiceException {
        if (value.getId() != null && getAuthorizationService().getPrincipal().getName().equals(value.getName())) {
            return value;
        }

        return super.preInsert(value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#preUpdate(java.util.Optional, org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    protected User preUpdate(final Optional<User> existing, final User value) throws IllegalAccessException, ServiceException {
        if (existing.isPresent()) {
            if (!Objects.equals(existing.get().getGroups(), value.getGroups())) {
                getAuthorizationService().assertRole(getAuthorizationService().getPrincipal(), org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
            }

            // user can write its data
            if (getAuthorizationService().getPrincipal().getName().equals(existing.get().getName())) {
                return value;
            }
        }

        return super.preUpdate(existing, value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#update(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    public void update(final User value) throws ServiceException, IllegalAccessException {
        if (value == null) {
            throw new IllegalArgumentException(Constants.ARGUMENT_IS_NULL_OR_EMPTY);
        }

        final User entityToUpdate = new User(value);

        // if password value is empty (not provided by presentation layer), existing value is set
        if (StringUtils.isEmpty(entityToUpdate.getPassword()) && entityToUpdate.getId() != null && entityToUpdate.getId().longValue() > 0) {
            try {
                final Optional<User> optional = getDataAccessObject().findById(entityToUpdate.getId());

                if (optional.isPresent()) {
                    // back to existing value
                    entityToUpdate.setPassword(optional.get().getPassword());
                }
            } catch (final PersistenceException e) {
                throw new ServiceException(ExceptionUtils.getRootCause(e));
            }
        }

        final User existingEntity;

        try {
            final Optional<User> optional = dao.findById(entityToUpdate.getId());

            if (!optional.isPresent()) {
                return;
            }

            existingEntity = optional.get();
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        // User can change its email, displayName and password
        if (getAuthorizationService().getPrincipal().getName().equals(entityToUpdate.getName())) {
            // only some data are allowed for update
            existingEntity.setDisplayName(entityToUpdate.getDisplayName());
            existingEntity.setEmail(entityToUpdate.getEmail());
            existingEntity.setPassword(entityToUpdate.getPassword());
            entityToUpdate.getProperties().forEach(p -> existingEntity.getProperties().add(p));
            super.update(Optional.of(existingEntity), existingEntity);

            return;
        }

        if (org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATOR.equals(existingEntity.getName())) {
            entityToUpdate.setName(existingEntity.getName());
            entityToUpdate.setGroups(existingEntity.getGroups());
            entityToUpdate.setLocked(existingEntity.isLocked());
            entityToUpdate.setExpirationDate(existingEntity.getExpirationDate());
        }

        super.update(Optional.of(existingEntity), entityToUpdate);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#validate(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    public void validate(final User value) throws ServiceException {
        if (value == null) {
            return;
        }

        StringUtils.trim(value.getDisplayName());
        StringUtils.trim(value.getName());
        StringUtils.trim(value.getEmail());

        super.validate(value);
    }
}
