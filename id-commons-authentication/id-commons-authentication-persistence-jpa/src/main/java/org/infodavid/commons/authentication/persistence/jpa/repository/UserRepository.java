package org.infodavid.commons.authentication.persistence.jpa.repository;

import java.util.Optional;

import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.model.DefaultEntityReference;
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
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#deleteDeletable()
     */
    @Override
    @Query("delete from User where deletable=true")
    void deleteDeletable() throws PersistenceException;

    @Override
    @Query(value = "select u.* from users u inner join users_properties p on p.user_id = u.id where ((?1 IS NULL and p.scope IS NULL) OR p.scope = ?1) and p.name = ?2 and p.data = ?3", countQuery = "select cound(u.id) from users u inner join users_properties p on p.user_id = u.id where ((?1 IS NULL and p.scope IS NULL) OR p.scope = ?1) and p.name = ?2 and p.data = ?3", nativeQuery = true)
    Optional<User> findByProperty(final String scope, final String name, final String value) throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findByRole(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    @Query(value = "select u.* from users u where u.roles = ?1 or u.roles like '?1;%' or u.roles like '%;?1' or u.roles like '%;?1;%'", countQuery = "select cound(id) from users u where u.roles = ?1 or u.roles like '?1;%' or u.roles like '%;?1' or u.roles like '%;?1;%'", nativeQuery = true)
    Page<User> findByRole(String value, Pageable pageable) throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findHavingProperty(java.lang.String, java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    @Query(value = "select u.* from users u inner join users_properties p on p.user_id = u.id where ((?1 IS NULL and p.scope IS NULL) OR p.scope = ?1) and p.name = ?2", countQuery = "select cound(u.id) from users u inner join users_properties p on p.user_id = u.id where ((?1 IS NULL and p.scope IS NULL) OR p.scope = ?1) and p.name = ?2", nativeQuery = true)
    Page<User> findHavingProperty(String scope, String name, Pageable pageable) throws PersistenceException;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    @Query("select new org.infodavid.commons.model.DefaultEntityReference(id,displayName) from User")
    Page<DefaultEntityReference> findReferences(final Pageable pageable) throws PersistenceException;
}
