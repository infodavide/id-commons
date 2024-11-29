package org.infodavid.commons.persistence.jpa.repository;

import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.persistence.dao.ConfigurationPropertyDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceException;

/**
 * The Interface ConfigurationPropertyRepository.
 */
@Repository
public interface ConfigurationPropertyRepository extends JpaRepository<ConfigurationProperty, Long>, ConfigurationPropertyDao {

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.ConfigurationPropertyDao#deleteDeletable()
     */
    @Override
    @Query("delete from ConfigurationProperty where deletable=true")
    void deleteDeletable() throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.ConfigurationPropertyDao#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    @Query("select new org.infodavid.commons.model.DefaultEntityReference(id,name) from ConfigurationProperty")
    Page<DefaultEntityReference> findReferences(final Pageable pageable) throws PersistenceException;
}
