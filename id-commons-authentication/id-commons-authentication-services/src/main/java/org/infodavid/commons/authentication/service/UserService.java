package org.infodavid.commons.authentication.service;

import java.io.OutputStream;
import java.util.Optional;

import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.EntityService;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The Class UserService.<br>
 * Password is stored as provided by the presentation layer, typically in MD5.<br>
 * If validate method receives an empty password, it tries to replace the value by the one from database.<br>
 * If validate method receives an non empty password, it leaves it untouched and the value is stored as it is (typically in MD5).<br>
 * Normally, password are not provided when listing or getting users, this must be handled by the presentation layer.
 */
public interface UserService extends EntityService<Long, User> {

    /**
     * Export the data in CSV format.
     * @param out the out
     * @throws ServiceException the service exception
     */
    void export(OutputStream out) throws ServiceException;

    /**
     * Find by email.
     * @param value the value
     * @return the optional
     * @throws ServiceException the service exception
     */
    Optional<User> findByEmail(String value) throws ServiceException;

    /**
     * Find by name.
     * @param value the value
     * @return the optional
     * @throws ServiceException the service exception
     */
    Optional<User> findByName(String value) throws ServiceException;

    /**
     * Find by role.
     * @param role     the role
     * @param pageable the page definition
     * @return the page
     * @throws ServiceException the service exception
     */
    Page<User> findByRole(String role, Pageable pageable) throws ServiceException;

    /**
     * Gets the references.
     * @param pageable the page definition
     * @return the references
     * @throws ServiceException the service exception
     */
    Page<DefaultEntityReference> findReferences(Pageable pageable) throws ServiceException;

    /**
     * Checks for role.
     * @param user  the user
     * @param value the value
     * @return true, if successful
     * @throws ServiceException the service exception
     */
    boolean hasRole(User user, String value) throws ServiceException;
}
