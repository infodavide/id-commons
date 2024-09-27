package org.infodavid.commons.restapi.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.infodavid.commons.model.AbstractProperty;
import org.infodavid.commons.model.Property;
import org.infodavid.commons.model.decorator.PropertiesDecorator;
import org.infodavid.commons.restapi.dto.PropertyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * The Interface PropertyMapper.
 */
@Mapper(uses = { CommonMapper.class })
public interface PropertyMapper { // NOSONAR See Mapstruct documentation

    /** The instance. */
    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    /**
     * To DTO.
     * @param entity the entity
     * @return the DTO
     */
    @Mappings(value = { @Mapping(target = "editable", ignore = true) })
    default PropertyDto map(final AbstractProperty<?> entity) {
        if (entity == null) {
            return null;
        }

        final PropertyDto propertyDto = new PropertyDto();
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
    default void map(final List<PropertyDto> dto, @MappingTarget final PropertiesDecorator value) {
        if (value != null) {
            for (final PropertyDto property : dto) {
                value.add(map(property));
            }
        }
    }

    /**
     * Map.
     * @param value the value
     * @return the map of DTO
     */
    default List<PropertyDto> map(final PropertiesDecorator value) {
        final List<PropertyDto> result = new ArrayList<>();

        if (value != null) {
            for (final Property property : value) {
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
    Property map(PropertyDto dto);

    /**
     * Map.
     * @param value the map of DTO
     * @return the property map decorator
     */
    PropertiesDecorator map(Set<PropertyDto> value);
}
