package org.infodavid.commons.persistence.mongodb.repository;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.PersistentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.support.PageableExecutionUtils;

import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractContentSearchRepositoryImpl.
 * @param <K> the key type
 * @param <T> the generic type
 */
@Slf4j
public abstract class AbstractContentSearchRepositoryImpl<K extends Serializable, T extends PersistentEntity<K>> implements ContentSearchRepository<K, T> {

    /** The entity class. */
    private Class<T> entityClass;

    /** The template. */
    private MongoTemplate mongoTemplate;

    /** The operations. */
    private final MongoOperations operations;

    /**
     * Instantiates a new content search repository.
     * @param mongoTemplate the template
     * @param operations    the operations
     * @param entityClass   the entity class
     */
    AbstractContentSearchRepositoryImpl(final MongoTemplate mongoTemplate, final MongoOperations operations, final Class<T> entityClass) {
        this.mongoTemplate = mongoTemplate;
        this.operations = operations;
        this.entityClass = entityClass;
    }

    /**
     * Gets the text index fields.
     * @return the text index fields
     */
    protected abstract String[] getTextIndexFields();

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.ContentSearch#search(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<T> search(final String needle, final Pageable pageable) throws PersistenceException {
        LOGGER.debug("Searching needle: {}", needle);

        if (StringUtils.isEmpty(needle)) {
            return Page.empty();
        }

        operations.indexOps(entityClass).ensureIndex(new TextIndexDefinitionBuilder().onFields(getTextIndexFields()).build());
        final String[] parameters = needle.split(" ");
        final Query query = Query.query(new TextCriteria().matchingAny(parameters)).with(pageable);

        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, entityClass),
                pageable,
                () -> mongoTemplate.count(query.skip(0).limit(0), entityClass));
    }
}
