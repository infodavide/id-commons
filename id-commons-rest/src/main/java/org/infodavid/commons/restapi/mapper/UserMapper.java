package org.infodavid.commons.restapi.mapper;

import org.infodavid.commons.model.User;
import org.infodavid.commons.restapi.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * The Interface UserMapper.
 */
@Mapper(uses = { CommonMapper.class, PropertyMapper.class })
public interface UserMapper { // NOSONAR See Mapstruct documentation

    /** The instance. */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * To DTO.
     * @param entity the entity
     * @return the DTO
     */
    @Mappings(value = { @Mapping(target = "editable", ignore = true), @Mapping(target = "password", ignore = true), @Mapping(target = "connected", ignore = true) })
    UserDto map(User entity);

    /**
     * To entity.
     * @param dto the DTO
     * @return the entity
     */

    @Mappings(value = { @Mapping(target = "archivingDate", ignore = true), @Mapping(target = "creationDate", ignore = true), @Mapping(target = "modificationDate", ignore = true), @Mapping(target = "connectionsCount", ignore = true), @Mapping(target = "lastConnectionDate", ignore = true) })
    User map(UserDto dto);
}
