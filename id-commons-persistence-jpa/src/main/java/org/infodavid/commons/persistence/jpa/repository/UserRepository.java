package org.infodavid.commons.persistence.jpa.repository;

import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.UserDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.PersistenceException;

/**
 * The Interface UserRepository.
 */
public interface UserRepository extends JpaRepository<User, Long>, UserDao {

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.UserDao#deleteDeletable()
     */
    @Override
    @Query("delete from User where deletable=true")
    void deleteDeletable() throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.persistence.dao.UserDao#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    @Query("select new org.infodavid.commons.model.DefaultEntityReference(id,displayName) from User")
    Page<DefaultEntityReference> findReferences(final Pageable pageable) throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.UserDao#findByRole(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    @Query(value = "select * from users u where u.roles = ?1 or u.roles like '?1;%' or u.roles like '%;?1' or u.roles like '%;?1;%'", countQuery = "select cound(id) from users u where u.roles = ?1 or u.roles like '?1;%' or u.roles like '%;?1' or u.roles like '%;?1;%'", nativeQuery = true)
    Page<User> findByRole(String value, Pageable pageable) throws PersistenceException;
}
