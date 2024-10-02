package org.infodavid.commons.impl.service;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.impl.security.DefaultAuthenticationServiceImpl;
import org.infodavid.commons.model.PersistentObject;
import org.infodavid.commons.model.PropertiesContainer;
import org.infodavid.commons.model.Property;
import org.infodavid.commons.model.decorator.PropertiesDecorator;
import org.infodavid.commons.persistence.dao.DefaultDao;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.PersistenceException;

/**
 * The Class AbstractEntityService.
 * @param <K> the key type
 * @param <T> the generic type
 */
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.SUPPORTS)
public abstract class AbstractEntityService<K extends Serializable, T extends PersistentObject<K>> extends AbstractService {

    /** The authentication supported. */
    private boolean authenticationSupported = true;

    /** The entity class. */
    private final Class<T> entityClass;

    /** The identifier class. */
    private final Class<K> identifierClass;

    /** The validation helper. */
    protected final ValidationHelper validationHelper;

    /**
     * Instantiates a new abstract entity service.
     * @param applicationContext the application context
     * @param identifierClass    the identifier class
     * @param entityClass        the entity class
     * @param validationHelper   the validation helper
     */
    protected AbstractEntityService(final ApplicationContext applicationContext, final Class<K> identifierClass, final Class<T> entityClass, final ValidationHelper validationHelper) {
        super(applicationContext);
        this.entityClass = entityClass;
        this.identifierClass = identifierClass;
        this.validationHelper = validationHelper;
    }

    /**
     * Adds the entity.
     * @param value the value
     * @return the t
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public T add(final T value) throws ServiceException, IllegalAccessException {
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
     * Delete by identifier.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    public void deleteById(final K id) throws ServiceException, IllegalAccessException {
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
     * Filter the entities.
     * @param entities the entities
     * @return the post processed entities
     */
    protected List<T> filter(final List<T> entities) {
        if (entities != null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Filtering {} entities", String.valueOf(entities.size()));
            }

            final List<T> result = entities.stream().map(this::filter).collect(Collectors.toList());

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Filtered {} entities", String.valueOf(result.size()));
            }

            return result;
        }

        return entities;
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
        if (entity instanceof PropertiesContainer) {
            final PropertiesDecorator properties = ((PropertiesContainer) entity).getProperties();
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
     * Gets the authentication service or null if not supported.
     * @return the authentication service
     */
    protected final synchronized AuthenticationService getAuthenticationService() {
        if (authenticationSupported) {
            try {
                return getApplicationContext().getBean(AuthenticationService.class);
            } catch (@SuppressWarnings("unused") final NoSuchBeanDefinitionException e) { // NOSONAR Nothing to do if authentication service is not supported
                authenticationSupported = false;
            }
        }

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
    protected Collection<Property> getDefaultProperties() {
        return Collections.emptyList();
    }

    /**
     * Gets the entity class.
     * @return the class
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * Gets the identifier class.
     * @return the class
     */
    public Class<K> getIdentifierType() {
        return identifierClass;
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
        final AuthenticationService authenticationService = getAuthenticationService(); // NOSONAR Use getter to initialize field

        if (authenticationService != null && !authenticationService.hasRole(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE) && id instanceof Number) {
            throw new IllegalAccessException(String.format(DefaultAuthenticationServiceImpl.USER_HAS_NOT_THE_ROLE, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
        }
    }

    /**
     * Pre-delete.
     * @param value the value
     * @throws IllegalAccessException the illegal access exception
     * @throws ServiceException       the service exception
     */
    protected void preDelete(final T value) throws IllegalAccessException, ServiceException {
        final AuthenticationService authenticationService = getAuthenticationService(); // NOSONAR Use getter to initialize field

        if (authenticationService != null && !authenticationService.hasRole(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE) && value.getId() instanceof Number) {
            throw new IllegalAccessException(String.format(DefaultAuthenticationServiceImpl.USER_HAS_NOT_THE_ROLE, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
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

        final AuthenticationService authenticationService = getAuthenticationService(); // NOSONAR Use getter to initialize field

        if (authenticationService != null && !authenticationService.hasRole(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE)) {
            throw new IllegalAccessException(String.format(DefaultAuthenticationServiceImpl.USER_HAS_NOT_THE_ROLE, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
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
        final AuthenticationService authenticationService = getAuthenticationService(); // NOSONAR Use getter to initialize field

        if (existing.isPresent() && value instanceof PropertiesContainer) {
            validateProperties(((PropertiesContainer) existing.get()).getProperties(), ((PropertiesContainer) value).getProperties());
        }

        if (authenticationService != null && !authenticationService.hasRole(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE) && value.getId() instanceof Number) {
            throw new IllegalAccessException(String.format(DefaultAuthenticationServiceImpl.USER_HAS_NOT_THE_ROLE, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
        }

        return value;
    }

    /**
     * Update the entities.
     * @param values the values
     * @return the updated entities
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<T> update(final Collection<T> values) throws ServiceException, IllegalAccessException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Updating {} entities", String.valueOf(values.size()));
        }

        return new ArrayList<>(values);
    }

    /**
     * Update the entity.
     * @param matching the matching
     * @param value    the value
     * @return the updated entity
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected T update(final Optional<T> matching, final T value) throws ServiceException, IllegalAccessException {
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

        return valueToUpdate;
    }

    /**
     * Update the entity.
     * @param value the value
     * @return the updated entity
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public T update(final T value) throws ServiceException, IllegalAccessException {
        // Avoid unique constraint violation by searching for an entity having the same data without considering its identifier
        final Optional<T> matching = findByUniqueConstraints(value);

        return update(matching, value);
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
     */
    protected void validateProperties(final PropertiesDecorator existing, final PropertiesDecorator decorator) {
        if (existing != null) {
            for (final Property existingProperty : existing) {
                if (existingProperty.isReadOnly()) {
                    // Property is read only but can be removed
                    if (decorator.contains(existingProperty.getScope(), existingProperty.getName())) {
                        final Property clonedProperty = new Property(existingProperty);
                        decorator.add(clonedProperty);
                    }
                } else if (!existingProperty.isDeletable()) { // Property is immutable but its value can be modified
                    final Property clonedProperty = new Property(existingProperty);
                    clonedProperty.setValue(decorator.get(existingProperty.getScope(), existingProperty.getName()));
                    decorator.add(clonedProperty);
                }
            }
        }
    }
}
