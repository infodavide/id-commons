package org.infodavid.commons.rest.v1.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.decorator.PropertiesDecorator;
import org.infodavid.commons.rest.v1.api.dto.EntityPropertyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * The Interface EntityPropertyMapper.
 */
@Mapper(uses = { CommonMapper.class })
public interface EntityPropertyMapper { // NOSONAR See Mapstruct documentation

    /** The instance. */
    EntityPropertyMapper INSTANCE = Mappers.getMapper(EntityPropertyMapper.class);

    /**
     * To DTO.
     * @param entity the entity
     * @return the DTO
     */
    @Mappings(value = { @Mapping(target = "editable", ignore = true) })
    default EntityPropertyDto map(final EntityProperty entity) {
        if (entity == null) {
            return null;
        }

        final EntityPropertyDto propertyDto = new EntityPropertyDto();
        propertyDto.setDeletable(entity.isDeletable());
        propertyDto.setName(entity.getName());
        propertyDto.setLabel(entity.getLabel());
        propertyDto.setReadOnly(entity.isReadOnly());
        propertyDto.setScope(entity.getScope());

        if (entity.getType() != null) { // NOSONAR Keep null check
            propertyDto.setType(entity.getType().name());
        }

        if (propertyDto.getLabel() == null) {
            propertyDto.setLabel(propertyDto.getName());
        }

        propertyDto.setTypeDefinition(entity.getTypeDefinition());
        propertyDto.setValue(entity.getValue());
        propertyDto.setDefaultValue(entity.getDefaultValue());
        propertyDto.setMinimum(entity.getMinimum());
        propertyDto.setMaximum(entity.getMaximum());

        return propertyDto;
    }

    /**
     * Map.
     * @param dto   the map of DTO
     * @param value the value
     */
    @Mappings(value = { @Mapping(target = "delegate", ignore = true) })
    default void map(final List<EntityPropertyDto> dto, @MappingTarget final PropertiesDecorator value) {
        if (value != null) {
            for (final EntityPropertyDto property : dto) {
                value.add(map(property));
            }
        }
    }

    /**
     * Map.
     * @param value the value
     * @return the map of DTO
     */
    default List<EntityPropertyDto> map(final PropertiesDecorator value) {
        final List<EntityPropertyDto> result = new ArrayList<>();

        if (value != null) {
            for (final EntityProperty property : value) {
                result.add(map(property));
            }
        }

        return result;
    }

    /**
     * To entity.<br/>
     * @param dto the DTO
     * @return the entity
     */
    @Mappings(value = { @Mapping(target = "archivingDate", ignore = true) })
    EntityProperty map(EntityPropertyDto dto);

    /**
     * Map.
     * @param value the map of DTO
     * @return the property map decorator
     */
    PropertiesDecorator map(Set<EntityPropertyDto> value);
}
