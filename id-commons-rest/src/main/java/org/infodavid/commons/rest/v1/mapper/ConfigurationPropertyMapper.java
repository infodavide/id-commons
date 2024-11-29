package org.infodavid.commons.rest.v1.mapper;

import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.rest.v1.api.dto.ConfigurationPropertyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * The Interface EntityPropertyMapper.
 */
@Mapper(uses = { CommonMapper.class })
public interface ConfigurationPropertyMapper { // NOSONAR See Mapstruct documentation

    /** The instance. */
    ConfigurationPropertyMapper INSTANCE = Mappers.getMapper(ConfigurationPropertyMapper.class);

    /**
     * To DTO.
     * @param entity the entity
     * @return the DTO
     */
    @Mappings(value = { @Mapping(target = "editable", ignore = true) })
    default ConfigurationPropertyDto map(final ConfigurationProperty entity) {
        if (entity == null) {
            return null;
        }

        final ConfigurationPropertyDto result = new ConfigurationPropertyDto();
        result.setDeletable(entity.isDeletable());
        result.setName(entity.getName());
        result.setLabel(entity.getLabel());
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
     * To entity.<br/>
     * @param dto the DTO
     * @return the entity
     */
    @Mappings(value = { @Mapping(target = "archivingDate", ignore = true) })
    ConfigurationProperty map(ConfigurationPropertyDto dto);
}
