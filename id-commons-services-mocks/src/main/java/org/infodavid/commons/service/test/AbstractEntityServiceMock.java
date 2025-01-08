package org.infodavid.commons.service.test;

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
import org.infodavid.commons.service.EntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;
import lombok.Getter;

/**
 * The Class AbstractEntityServiceMock.
 * @param <K> the key type
 * @param <T> the generic type
 */
public abstract class AbstractEntityServiceMock<K extends Serializable, T extends PersistentEntity<K>> implements EntityService<K, T> {

    /** The entity class. */
    @Getter
    private final Class<T> entityClass;

    /** The identifier class. */
    @Getter
    private final Class<K> identifierClass;

    /** The map. */
    protected Map<K, T> map = new ConcurrentHashMap<>();

    /**
     * Instantiates a new abstract service.
     * @param identifierClass the identifier class
     * @param entityClass     the entity class
     */
    protected AbstractEntityServiceMock(final Class<K> identifierClass, final Class<T> entityClass) {
        this.identifierClass = identifierClass;
        this.entityClass = entityClass;
    }

    /**
     * Adds the.
     * @param values the values
     * @return the collection
     */
    public Collection<T> add(final Collection<T> values) {
        if (values == null) {
            return Collections.emptyList();
        }

        final List<T> inserted = new ArrayList<>();

        for (final T value : values) {
            inserted.add(add(value));
        }

        return inserted;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.EntityService#add(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    public T add(final T value) {
        if (value.getId() == null) {
            value.setId(nextId());
        }

        value.setCreationDate(new Date());
        value.setModificationDate(value.getCreationDate());
        map.put(value.getId(), clone(value));

        return clone(value);
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
     * @param <S>    the generic type
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
     * @param <S>    the generic type
     * @param source the source
     * @return the clone
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <S extends T> S clone(final S source) {
        final Constructor<? extends PersistentEntity> constructor = ConstructorUtils.getAccessibleConstructor(source.getClass(), source.getClass());

        if (constructor == null) {
            throw new IllegalStateException("No accessible constructor by copy found for class: " + source.getClass().getName());
        }

        try {
            return (S) constructor.newInstance(source);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.EntityService#count()
     */
    @Override
    public long count() throws PersistenceException {
        return map.size();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.EntityService#deleteById(java.io.Serializable)
     */
    @Override
    public void deleteById(final K id) throws PersistenceException {
        map.remove(id);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.EntityService#find(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<T> find(final Pageable pageable) {
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
     * @see org.infodavid.commons.service.EntityService#findById(java.io.Serializable)
     */
    @Override
    public Optional<T> findById(final K id) {
        return Optional.ofNullable(clone(map.get(id)));
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
     * @see org.infodavid.commons.service.EntityService#update(java.util.Collection)
     */
    @Override
    public void update(final Collection<T> values) {
        if (values == null) {
            return;
        }

        for (final T value : values) {
            update(value);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.EntityService#update(org.infodavid.commons.model.PersistentEntity)
     */
    @Override
    public void update(final T value) {
        value.setModificationDate(new Date());
        map.put(value.getId(), clone(value));
    }
}
