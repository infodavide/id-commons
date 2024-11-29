package org.infodavid.commons.authentication.rest;

import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.util.collection.CollectionUtils;

/**
 * The Class DataBuilder.
 */
public class DataBuilder {

    /**
     * New configuration property.
     * @return the configuration property
     */
    public ConfigurationProperty newConfigurationProperty() {
        final ConfigurationProperty result = new ConfigurationProperty();
        result.setName("test-" + System.nanoTime());
        result.setType(PropertyType.STRING);

        return result;
    }

    /**
     * New entity property.
     * @return the entity property
     */
    public EntityProperty newEntityProperty() {
        final EntityProperty result = new EntityProperty();
        result.setName("test-" + System.nanoTime());
        result.setType(PropertyType.STRING);

        return result;
    }

    /**
     * New user.
     * @return the user
     */
    public User newUser() {
        final User result = new User();
        result.setDisplayName("User " + result.getId());
        result.setName("test-" + System.nanoTime());
        result.setPassword("24C9E15E52AFC47C225B757E7BEE1F9D");
        result.setEmail(result.getName() + "@infodavid.org");
        result.setRoles(CollectionUtils.of(org.infodavid.commons.model.Constants.USER_ROLE));

        return result;
    }
}
