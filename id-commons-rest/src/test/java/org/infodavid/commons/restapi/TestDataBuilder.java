package org.infodavid.commons.restapi;

import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.Property;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.model.User;
import org.infodavid.commons.util.collection.CollectionUtils;

/**
 * The Class TestDataBuilder.
 */
public class TestDataBuilder {

    /**
     * New property.
     * @return the property
     */
    public ApplicationProperty newApplicationProperty() {
        final ApplicationProperty result = new ApplicationProperty();
        result.setName("test-" + System.nanoTime());
        result.setType(PropertyType.STRING);

        return result;
    }

    /**
     * New property.
     * @return the property
     */
    public Property newProperty() {
        final Property result = new Property();
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
