package org.infodavid.commons.authentication.rest;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.rest.v1.api.dto.GroupDto;
import org.infodavid.commons.authentication.rest.v1.api.dto.UserDto;
import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.Constants;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.PropertyType;

/**
 * The Class DataInitializer.
 */
public class DataInitializer {

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
     * New group.
     * @return the group
     */
    @SuppressWarnings("boxing")
    public Group newGroup() {
        final Group result = new Group();
        result.setId(System.nanoTime());
        result.setName("users" + result.getId());
        result.getRoles().add(Constants.USER_ROLE);

        return result;
    }

    /**
     * New group dto.
     * @return the group dto
     */
    @SuppressWarnings("boxing")
    public GroupDto newGroupDto() {
        final GroupDto result = new GroupDto();
        result.setId(System.nanoTime());
        result.setName("users" + result.getId());
        result.getRoles().add(Constants.USER_ROLE);

        return result;
    }

    /**
     * New entity.
     * @return the user
     */
    @SuppressWarnings("boxing")
    public User newUser() {
        final Group group1 = newGroup();
        final User result = new User();
        result.setId(System.nanoTime());
        result.setDisplayName("User " + result.getId());
        result.setName("user" + System.nanoTime());
        result.setPassword("24C9E15E52AFC47C225B757E7BEE1F9D");
        result.setEmail(result.getName() + "@infodavid.org");
        result.getGroups().add(group1);

        return result;
    }

    /**
     * New user.
     * @return the DTO
     */
    @SuppressWarnings("boxing")
    public UserDto newUserDto() {
        final GroupDto group1 = newGroupDto();
        final UserDto result = new UserDto();
        result.setId(System.nanoTime());
        result.setDisplayName("User " + result.getId());
        result.setName("user" + System.nanoTime());
        result.setPassword("24C9E15E52AFC47C225B757E7BEE1F9D");
        result.setEmail(result.getName() + "@infodavid.org");
        result.getGroups().add(group1);

        return result;
    }
}
