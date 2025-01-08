package org.infodavid.commons.service.test;

import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.persistence.dao.ConfigurationPropertyDao;
import org.infodavid.commons.service.test.persistence.dao.AbstractDefaultDaoMock;
import org.infodavid.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The Class DataInitializer.
 */
@Component
public class DataInitializer {

    /** The Constant LOGGER. */
    public static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    /** The configuration property data access object. */
    private ConfigurationPropertyDao configurationPropertyDao;

    /**
     * Instantiates a new test data initializer.
     * @param configurationPropertyDao the configuration property data access object
     */
    public DataInitializer(final ConfigurationPropertyDao configurationPropertyDao) {
        this.configurationPropertyDao = configurationPropertyDao;
    }

    /**
     * Initialize.
     */
    @SuppressWarnings("rawtypes")
    public void initialize() {
        LOGGER.info("Initializing data...");
        // populate mocks of data access objects
        // properties
        ((AbstractDefaultDaoMock) configurationPropertyDao).clear();
        ConfigurationProperty property = new ConfigurationProperty(org.infodavid.commons.model.Constants.SCHEMA_VERSION_PROPERTY, PropertyType.STRING, "1.0");
        property.setScope(org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
        property.setDeletable(false);
        configurationPropertyDao.insert(property);
        property = new ConfigurationProperty(org.infodavid.commons.service.Constants.APPLICATION_NAME_PROPERTY, PropertyType.STRING, "TestApplication");
        property.setScope(org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
        property.setDeletable(false);
        configurationPropertyDao.insert(property);
        property = new ConfigurationProperty(org.infodavid.commons.service.Constants.APPLICATION_VERSION_PROPERTY, PropertyType.STRING, "1.0");
        property.setScope(org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
        property.setDeletable(false);
        configurationPropertyDao.insert(property);
        property = new ConfigurationProperty(org.infodavid.commons.service.Constants.BUILD_NUMBER_PROPERTY, PropertyType.STRING, "1");
        property.setScope(org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
        property.setDeletable(false);
        configurationPropertyDao.insert(property);
        property = new ConfigurationProperty("grantedTo", PropertyType.STRING, "Infodavid");
        property.setScope(org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
        property.setDeletable(false);
        configurationPropertyDao.insert(property);
        property = new ConfigurationProperty("Param1", PropertyType.STRING, "test");
        property.setScope(org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
        property.setDeletable(false);
        configurationPropertyDao.insert(property);
        property = new ConfigurationProperty("Param2", PropertyType.STRING, "test");
        property.setScope(org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
        property.setDeletable(true);
        configurationPropertyDao.insert(property);
        property = new ConfigurationProperty("Param3", PropertyType.PASSWORD, new String(StringUtils.encode("test")));
        property.setScope(org.infodavid.commons.service.Constants.APPLICATION_SCOPE);
        property.setDeletable(true);
        configurationPropertyDao.insert(property);
        LOGGER.info("{} properties inserted", String.valueOf(configurationPropertyDao.count())); // NOSONAR For testing
    }
}
