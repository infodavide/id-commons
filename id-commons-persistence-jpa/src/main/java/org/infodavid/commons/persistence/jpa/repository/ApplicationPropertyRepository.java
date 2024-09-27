package org.infodavid.commons.persistence.jpa.repository;

import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.persistence.dao.ApplicationPropertyDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceException;

/**
 * The Interface ApplicationPropertyRepository.
 */
@Repository
public interface ApplicationPropertyRepository extends JpaRepository<ApplicationProperty, Long>, ApplicationPropertyDao {

    /*
     * (non-Javadoc)
     * @see org.infodavid.persistence.dao.ApplicationPropertyDao#deleteNotBuiltin()
     */
    @Override
    @Query("delete from ApplicationProperty where deletable=true")
    void deleteDeletable() throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.persistence.dao.ApplicationPropertyDao#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    @Query("select new org.infodavid.commons.model.DefaultEntityReference(id,name) from ApplicationProperty")
    Page<DefaultEntityReference> findReferences(final Pageable pageable) throws PersistenceException;
}
