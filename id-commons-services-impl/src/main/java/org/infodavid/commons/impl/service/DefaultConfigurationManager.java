package org.infodavid.commons.impl.service;

import java.util.LinkedHashSet;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.persistence.dao.ConfigurationPropertyDao;
import org.infodavid.commons.service.ConfigurationManager;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.listener.PropertyChangedListener;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.PersistenceException;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * The Class DefaultConfigurationManager.<br>
 * Keep this class abstract to make it optional for the projects using this module.<br>
 * To use this service, the project must extends this class and add the Spring annotation(s).
 */
@Transactional(readOnly = true)
public class DefaultConfigurationManager extends AbstractEntityService<Long, ConfigurationProperty> implements ConfigurationManager {

    /** The Constant PROPERTY_IS_READ_ONLY. */
    private static final String PROPERTY_IS_READ_ONLY = "Property '%s' is read only";

    /** The data access object. */
    private final ConfigurationPropertyDao dao;

    /** The listeners. */
    @Getter(value = AccessLevel.PROTECTED)
    private final LinkedHashSet<PropertyChangedListener> listeners = new LinkedHashSet<>();

    /** The scope. */
    @Getter
    private String scope;

    /**
     * Instantiates a new application service.
     * @param logger             the logger
     * @param applicationContext the application context
     * @param validationHelper   the validation helper
     * @param dao                the data access object
     * @param scope              the scope
     */
    public DefaultConfigurationManager(final Logger logger, final ApplicationContext applicationContext, final ValidationHelper validationHelper, final ConfigurationPropertyDao dao, final String scope) {
        super(logger, applicationContext, Long.class, ConfigurationProperty.class, validationHelper);
        this.dao = dao;
        this.scope = scope;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#addListener(org.infodavid.commons.service.listener.PropertyChangedListener)
     */
    @Override
    public void addListener(final PropertyChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#deleteByName(java.lang.String)
     */
    @Override
    public void deleteByName(final String name) throws ServiceException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        try {
            final Optional<ConfigurationProperty> optional = getDataAccessObject().findByScopeAndName(getScope(), name);

            if (optional.isPresent()) {
                getDataAccessObject().deleteById(optional.get().getId());
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#doAdd(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected ConfigurationProperty doAdd(final ConfigurationProperty value) throws ServiceException, IllegalAccessException {
        if (value != null) {
            value.setScope(scope);
        }

        return super.doAdd(value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#doUpdate(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected void doUpdate(final ConfigurationProperty value) throws ServiceException, IllegalAccessException {
        if (value != null) {
            value.setScope(scope);
        }

        super.doUpdate(value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#filter(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected ConfigurationProperty filter(final ConfigurationProperty value) {
        if (value != null && PropertyType.PASSWORD.equals(value.getType()) && StringUtils.isNotEmpty(value.getValue())) {
            // password is decoded and exposed as plain to allow other services to use the property without knowledge of encoding mechanism
            final ConfigurationProperty clone = new ConfigurationProperty(value);
            clone.setValue(org.infodavid.commons.util.StringUtils.decode(value.getValue()));

            return clone;
        }

        return value;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#find(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<ConfigurationProperty> find(final Pageable pageable) throws ServiceException {
        getLogger().debug("Searching all entities using page definition: {}", pageable);

        try {
            final Page<ConfigurationProperty> result = getDataAccessObject().findByScope(getScope(), pageable);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Result(s) found: {}", String.valueOf(result.getNumberOfElements()));
            }

            return filter(result);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findByName(java.lang.String)
     */
    @Override
    public Optional<ConfigurationProperty> findByName(final String name) throws ServiceException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        try {
            final Optional<ConfigurationProperty> optional = getDataAccessObject().findByScopeAndName(getScope(), name);

            if (optional.isPresent()) {
                return Optional.of(filter(optional.get()));
            }

            return Optional.empty();
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findByScopeAndName(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("hiding")
    @Override
    public Optional<ConfigurationProperty> findByScopeAndName(final String scope, final String name) throws ServiceException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        ConfigurationProperty result = null;

        try {
            final Optional<ConfigurationProperty> optional = dao.findByScopeAndName(scope, name);

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
     * @see org.infodavid.commons.impl.service.AbstractEntityService#findByUniqueConstraints(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected Optional<ConfigurationProperty> findByUniqueConstraints(final ConfigurationProperty value) throws ServiceException {
        if (value == null) {
            return Optional.empty();
        }

        value.setScope(scope);

        return findByScopeAndName(value.getScope(), value.getName());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ApplicationService#findReferences(org.springframework.data.domain.Pageable)
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
     * @see org.infodavid.commons.service.ConfigurationManager#findValueOrDefault(java.lang.String, boolean)
     */
    @Override
    public boolean findValueOrDefault(final String name, final boolean defaultValue) throws ServiceException {
        final Optional<ConfigurationProperty> optional = findByName(name);

        if (optional.isEmpty()) {
            return defaultValue;
        }

        return optional.get().getValueOrDefault(defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findValueOrDefault(java.lang.String, double)
     */
    @Override
    public double findValueOrDefault(final String name, final double defaultValue) throws ServiceException {
        final Optional<ConfigurationProperty> optional = findByName(name);

        if (optional.isEmpty()) {
            return defaultValue;
        }

        return optional.get().getValueOrDefault(defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findValueOrDefault(java.lang.String, int)
     */
    @Override
    public int findValueOrDefault(final String name, final int defaultValue) throws ServiceException {
        final Optional<ConfigurationProperty> optional = findByName(name);

        if (optional.isEmpty()) {
            return defaultValue;
        }

        return optional.get().getValueOrDefault(defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findValueOrDefault(java.lang.String, long)
     */
    @Override
    public long findValueOrDefault(final String name, final long defaultValue) throws ServiceException {
        final Optional<ConfigurationProperty> optional = findByName(name);

        if (optional.isEmpty()) {
            return defaultValue;
        }

        return optional.get().getValueOrDefault(defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findValueOrDefault(java.lang.String, java.lang.Object)
     */
    @Override
    public Object findValueOrDefault(final String name, final Object defaultValue) throws ServiceException {
        final Optional<ConfigurationProperty> optional = findByName(name);

        if (optional.isEmpty()) {
            return defaultValue;
        }

        return optional.get().getValueOrDefault(defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findValueOrDefault(java.lang.String, java.lang.String)
     */
    @Override
    public String findValueOrDefault(final String name, final String defaultValue) throws ServiceException {
        final Optional<ConfigurationProperty> optional = findByName(name);

        if (optional.isEmpty()) {
            return defaultValue;
        }

        return optional.get().getValueOrDefault(defaultValue);
    }

    /**
     * Fire change.
     * @param property the property
     */
    protected void fireChange(final ConfigurationProperty property) {
        if (StringUtils.isEmpty(property.getName())) {
            return;
        }

        getLogger().debug("property '{}' changed to: '{}'", property.getName(), property.getValue());

        for (final PropertyChangedListener listener : listeners) {
            listener.propertyChanged(property);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#getDataAccessObject()
     */
    @Override
    protected ConfigurationPropertyDao getDataAccessObject() {
        return dao;
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

        super.preDelete(id);
        ConfigurationProperty value = null;

        try {
            final Optional<ConfigurationProperty> optional = getDataAccessObject().findById(id);

            if (optional.isPresent()) {
                value = optional.get();
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (value == null) {
            return;
        }

        if (!value.isDeletable()) {
            throw new IllegalAccessException("Setting is protected, deletion is not allowed: " + value);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#preInsert(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected ConfigurationProperty preInsert(final ConfigurationProperty value) throws IllegalAccessException, ServiceException {
        if (value != null && value.isReadOnly()) {
            throw new IllegalAccessException(String.format(PROPERTY_IS_READ_ONLY, value.getName()));
        }

        super.preInsert(value);
        ConfigurationProperty result = value;

        if (value != null && PropertyType.PASSWORD.equals(value.getType()) && StringUtils.isNotEmpty(value.getValue())) {
            // password is encoded in database
            result = new ConfigurationProperty(value);

            result.setValue(org.infodavid.commons.util.StringUtils.encode(value.getValue()));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#preUpdate(java.util.Optional, org.infodavid.commons.model.PersistentObject)
     */
    @Override
    protected ConfigurationProperty preUpdate(final Optional<ConfigurationProperty> existing, final ConfigurationProperty value) throws IllegalAccessException, ServiceException {
        ConfigurationProperty result = value;

        if (existing.isPresent()) {
            if (existing.get().isReadOnly()) {
                throw new IllegalAccessException(String.format(PROPERTY_IS_READ_ONLY, existing.get().getName()));
            }

            if (!existing.get().isDeletable()) {
                result = new ConfigurationProperty(existing.get());
                result.setValue(value.getValue());
            }
        }

        super.preUpdate(existing, result);

        // password value is handled here as it is optional, not required by validation
        if (value != null && PropertyType.PASSWORD.equals(value.getType())) {
            if (value == result) {
                result = new ConfigurationProperty(result);
            }

            if (StringUtils.isEmpty(value.getValue())) {
                // back to existing value
                result.setValue(existing.isPresent() ? existing.get().getValue() : result.getValue());
            }

            // password is encoded in database
            result.setValue(org.infodavid.commons.util.StringUtils.encode(result.getValue()));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#removeListener(org.infodavid.commons.service.listener.PropertyChangedListener)
     */
    @Override
    public void removeListener(final PropertyChangedListener listener) {
        if (listener == null) {
            return;
        }

        getLogger().debug("Unregistering listener: {}", listener);

        listeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#update(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    public void update(final ConfigurationProperty value) throws ServiceException, IllegalAccessException {
        final Optional<ConfigurationProperty> matching = findByUniqueConstraints(value);

        if (matching.isPresent() && matching.get().isReadOnly()) {
            return;
        }

        update(matching, value);
        fireChange(value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractEntityService#validate(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    public void validate(final ConfigurationProperty value) throws ServiceException {
        if (value == null) {
            return;
        }

        StringUtils.trim(value.getName());
        StringUtils.trim(value.getValue());
        super.validate(value);
    }
}
