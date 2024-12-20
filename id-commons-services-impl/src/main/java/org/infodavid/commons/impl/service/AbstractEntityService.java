package org.infodavid.commons.impl.service;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.PersistentObject;
import org.infodavid.commons.model.PropertiesContainer;
import org.infodavid.commons.model.decorator.PropertiesDecorator;
import org.infodavid.commons.persistence.dao.DefaultDao;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.security.AuthorizationService;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.PersistenceException;
import lombok.Getter;

/**
 * The Class AbstractEntityService.
 * @param <K> the key type
 * @param <T> the generic type
 */
@Transactional(readOnly = true)
public abstract class AbstractEntityService<K extends Serializable, T extends PersistentObject<K>> extends AbstractService {

    /** The entity class. */
    @Getter
    private final Class<T> entityClass;

    /** The identifier class. */
    @Getter
    private final Class<K> identifierClass;

    /** The validation helper. */
    protected final ValidationHelper validationHelper;

    /**
     * Instantiates a new abstract entity service.
     * @param logger             the logger
     * @param applicationContext the application context
     * @param identifierClass    the identifier class
     * @param entityClass        the entity class
     */
    protected AbstractEntityService(final Logger logger, final ApplicationContext applicationContext, final Class<K> identifierClass, final Class<T> entityClass) {
        super(logger, applicationContext);
        this.entityClass = entityClass;
        this.identifierClass = identifierClass;
        validationHelper = newValidationHelperInstance();
    }

    /**
     * Adds the entity in transaction.
     * @param value the value
     * @return the t
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public T add(final T value) throws ServiceException, IllegalAccessException {
        return doAdd(value);
    }

    /**
     * Count.
     * @return the long
     * @throws ServiceException the service exception
     */
    public long count() throws ServiceException {
        try {
            return getDataAccessObject().count();
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /**
     * Delete the entities.
     * @param entities the entities
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void delete(final Collection<T> entities) throws ServiceException, IllegalAccessException {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        for (final T entity : entities) {
            validationHelper.validateId(entity.getId());
            preDelete(entity.getId());
            preDelete(entity);
            delete(entity);
        }
    }

    /**
     * Delete the entity.
     * @param entity the entity
     * @throws ServiceException the service exception
     */
    protected void delete(final T entity) throws ServiceException {
        try {
            getDataAccessObject().deleteById(entity.getId());
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        getLogger().info("Entity removed: {}", entity.getId());
    }

    /**
     * Delete by identifier in transaction.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteById(final K id) throws ServiceException, IllegalAccessException {
        doDeleteById(id);
    }

    /**
     * Do add.
     * @param value the value
     * @return the t
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected T doAdd(final T value) throws ServiceException, IllegalAccessException {
        getLogger().debug("Adding entity");

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(Constants.DATA_PATTERN, value);
        }

        validate(value);

        final T valueToAdd = preInsert(value);
        final Optional<T> existing = findByUniqueConstraints(value);

        if (existing.isPresent()) {
            getLogger().warn("Given id: {} matches the entity with a different id: {}", valueToAdd.getId(), existing.get().getId());

            throw new EntityExistsException(String.format(Constants.DATA_ALREADY_EXISTS_PATTERN, existing.get().getId()));
        }

        try {
            getDataAccessObject().insert(valueToAdd);
            value.setId(valueToAdd.getId());
        } catch (final Exception e) { // NOSONAR
            getLogger().warn("Cannot add entity", e);

            if (e.getClass().getSimpleName().startsWith("Duplicate")) {
                throw validationHelper.newConstraintViolationException(getEntityClass(), valueToAdd, Constants.DATA_ALREADY_EXISTS_PATTERN);
            }

            throw new ServiceException(e.getMessage());
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(org.infodavid.commons.impl.service.Constants.ADDED_DATA_PATTERN, valueToAdd);
        }

        getLogger().debug("Identifier of added entity: {}", String.valueOf(valueToAdd.getId())); // NOSONAR Always written

        return value;
    }

    /**
     * Do delete by identifier.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void doDeleteById(final K id) throws ServiceException, IllegalAccessException {
        getLogger().info("Removing entity: {}", String.valueOf(id)); // NOSONAR Always written
        validationHelper.validateId(id);
        preDelete(id);
        Optional<T> optional;

        try {
            optional = getDataAccessObject().findById(id);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (optional.isPresent()) {
            preDelete(optional.get());
            delete(optional.get());
        }
    }

    /**
     * Do update.
     * @param values the values
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void doUpdate(final Collection<T> values) throws ServiceException, IllegalAccessException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Updating {} entities", String.valueOf(values.size()));
        }

        for (final T value : values) {
            // Avoid unique constraint violation by searching for an entity having the same data without considering its identifier
            update(findByUniqueConstraints(value), value);
        }
    }

    /**
     * Do update.
     * @param value the value
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void doUpdate(final T value) throws ServiceException, IllegalAccessException {
        // Avoid unique constraint violation by searching for an entity having the same data without considering its identifier
        update(findByUniqueConstraints(value), value);
    }

    /**
     * Filter the entities.
     * @param entities the entities
     * @return the post processed entities
     */
    protected List<T> filter(final List<T> entities) {
        if (entities != null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Filtering {} entities", String.valueOf(entities.size()));
            }

            final List<T> result = entities.stream().map(this::filter).toList();

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Filtered {} entities", String.valueOf(result.size()));
            }

            return result;
        }

        return entities;
    }

    /**
     * Filter the entity.
     * @param optional the optional
     * @return the post processed optional
     */
    protected Optional<T> filter(final Optional<T> optional) {
        if (optional == null || !optional.isPresent()) { // NOSONAR Null check
            return Optional.empty();
        }

        final T entity = filter(optional.get());

        if (entity == null) {
            return Optional.empty();
        }


        return Optional.of(entity);
    }

    /**
     * Filter the entities.
     * @param page the page
     * @return the post processed entities
     */
    protected Page<T> filter(final Page<T> page) {
        if (page != null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Filtering page: {} (count: {})", page, String.valueOf(page.getNumberOfElements()));
            }

            final Page<T> result = page.map(this::filter);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Filtered page: {} (count: {})", result, String.valueOf(result.getNumberOfElements()));
            }

            return result;
        }

