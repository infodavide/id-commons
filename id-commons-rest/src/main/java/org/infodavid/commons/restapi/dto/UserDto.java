package org.infodavid.commons.restapi.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Generated;

import org.infodavid.commons.model.User;
import org.infodavid.commons.restapi.annotation.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class UserDto.</br>
 * Password of the user is always hashed using MD5 in the DTO and database.
 */
@DataTransferObject(model = User.class)
@Getter
@Setter
public class UserDto extends AbstractDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2743878100657229100L;

    /** The connected. */
    @Generated("Set by the service")
    private boolean connected;

    /** The connections count. */
    @Generated("Set by the service")
    private int connectionsCount;

    /** The display name. */
    private String displayName;

    /** The email. */
    private String email;

    /** The expiration date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date expirationDate;

    /** The last connection date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Generated("Set by the service")
    private Date lastConnectionDate;

    /** The last IP address. */
    @Generated("Set by the service")
    private String lastIp;

    /** The locked. */
    private boolean locked;

    /** The name. */
    private String name;

    /** The password. */
    private String password;

    /** The properties. */
    private List<PropertyDto> properties;

    /** The role. */
    private Set<String> roles;

    /*
     * (non-Javadoc)
     * @see org.infodavid.web.dto.AbstractDto#init()
     */
    @Override
    public void init() {
        super.init();
        connectionsCount = 0;
        expirationDate = null;
        lastConnectionDate = null;
        lastIp = null;
        locked = false;
        password = null;
        roles = null;
        name = null;
        displayName = null;
        email = null;
    }
}
