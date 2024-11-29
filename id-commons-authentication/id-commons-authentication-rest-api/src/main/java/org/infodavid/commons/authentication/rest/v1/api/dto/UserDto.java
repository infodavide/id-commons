package org.infodavid.commons.authentication.rest.v1.api.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.infodavid.commons.rest.api.annotation.DataTransferObject;
import org.infodavid.commons.rest.v1.api.dto.AbstractDto;
import org.infodavid.commons.rest.v1.api.dto.EntityPropertyDto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class UserDto.
 */
@DataTransferObject(model = "org.infodavid.commons.authentication.model.User")
@Getter
@Setter
public class UserDto extends AbstractDto<Long> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2743878100657229100L;

    /** The connected. */
    private boolean connected = false;

    /** The connections count. */
    @Min(0)
    private int connectionsCount;

    /** The deletable flag. */
    private boolean deletable = true;

    /** The display name. */
    @NotBlank
    @Size(min = 0, max = 96)
    private String displayName;

    /** The email. */
    @Size(min = 0, max = 255)
    private String email;

    /** The expiration date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date expirationDate;

    /** The last connection date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date lastConnectionDate;

    /** The last IP address. */
    @Size(min = 0, max = 255)
    private String lastIp;

    /** The locked. */
    private boolean locked;

    /** The name. */
    @NotBlank
    @Size(min = 0, max = 48)
    private String name;

    /** The password. */
    @Size(min = 0, max = 48)
    private String password;

    /** The properties. */
    private List<EntityPropertyDto> properties;

    /** The role. */
    private Set<String> roles;
}