        return page;
    }

    /**
     * Filter, by default return the given entity.
     * @param entity the entity
     * @return the post processed entity
     */
    protected T filter(final T entity) {
        if (entity instanceof final PropertiesContainer container) {
            final PropertiesDecorator properties = container.getProperties();
            getDefaultProperties().stream().filter(p -> !properties.contains(p.getScope(), p.getName())).forEach(properties::add);
        }

        return entity;
    }

    /**
     * Find.<br>
     * @param pageable the page definition
     * @return page the page
     * @throws ServiceException the service exception
     */
    public Page<T> find(final Pageable pageable) throws ServiceException {
        getLogger().debug("Searching all entities using page definition: {}", pageable);

        try {
            final Page<T> result = getDataAccessObject().findAll(pageable);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Result(s) found: {}", String.valueOf(result.getNumberOfElements()));
            }

            return filter(result);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /**
     * Find by identifier.
     * @param id the identifier
     * @return the optional
     * @throws ServiceException the service exception
     */
    @SuppressWarnings("boxing")
    public Optional<T> findById(final K id) throws ServiceException {
        getLogger().debug("Searching using id: {}", id);
        validationHelper.validateId(id);
        T result = null;

        try {
            final Optional<T> optional = getDataAccessObject().findById(id);

            if (optional.isPresent()) {
                result = filter(optional.get());
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Result exists: {}", result != null);
            getLogger().debug(Constants.DATA_PATTERN, result);
        }

        return Optional.ofNullable(result);
    }

    /**
     * Find by unique constraints.
     * @param value the value
     * @return the optional
     * @throws ServiceException the service exception
     */
    protected abstract Optional<T> findByUniqueConstraints(T value) throws ServiceException;

    /**
     * Gets the authorization service or null if not supported.
     * @return the service
     */
    protected AuthorizationService getAuthorizationService() {
        return null;
    }

    /**
     * Gets the data access object.
     * @return the data access object
     */
    protected abstract DefaultDao<K, T> getDataAccessObject();

    /**
     * Gets the default properties.
     * @return the default properties
     */
    protected Collection<EntityProperty> getDefaultProperties() {
        return Collections.emptyList();
    }

    /**
     * New validation helper.
     * @return the validation helper
     */
    protected ValidationHelper newValidationHelperInstance() {
        return new ValidationHelper();
    }

    /**
     * Pre-delete.
     * @param id the identifier
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     */
    protected void preDelete(final K id) throws IllegalAccessException, ServiceException {
        if (id == null) {
            return;
        }

        validationHelper.validateId(id);
        final AuthorizationService authorizationService = getAuthorizationService(); // NOSONAR Use getter to initialize field

        if (authorizationService != null) {
            authorizationService.assertDeleteAuthorization(authorizationService.getPrincipal(), entityClass, id);
        }
    }

    /**
     * Pre-delete.
     * @param value the value
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     */
    protected void preDelete(final T value) throws IllegalAccessException, ServiceException {
        final AuthorizationService authorizationService = getAuthorizationService(); // NOSONAR Use getter to initialize field

        if (authorizationService != null) {
            authorizationService.assertDeleteAuthorization(authorizationService.getPrincipal(), entityClass, value.getId());
        }
    }

    /**
     * Pre-insert.
     * @param value the value
     * @return the value
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     */
    protected T preInsert(final T value) throws IllegalAccessException, ServiceException {
        if (value == null) {
            return value;
        }

        final AuthorizationService authorizationService = getAuthorizationService(); // NOSONAR Use getter to initialize field

        if (authorizationService != null) {
            authorizationService.assertAddAuthorization(authorizationService.getPrincipal(), entityClass, value);
        }

        return value;
    }

    /**
     * Pre-update.
     * @param existing the existing
     * @param value    the value
     * @return the value
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     */
    protected T preUpdate(final Optional<T> existing, final T value) throws IllegalAccessException, ServiceException {
        final AuthorizationService authorizationService = getAuthorizationService(); // NOSONAR Use getter to initialize field

        if (existing.isPresent() && value instanceof final PropertiesContainer container) {
            validateProperties(((PropertiesContainer) existing.get()).getProperties(), container.getProperties());
        }

        if (authorizationService != null) {
            authorizationService.assertUpdateAuthorization(authorizationService.getPrincipal(), entityClass, value.getId());
        }

        return value;
    }

    /**
     * Update the entities in transaction.
     * @param values the values
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void update(final Collection<T> values) throws ServiceException, IllegalAccessException {
        doUpdate(values);
    }

    /**
     * Update the entity.
     * @param matching the matching
     * @param value    the value
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void update(final Optional<T> matching, final T value) throws ServiceException, IllegalAccessException {
        getLogger().debug("Updating entity: {}", value);
        final T valueToUpdate;

        if (matching.isPresent()) {
            if (!matching.get().getId().equals(value.getId())) {
                getLogger().warn("Given id: {} matches the entity with a different id: {}", value.getId(), matching.get().getId());

                throw new EntityExistsException(String.format(Constants.DATA_ALREADY_EXISTS_PATTERN, matching.get().getId()));
            }

            // Entity found is the one passed to the update method
            valueToUpdate = preUpdate(matching, value);
        } else {
            // In case of all the fields have not been set, we use the identifier to search the existing one, stored into the database
            getLogger().debug("No matching entity for: {}", value);

            try {
                valueToUpdate = preUpdate(getDataAccessObject().findById(value.getId()), value);
            } catch (final PersistenceException e) {
                throw new ServiceException(ExceptionUtils.getRootCause(e));
            }
        }

        validate(valueToUpdate);

        try {
            getDataAccessObject().update(valueToUpdate);
        } catch (final Exception e) { // NOSONAR
            getLogger().warn("Cannot update entity", e);

            if (e instanceof SQLIntegrityConstraintViolationException) { // NOSONAR SQLIntegrityConstraintViolationException is detected as not reacheable
                throw validationHelper.newConstraintViolationException(getEntityClass(), valueToUpdate, e.getMessage());
            }

            throw new ServiceException(e.getMessage());
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(Constants.DATA_PATTERN, valueToUpdate);
            getLogger().debug("Entity updated: {}", valueToUpdate.getId());
        }
    }

    /**
     * Update the entity in transaction.
     * @param value the value
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void update(final T value) throws ServiceException, IllegalAccessException {
        doUpdate(value);
    }

    /**
     * Validate.
     * @param values the values
     * @throws ServiceException the service exception
     */
    public void validate(final Collection<T> values) throws ServiceException {
        if (values == null || values.isEmpty()) {
            return;
        }

        for (final T value : values) {
            validate(value);
        }
    }

    /**
     * Validate.
     * @param value the value
     * @throws ServiceException the service exception
     */
    public void validate(final T value) throws ServiceException {
        if (value == null) {
            return;
        }

        getLogger().debug("Validating entity having identifier: {}", value.getId());
        validationHelper.validate(getEntityClass(), value);
        getLogger().debug("Entity is valid");
    }

    /**
     * Validate properties.
     * @param existing  the existing properties
     * @param decorator the properties
     * @throws ServiceException the service exception
     */
    protected void validateProperties(final PropertiesDecorator existing, final PropertiesDecorator decorator) throws ServiceException {
        if (existing != null) {
            for (final EntityProperty existingProperty : existing) {
                if (existingProperty.isReadOnly()) {
                    // EntityProperty is read only but can be removed
                    if (decorator.contains(existingProperty.getScope(), existingProperty.getName())) {
                        final EntityProperty clonedProperty = new EntityProperty(existingProperty);
                        decorator.add(clonedProperty);
                    }
                } else if (!existingProperty.isDeletable()) { // EntityProperty is immutable but its value can be modified
                    final EntityProperty clonedProperty = new EntityProperty(existingProperty);
                    clonedProperty.setValue(decorator.get(existingProperty.getScope(), existingProperty.getName()));
                    decorator.add(clonedProperty);
                }
            }
        }
    }
}
