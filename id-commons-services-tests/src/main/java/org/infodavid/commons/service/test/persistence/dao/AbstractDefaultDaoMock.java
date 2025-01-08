package org.infodavid.commons.service.test.persistence.dao;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.infodavid.commons.model.PersistentEntity;
import org.infodavid.commons.persistence.dao.DefaultDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import lombok.Getter;

/**
 * The Class AbstractDefaultDaoMock.
 * @param <K> the key type
 * @param <T> the generic type
 */
public abstract class AbstractDefaultDaoMock<K extends Serializable, T extends PersistentEntity<K>> implements DefaultDao<K, T> {

    /** The entity class. */
    @Getter
    protected final Class<T> entityClass;

    /** The identifier class. */
    @Getter
    private final Class<K> identifierClass;

    /** The map. */
    protected Map<K, T> map = new ConcurrentHashMap<>();

    /**
     * Instantiates a new abstract default data access object mock.
     * @param identifierClass the identifier class
     * @param entityClass     the entity class
     */
    protected AbstractDefaultDaoMock(final Class<K> identifierClass, final Class<T> entityClass) {
        this.entityClass = entityClass;
        this.identifierClass = identifierClass;
    }

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
    protected <S extends T> List<S> clone(final Collection<S> values) {
        final List<S> results = new ArrayList<>();
        values.forEach(t -> results.add(clone(t)));

        return results;
    }

    /**
     * Clone.
     * @param value the value
     * @return the clone
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <S extends T> S clone(final S source) {
        final Constructor<? extends PersistentEntity> constructor = ConstructorUtils.getAccessibleConstructor(entityClass, entityClass);

        if (constructor == null) {
            throw new IllegalStateException("No accessible constructor by copy found for class: " + entityClass.getName());
        }

        try {
            return (S) constructor.newInstance(source);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#count()
     */
    @Override
    public long count() {
        return map.size();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#deleteById(java.io.Serializable)
     */
    @Override
    public void deleteById(final K id) {
        map.remove(id);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#findAll(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<T> findAll(final Pageable pageable) {
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
    public Optional<T> findById(final K id) {
        return Optional.ofNullable(clone(map.get(id)));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#insert(java.lang.Iterable)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <S extends T> List<S> insert(final Iterable<S> values) {
        if (values == null) {
            return Collections.emptyList();
        }

        final List<S> inserted = new ArrayList<>();

        for (final T value : values) {
            inserted.add((S) insert(value));
        }

        return inserted;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#insert(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    public <S extends T> S insert(final S value) {
        if (value.getId() == null) {
            value.setId(nextId());
        }

        value.setCreationDate(new Date());
        value.setModificationDate(value.getCreationDate());
        map.put(value.getId(), clone(value));

        return clone(value);
    }

    /**
     * Next identifier.
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
     * @see org.infodavid.commons.persistence.dao.DefaultDao#update(java.lang.Iterable)
     */
    @Override
    public <S extends T> void update(final Iterable<S> values) {
        if (values == null) {
            return;
        }

        for (final T value : values) {
            update(value);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.DefaultDao#update(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    public <S extends T> void update(final S value) {
        value.setModificationDate(new Date());
        map.put(value.getId(), clone(value));
    }
}
