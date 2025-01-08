package org.infodavid.commons.service.test.persistence.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.persistence.dao.ConfigurationPropertyDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Class ConfigurationPropertyDaoMock.
 */
public class ConfigurationPropertyDaoMock extends AbstractDefaultDaoMock<Long, ConfigurationProperty> implements ConfigurationPropertyDao {

    /** The Constant SEQUENCE. */
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    /**
     * Instantiates a new configuration property data access object mock.
     */
    public ConfigurationPropertyDaoMock() {
        super(Long.class, ConfigurationProperty.class);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.test.persistence.dao.AbstractDefaultDaoMock#clear()
     */
    @Override
    public void clear() {
        super.clear();
        SEQUENCE.set(1);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.ConfigurationPropertyDao#deleteByName(java.lang.String)
     */
    @Override
    public void deleteByName(final String value) throws PersistenceException {
        final Iterator<ConfigurationProperty> ite = map.values().iterator();

        while (ite.hasNext()) {
            final ConfigurationProperty property = ite.next();

            if (StringUtils.equals(property.getName(), value)) {
                ite.remove();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.ConfigurationPropertyDao#deleteDeletable()
     */
    @Override
    public void deleteDeletable() throws PersistenceException {
        final Iterator<ConfigurationProperty> ite = map.values().iterator();

        while (ite.hasNext()) {
            final ConfigurationProperty property = ite.next();

            if (!property.isDeletable()) {
                ite.remove();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.ConfigurationPropertyDao#findByName(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<ConfigurationProperty> findByName(final String value, final Pageable pageable) {
        final List<ConfigurationProperty> results = new ArrayList<>();

        for (final Entry<Long, ConfigurationProperty> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getName(), value)) {
                results.add(clone(entry.getValue()));
            }
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.ConfigurationPropertyDao#findByScope(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<ConfigurationProperty> findByScope(final String value, final Pageable pageable) throws PersistenceException {
        final List<ConfigurationProperty> results = new ArrayList<>();

        for (final Entry<Long, ConfigurationProperty> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getScope(), value)) {
                results.add(clone(entry.getValue()));
            }
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.ConfigurationPropertyDao#findByScopeAndName(java.lang.String, java.lang.String)
     */
    @Override
    public Optional<ConfigurationProperty> findByScopeAndName(final String scope, final String name) throws PersistenceException {
        for (final Entry<Long, ConfigurationProperty> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getScope(), scope) && StringUtils.equals(entry.getValue().getName(), name)) {
                return Optional.ofNullable(clone(entry.getValue()));
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.ConfigurationPropertyDao#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<DefaultEntityReference> findReferences(final Pageable pageable) throws PersistenceException {
        final List<DefaultEntityReference> results = new ArrayList<>();

        for (final Entry<Long, ConfigurationProperty> entry : map.entrySet()) {
            results.add(new DefaultEntityReference(entry.getKey(), entry.getValue().getName()));
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.test.persistence.dao.AbstractDefaultDaoMock#nextId()
     */
    @SuppressWarnings("boxing")
    @Override
    protected Long nextId() {
        return SEQUENCE.getAndIncrement();
    }
}
