package org.infodavid.commons.authentication.persistence.jpa.repository;

import java.util.Optional;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.persistence.dao.GroupDao;
import org.infodavid.commons.model.DefaultEntityReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.PersistenceException;

/**
 * The Interface GroupRepository.
 */
public interface GroupRepository extends JpaRepository<Group, Long>, GroupDao {

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.GroupDao#findByProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Query(value = "select g.* from groups g inner join groups_properties p on p.group_id = g.id where ((?1 IS NULL and p.scope IS NULL) OR p.scope = ?1) and p.name = ?2 and p.data = ?3", countQuery = "select cound(g.id) from groups g inner join groups_properties p on p.group_id = g.id where ((?1 IS NULL and p.scope IS NULL) OR p.scope = ?1) and p.name = ?2 and p.data = ?3", nativeQuery = true)
    Optional<Group> findByProperty(final String scope, final String name, final String value) throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.GroupDao#findByRole(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    @Query(value = "select g.* from groups g where g.roles = ?1 or g.roles like '?1;%' or g.roles like '%;?1' or g.roles like '%;?1;%'", countQuery = "select cound(id) from groups g where g.roles = ?1 or g.roles like '?1;%' or g.roles like '%;?1' or g.roles like '%;?1;%'", nativeQuery = true)
    Page<Group> findByRole(String value, Pageable pageable) throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.GroupDao#findHavingProperty(java.lang.String, java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    @Query(value = "select g.* from groups g inner join groups_properties p on p.group_id = g.id where ((?1 IS NULL and p.scope IS NULL) OR p.scope = ?1) and p.name = ?2", countQuery = "select cound(g.id) from groups g inner join groups_properties p on p.group_id = g.id where ((?1 IS NULL and p.scope IS NULL) OR p.scope = ?1) and p.name = ?2", nativeQuery = true)
    Page<Group> findHavingProperty(String scope, String name, Pageable pageable) throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.GroupDao#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    @Query("select new org.infodavid.commons.model.DefaultEntityReference(id,name) from Group")
    Page<DefaultEntityReference> findReferences(final Pageable pageable) throws PersistenceException;
}
