package org.infodavid.commons.restapi.mapper;

import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.restapi.dto.PropertyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * The Interface PropertyMapper.
 */
@Mapper(uses = { CommonMapper.class, PropertyMapper.class })
public interface ApplicationPropertyMapper { // NOSONAR See Mapstruct documentation

    /** The instance. */
    ApplicationPropertyMapper INSTANCE = Mappers.getMapper(ApplicationPropertyMapper.class);

    /**
     * To DTO.
     * @param entity the entity
     * @return the DTO
     */
    @Mappings(value = { @Mapping(target = "editable", ignore = true) })
    default PropertyDto map(final ApplicationProperty entity) {
        if (entity == null) {
            return null;
        }

        final PropertyDto result = Mappers.getMapper(PropertyMapper.class).map(entity);

        result.setCreationDate(entity.getCreationDate());

        if (entity.getId() != null) {
            result.setId(String.valueOf(entity.getId()));
        }

        result.setModificationDate(entity.getModificationDate());

        return result;
    }

    /**
     * To entity.<br/>
     * @param dto the DTO
     * @return the entity
     */
    @Mappings(value = { @Mapping(target = "archivingDate", ignore = true), @Mapping(target = "creationDate", ignore = true), @Mapping(target = "modificationDate", ignore = true) })
    ApplicationProperty map(PropertyDto dto);
}
