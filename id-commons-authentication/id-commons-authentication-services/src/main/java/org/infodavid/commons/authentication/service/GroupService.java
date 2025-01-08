package org.infodavid.commons.authentication.service;

import java.io.OutputStream;
import java.util.Optional;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.EntityService;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The Class GroupService.
 */
public interface GroupService extends EntityService<Long, Group> {

    /**
     * Export the data in CSV format.
     * @param out the out
     * @throws ServiceException the service exception
     */
    void export(OutputStream out) throws ServiceException;

    /**
     * Find by name.
     * @param value the value
     * @return the optional
     * @throws ServiceException the service exception
     */
    Optional<Group> findByName(String value) throws ServiceException;

    /**
     * Find by role.
     * @param role     the role
     * @param pageable the page definition
     * @return the page
     * @throws ServiceException the service exception
     */
    Page<Group> findByRole(String role, Pageable pageable) throws ServiceException;

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
}
