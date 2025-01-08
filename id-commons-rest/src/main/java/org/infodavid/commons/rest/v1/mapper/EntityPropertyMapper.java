package org.infodavid.commons.rest.v1.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.decorator.PropertiesDecorator;
import org.infodavid.commons.rest.v1.api.dto.EntityPropertyDto;
import org.infodavid.commons.util.ResourceBundleDecorator;
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

    /** The resource bundle. */
    ResourceBundleDecorator RESOURCE_BUNDLE = new ResourceBundleDecorator(ResourceBundle.getBundle("labels"));

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

        final EntityPropertyDto result = new EntityPropertyDto();
        result.setDeletable(entity.isDeletable());
        result.setName(entity.getName());
        result.setLabel(RESOURCE_BUNDLE.getString(entity.getName()));
        result.setReadOnly(entity.isReadOnly());
        result.setScope(entity.getScope());

        if (entity.getType() != null) { // NOSONAR Keep null check
            result.setType(entity.getType().name());
        }

        if (result.getLabel() == null) {
            result.setLabel(result.getName());
        }

        result.setTypeDefinition(entity.getTypeDefinition());
        result.setValue(entity.getValue());
        result.setDefaultValue(entity.getDefaultValue());
        result.setMinimum(entity.getMinimum());
        result.setMaximum(entity.getMaximum());

        return result;
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
    EntityProperty map(EntityPropertyDto dto);

    /**
     * Map.
     * @param value the map of DTO
     * @return the property map decorator
     */
    PropertiesDecorator map(Set<EntityPropertyDto> value);
}
