package org.infodavid.commons.authentication.service.impl;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.persistence.dao.GroupDao;
import org.infodavid.commons.authentication.service.GroupService;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.impl.AbstractEntityService;
import org.infodavid.commons.service.impl.Constants;
import org.infodavid.commons.service.impl.TransactionUtils;
import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.util.jackson.JsonUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultGroupService.<br>
 */
/* If necessary, declare the bean in the Spring configuration. */
@Transactional(readOnly = true)
@Slf4j
public class DefaultGroupService extends AbstractEntityService<Long, Group> implements GroupService, InitializingBean {

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

    /**
     * Initialize.
     * @param service the service
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     */
    protected static void initialize(final GroupService service) throws IllegalAccessException, ServiceException {
        final Optional<Group> optional = service.findByName(org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATORS);

        if (!optional.isPresent()) {
            final Group group = new Group();
            group.setName(org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATORS);
            group.getRoles().add(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE);
            service.add(group);
        }
    }

    /** The data access object. */
    private final GroupDao dao;

    /** The initialized. */
    private boolean initialized;

    /**
     * Instantiates a new user service.
     * @param logger               the logger
     * @param applicationContext   the application context
     * @param authorizationService the authorization service
     * @param dao                  the data access object
     */
    public DefaultGroupService(final Logger logger, final ApplicationContext applicationContext, final AuthorizationService authorizationService, final GroupDao dao) {
        super(logger, applicationContext, authorizationService, Long.class, Group.class);
        this.dao = dao;
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
        TransactionUtils.doInTransaction("Checking groups", LOGGER, getApplicationContext(), () -> {
            initialize(this);

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
            final Page<Group> page = getDataAccessObject().findAll(Pageable.unpaged());
            writer.write("Name");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Creation cate");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Modification date");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Roles");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Properties");
            writer.write(Constants.EOL);

            for (final Group group : page) {
                writer.write(group.getName());
                writer.write(Constants.CSV_SEPARATOR);

                if (group.getCreationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(group.getCreationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (group.getModificationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(group.getModificationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);
                writer.write(StringUtils.join(group.getRoles().toArray(), ','));
                writer.write(Constants.CSV_SEPARATOR);

                if (group.getProperties() != null) {
                    writer.write(JsonUtils.toJson(group.getProperties()));
                }

                writer.write(Constants.EOL);
            }

            writer.flush();
        } catch (final Exception e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    @Override
    public Optional<Group> findByName(final String value) throws ServiceException {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        Group result = null;

        try {
            final Optional<Group> optional = dao.findByName(value);

            if (optional.isPresent()) {
                result = filter(optional.get());
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Group> findByRole(final String role, final Pageable pageable) throws ServiceException {
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
    protected Optional<Group> findByUniqueConstraints(final Group value) throws ServiceException {
        return findByName(value.getName());
    }

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
    protected GroupDao getDataAccessObject() {
        return dao;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.GroupService#getSupportedRoles()
     */
    @Override
    public String[] getSupportedRoles() {
        return SUPPORTED_ROLES;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#preDelete(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    protected void preDelete(final Group value) throws IllegalAccessException, ServiceException {
        if (value != null && org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATORS.equals(value.getName())) {
            throw new IllegalAccessException("Group '" + value.getName() + "' is protected, deletion is not allowed");
        }

        super.preDelete(value);
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
        Group entity = null;

        try {
            final Optional<Group> optional = getDataAccessObject().findById(id);

            if (optional.isPresent()) {
                entity = optional.get();
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (entity != null && org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATORS.equals(entity.getName())) {
            throw new IllegalAccessException("Group '" + entity.getName() + "' is protected, deletion is not allowed");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#preUpdate(java.util.Optional, org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    protected Group preUpdate(final Optional<Group> existing, final Group value) throws IllegalAccessException, ServiceException {
        if (existing.isPresent()) {
            if (!Objects.equals(existing.get().getRoles(), value.getRoles())) {
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
    public void update(final Group value) throws ServiceException, IllegalAccessException {
        if (value == null) {
            throw new IllegalArgumentException(Constants.ARGUMENT_IS_NULL_OR_EMPTY);
        }

        final Group entityToUpdate = new Group(value);
        final Group existingEntity;

        try {
            final Optional<Group> optional = dao.findById(entityToUpdate.getId());

            if (!optional.isPresent()) {
                return;
            }

            existingEntity = optional.get();
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATORS.equals(existingEntity.getName())) {
            entityToUpdate.setName(existingEntity.getName());
            entityToUpdate.setRoles(existingEntity.getRoles());
        }

        super.update(Optional.of(existingEntity), entityToUpdate);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#validate(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    public void validate(final Group value) throws ServiceException {
        if (value == null) {
            return;
        }

        StringUtils.trim(value.getName());

        super.validate(value);
    }
}
