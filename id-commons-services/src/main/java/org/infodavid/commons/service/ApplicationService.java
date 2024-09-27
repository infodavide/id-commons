package org.infodavid.commons.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.listener.ApplicationPropertyChangedListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The Class ApplicationService.<br>
 * If type of the property is Password, its value is stored using a two ways encoding (See StringUtils.encode()).<br>
 * If validate method receives an empty password, it tries to replace the value by the one from database.<br>
 * If validate method receives an non empty password, it encodes the value before update into the DB.<br>
 * Properties of type Password expose a plain password to allow other services to use the values without knowledge of encoding.<br>
 * Normally, password are not provided when listing or getting properties, this must be handled by the presentation layer.
 */
public interface ApplicationService extends EntityService<Long, ApplicationProperty> {

    /**
     * Adds the listener.
     * @param listener the listener
     */
    void addListener(ApplicationPropertyChangedListener listener);

    /**
     * Zip the backup of the application stuff.
     * @return the path of the zip file containing the backup
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    Path backup() throws ServiceException, IllegalAccessException;

    /**
     * Delete by name.
     * @param name the name
     * @throws ServiceException the service exception
     */
    void deleteByName(String name) throws ServiceException;

    /**
     * Export the data in CSV format.
     * @param out the out
     * @throws ServiceException the service exception
     */
    void export(OutputStream out) throws ServiceException;

    /**
     * Find by name.
     * @param name     the name
     * @param pageable the page definition
     * @return the page
     * @throws ServiceException the service exception
     */
    Page<ApplicationProperty> findByName(String name, Pageable pageable) throws ServiceException;

    /**
     * Find by scope.
     * @param scope    the scope
     * @param pageable the page definition
     * @return the page
     * @throws ServiceException the service exception
     */
    Page<ApplicationProperty> findByScope(String scope, Pageable pageable) throws ServiceException;

    /**
     * Find by scope and name.
     * @param scope the scope
     * @param name  the name
     * @return the collection
     * @throws ServiceException the service exception
     */
    Optional<ApplicationProperty> findByScopeAndName(String scope, String name) throws ServiceException;

    /**
     * Gets the references.
     * @param pageable the page definition
     * @return the references
     * @throws ServiceException the service exception
     */
    Page<DefaultEntityReference> findReferences(Pageable pageable) throws ServiceException;

    /**
     * Gets the application build number.
     * @return the build number
     */
    String getBuild();

    /**
     * Gets the health value.
     * @return the health value
     */
    String getHealthValue();

    /**
     * Gets the information.
     * @return the information
     */
    Map<String, String[]> getInformation();

    /**
     * Gets the application name.
     * @return the name
     */
    String getName();

    /**
     * Gets the root directory of the application.
     * @return the root directory
     */
    Path getRootDirectory();

    /**
     * Gets the up time in seconds.
     * @return the up time
     */
    long getUpTime();

    /**
     * Gets the application version.
     * @return the version
     */
    String getVersion();

    /**
     * Removes the listener.
     * @param listener the listener
     */
    void removeListener(ApplicationPropertyChangedListener listener);

    /**
     * Unzip the given stream and restore the application stuff.
     * @param in the input stream
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @throws InterruptedException   the interrupted exception
     */
    void restore(InputStream in) throws ServiceException, IllegalAccessException, InterruptedException;
}
