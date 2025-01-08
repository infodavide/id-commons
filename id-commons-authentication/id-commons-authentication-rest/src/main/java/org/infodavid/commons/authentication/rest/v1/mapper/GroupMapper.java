package org.infodavid.commons.authentication.rest.v1.mapper;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.rest.v1.api.dto.GroupDto;
import org.infodavid.commons.rest.v1.mapper.CommonMapper;
import org.infodavid.commons.rest.v1.mapper.EntityPropertyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * The Interface GroupMapper.
 */
@Mapper(uses = {
        CommonMapper.class,
        EntityPropertyMapper.class
})
public interface GroupMapper { // NOSONAR See Mapstruct documentation

    /** The instance. */
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    /**
     * To DTO.
     * @param entity the entity
     * @return the DTO
     */
    @Mappings(value = {
            @Mapping(target = "editable", ignore = true),
            @Mapping(target = "deletable", ignore = true),
            @Mapping(target = "properties", ignore = true)
    })
    GroupDto map(Group entity);

    /**
     * To entity.
     * @param dto the DTO
     * @return the entity
     */

    @Mappings(value = {
            @Mapping(target = "properties", ignore = true)
    })
    Group map(GroupDto dto);
}
