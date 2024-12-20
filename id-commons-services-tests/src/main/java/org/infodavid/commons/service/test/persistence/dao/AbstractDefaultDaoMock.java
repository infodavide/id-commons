package org.infodavid.commons.service.test.persistence.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.infodavid.commons.model.PersistentObject;
import org.infodavid.commons.persistence.dao.DefaultDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Class AbstractDefaultDaoMock.
 * @param <K> the key type
 * @param <T> the generic type
 */
public abstract class AbstractDefaultDaoMock<K extends Serializable, T extends PersistentObject<K>> implements DefaultDao<K, T> {

    /** The map. */
    protected Map<K, T> map = new ConcurrentHashMap<>();

    /**
     * Backup.
     * @return the map
     */
    public Map<K, T> backup() {
        return new HashMap<>(map);
    }

    /**
     * Clear.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Clone.
     * @param values the values
     * @return the list
     */
    protected List<T> clone(final Collection<T> values) {
        final List<T> results = new ArrayList<>();
        values.forEach(t -> results.add(clone(t)));

        return results;
    }

    /**
     * Clone.
     * @param value the value
     * @return the t
     */
    protected abstract T clone(T value);

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#count()
     */
    @Override
    public long count() throws PersistenceException {
        return map.size();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#deleteById(java.io.Serializable)
     */
    @Override
    public void deleteById(final K id) throws PersistenceException {
        map.remove(id);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#findAll(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<T> findAll(final Pageable pageable) throws PersistenceException {
        final List<T> results = new ArrayList<>();
        map.values().forEach(t -> results.add(clone(t)));

        if (pageable.isUnpaged() || pageable.getOffset() < 0 || pageable.getOffset() >= results.size() - 1) {
            return new PageImpl<>(results);
        }

        int limit = pageable.getPageSize();

        if (limit > results.size()) {
            limit = (int) (results.size() - pageable.getOffset());
        }

        return new PageImpl<>(results.subList((int) pageable.getOffset(), limit));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#findById(java.io.Serializable)
     */
    @Override
    public Optional<T> findById(final K id) throws PersistenceException {
        return Optional.ofNullable(clone(map.get(id)));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#insert(java.util.Collection)
     */
    @Override
    public void insert(final Collection<T> values) throws PersistenceException {
        if (values == null || values.isEmpty()) {
            return;
        }

        for (final T value : values) {
            insert(value);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#insert(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    public void insert(final T value) throws PersistenceException {
        if (value.getId() == null) {
            value.setId(nextId());
        }

        value.setCreationDate(new Date());
        value.setModificationDate(value.getCreationDate());
        map.put(value.getId(), clone(value));
    }

    /**
     * Next id.
     * @return the new identifier
     */
    protected abstract K nextId();

    /**
     * Restore.
     * @param backup the backup
     */
    public void restore(final Map<K, T> backup) {
        map.clear();
        map.putAll(backup);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#update(java.util.Collection)
     */
    @Override
    public void update(final Collection<T> values) throws PersistenceException {
        if (values == null || values.isEmpty()) {
            return;
        }

        for (final T value : values) {
            update(value);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#update(org.infodavid.commons.model.PersistentObject)
     */
    @Override
    public void update(final T value) throws PersistenceException {
        value.setModificationDate(new Date());
        map.put(value.getId(), clone(value));
    }
}