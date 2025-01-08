package org.infodavid.commons.persistence.mongodb.repository;

import java.io.Serializable;

import org.infodavid.commons.model.PersistentEntity;
import org.infodavid.commons.persistence.dao.ContentSearch;

/**
 * The Interface ContentSearchRepository.br/>
 * Used to implement partial content search.
 */
public interface ContentSearchRepository<K extends Serializable, T extends PersistentEntity<K>> extends ContentSearch<K, T> {
    // noop
}
