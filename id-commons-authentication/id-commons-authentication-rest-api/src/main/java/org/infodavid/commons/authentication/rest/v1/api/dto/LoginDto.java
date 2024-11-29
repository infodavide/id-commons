package org.infodavid.commons.authentication.rest.v1.api.dto;

import org.infodavid.commons.rest.api.annotation.DataTransferObject;

/**
 * The Record LoginDto.
 * @param name     the name
 * @param password the password
 */
@DataTransferObject
public record LoginDto(String name, String password) {
}
