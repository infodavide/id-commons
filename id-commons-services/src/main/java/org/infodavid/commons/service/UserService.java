package org.infodavid.commons.service;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.User;
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
     * Find by one of the given roles.
     * @param roles    the roles
     * @param pageable the page definition
     * @return the page
     * @throws ServiceException the service exception
     */
    Page<User> findByRole(Collection<String> roles, Pageable pageable) throws ServiceException;

    /**
     * Find by status.
     * @param connected the connected
     * @return the page
     * @throws ServiceException the service exception
     */
    List<User> findByStatus(boolean connected) throws ServiceException;

    /**
     * Gets the references.
     * @param pageable the page definition
     * @return the references
     * @throws ServiceException the service exception
     */
    Page<DefaultEntityReference> findReferences(Pageable pageable) throws ServiceException;

    /**
     * Gets the supported roles.
     * @return the supported roles
     */
    String[] getSupportedRoles();

    /**
     * Checks if is connected.
     * @param name the name
     * @return true, if user is connected or false if not connected or not found
     */
    boolean isConnected(String name);
}
