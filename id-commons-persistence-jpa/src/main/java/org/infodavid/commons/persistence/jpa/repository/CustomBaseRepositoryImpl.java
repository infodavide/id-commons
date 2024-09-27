package org.infodavid.commons.persistence.jpa.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.infodavid.commons.model.PersistentObject;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

/**
 * The Class CustomBaseRepositoryImpl.
 * @param <K> the key type
 * @param <T> the generic type
 */
public class CustomBaseRepositoryImpl<K extends Serializable, T extends PersistentObject<K>> extends SimpleJpaRepository<T, K> {

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
    public void insert(final Collection<T> values) throws PersistenceException {
        if (values == null || values.isEmpty()) {
            return;
        }

        for (final T value : values) {
            if (value.getCreationDate() == null) {
                value.setCreationDate(new Date());
            }

            if (value.getModificationDate() == null) {
                value.setModificationDate(new Date());
            }

            entityManager.persist(value);
        }

        entityManager.flush();
    }

    /**
     * Insert.
     * @param value the value
     * @throws PersistenceException the persistence exception
     */
    public void insert(final T value) throws PersistenceException {
        if (value.getCreationDate() == null) {
            value.setCreationDate(new Date());
        }

        if (value.getModificationDate() == null) {
            value.setModificationDate(new Date());
        }

        entityManager.persist(value);
        entityManager.flush();
    }

    /**
     * Update.
     * @param values the values
     * @throws PersistenceException the persistence exception
     */
    public void update(final Collection<T> values) throws PersistenceException {
        if (values == null || values.isEmpty()) {
            return;
        }

        for (final T value : values) {
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
    public void update(final T value) throws PersistenceException {
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
