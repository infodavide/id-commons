package org.infodavid.commons.authentication.rest.v1.mapper;

import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.rest.v1.api.dto.UserDto;
import org.infodavid.commons.rest.v1.mapper.CommonMapper;
import org.infodavid.commons.rest.v1.mapper.EntityPropertyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * The Interface UserMapper.
 */
@Mapper(uses = {
        CommonMapper.class,
        EntityPropertyMapper.class
})
public interface UserMapper { // NOSONAR See Mapstruct documentation

    /** The instance. */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * To DTO.
     * @param entity the entity
     * @return the DTO
     */
    @Mappings(value = {
            @Mapping(target = "editable", ignore = true),
            @Mapping(target = "properties", ignore = true),
            @Mapping(target = "deletable", ignore = true),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "connected", ignore = true)
    })
    UserDto map(User entity);

    /**
     * To entity.
     * @param dto the DTO
     * @return the entity
     */

    @Mappings(value = {
            @Mapping(target = "archivingDate", ignore = true),
            @Mapping(target = "connectionsCount", ignore = true),
            @Mapping(target = "lastConnectionDate", ignore = true),
            @Mapping(target = "properties", ignore = true)
    })
    User map(UserDto dto);
}
