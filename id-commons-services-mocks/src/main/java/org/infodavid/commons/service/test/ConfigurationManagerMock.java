package org.infodavid.commons.service.test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.ConfigurationManager;
import org.infodavid.commons.service.listener.PropertyChangedListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.google.common.base.Objects;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * The Class ConfigurationManagerMock.
 */
public class ConfigurationManagerMock extends AbstractEntityServiceMock<Long, ConfigurationProperty> implements ConfigurationManager {

    /** The Constant SEQUENCE. */
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    /** The listeners. */
    @Getter(value = AccessLevel.PROTECTED)
    private final LinkedHashSet<PropertyChangedListener> listeners = new LinkedHashSet<>();

    /** The scope. */
    @Getter
    private String scope;

    /**
     * Instantiates a new configuration manager mock.
     * @param scope the scope
     */
    public ConfigurationManagerMock(final String scope) {
        super(Long.class, ConfigurationProperty.class);
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
     * @see org.infodavid.commons.service.test.AbstractEntityServiceMock#clear()
     */
    @Override
    public void clear() {
        super.clear();
        SEQUENCE.set(1);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#deleteByName(java.lang.String)
     */
    @Override
    public void deleteByName(final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        final Optional<ConfigurationProperty> optional = findByScopeAndName(getScope(), name);

        if (optional.isPresent()) {
            deleteById(optional.get().getId());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findByName(java.lang.String)
     */
    @Override
    public Optional<ConfigurationProperty> findByName(final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        final Optional<ConfigurationProperty> optional = findByScopeAndName(getScope(), name);

        if (optional.isPresent()) {
            return Optional.of(optional.get());
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findByScopeAndName(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("hiding")
    @Override
    public Optional<ConfigurationProperty> findByScopeAndName(final String scope, final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        for (final ConfigurationProperty entity : find(Pageable.unpaged())) {
            if (Objects.equal(entity.getScope(), scope) && Objects.equal(entity.getName(), name)) {
                return Optional.of(entity);
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.impl.AbstractEntityService#findByUniqueConstraints(org.infodavid.commons.model.PersistentEntity)
     */
    protected Optional<ConfigurationProperty> findByUniqueConstraints(final ConfigurationProperty value) {
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
    public Page<DefaultEntityReference> findReferences(final Pageable pageable) {
        final List<DefaultEntityReference> results = new ArrayList<>();

        for (final ConfigurationProperty entity : find(pageable)) {
            results.add(new DefaultEntityReference(entity.getId(), entity.getName()));
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.ConfigurationManager#findValueOrDefault(java.lang.String, boolean)
     */
    @Override
    public boolean findValueOrDefault(final String name, final boolean defaultValue) {
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
    public double findValueOrDefault(final String name, final double defaultValue) {
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
    public int findValueOrDefault(final String name, final int defaultValue) {
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
    public long findValueOrDefault(final String name, final long defaultValue) {
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
    public Object findValueOrDefault(final String name, final Object defaultValue) {
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
    public String findValueOrDefault(final String name, final String defaultValue) {
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

        for (final PropertyChangedListener listener : listeners) {
            listener.propertyChanged(property);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.test.AbstractEntityServiceMock#nextId()
     */
    @SuppressWarnings("boxing")
    @Override
    protected Long nextId() {
        return SEQUENCE.getAndIncrement();
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

        listeners.remove(listener);
    }
}
