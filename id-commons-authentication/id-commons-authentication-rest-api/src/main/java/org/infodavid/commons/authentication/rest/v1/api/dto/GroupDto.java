package org.infodavid.commons.authentication.rest.v1.api.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.infodavid.commons.rest.api.annotation.DataTransferObject;
import org.infodavid.commons.rest.v1.api.dto.AbstractDto;
import org.infodavid.commons.rest.v1.api.dto.EntityPropertyDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class GroupDto.
 */
@DataTransferObject(model = "org.infodavid.commons.authentication.model.Group")
@Getter
@Setter
public class GroupDto extends AbstractDto<Long> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6654742063299216699L;

    /** The name. */
    @NotBlank
    @Size(min = 0, max = 48)
    private String name;

    /** The description. */
    @Size(min = 0, max = 512)
    private String description;

    /** The properties. */
    private List<EntityPropertyDto> properties = new ArrayList<>();

    /** The role. */
    private Set<String> roles = new HashSet<>();
}
