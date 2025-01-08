package org.infodavid.commons.persistence.jpa.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.infodavid.commons.model.PersistentEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

/**
 * The Class CustomBaseRepositoryImpl.
 * @param <K> the key type
 * @param <T> the generic type
 */
public class CustomBaseRepositoryImpl<K extends Serializable, T extends PersistentEntity<K>> extends SimpleJpaRepository<T, K> {

    /** The entity information. */
    private final JpaEntityInformation<T, K> entityInformation;

    /** The entity manager. */
    private final EntityManager entityManager;

    /**
     * Instantiates a new custom base repository.
     * @param entityInformation the entity information
     * @param entityManager     the entity manager
     */
    protected CustomBaseRepositoryImpl(final JpaEntityInformation<T, K> entityInformation, final EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    /**
     * Gets the entity manager.
     * @return the entity manager
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Insert.
     * @param values the values
     * @throws PersistenceException the persistence exception
     */
    public <S extends T> List<S> insert(final Iterable<S> values) throws PersistenceException {
        if (values == null) {
            return Collections.emptyList();
        }

        final List<S> results = new ArrayList<>();

        for (final S value : values) {
            if (value.getCreationDate() == null) {
                value.setCreationDate(new Date());
            }

            if (value.getModificationDate() == null) {
                value.setModificationDate(new Date());
            }

            entityManager.persist(value);
            results.add(value);
        }

        entityManager.flush();

        return results;
    }

    /**
     * Insert.
     * @param value the value
     * @throws PersistenceException the persistence exception
     */
    public <S extends T> S insert(final S value) throws PersistenceException {
        if (value.getCreationDate() == null) {
            value.setCreationDate(new Date());
        }

        if (value.getModificationDate() == null) {
            value.setModificationDate(new Date());
        }

        entityManager.persist(value);
        entityManager.flush();

        return value;
    }

    /**
     * Update.
     * @param values the values
     * @throws PersistenceException the persistence exception
     */
    public <S extends T> void update(final Iterable<S> values) throws PersistenceException {
        if (values == null) {
            return;
        }

        for (final S value : values) {
            if (value.getCreationDate() == null) {
                value.setCreationDate(new Date());
            }

            if (value.getModificationDate() == null) {
                value.setModificationDate(new Date());
            }

            if (entityInformation.isNew(value)) {
                entityManager.persist(value);
            } else {
                entityManager.merge(value);
            }
        }
    }

    /**
     * Update.
     * @param value the value
     * @throws PersistenceException the persistence exception
     */
    public <S extends T> void update(final S value) throws PersistenceException {
        if (value.getCreationDate() == null) {
            value.setCreationDate(new Date());
        }

        if (value.getModificationDate() == null) {
            value.setModificationDate(new Date());
        }

        if (entityInformation.isNew(value)) {
            entityManager.persist(value);
        } else {
            entityManager.merge(value);
        }
    }
}
