package org.infodavid.commons.restapi.dto;

import org.infodavid.commons.restapi.annotation.DataTransferObject;

/**
 * The Record LoginDto.
 * @param name     the name
 * @param password the password
 */
@DataTransferObject
public record LoginDto(String name, String password) {
}
