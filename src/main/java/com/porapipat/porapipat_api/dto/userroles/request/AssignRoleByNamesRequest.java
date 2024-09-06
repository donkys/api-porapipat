package com.porapipat.porapipat_api.dto.userroles.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleByNamesRequest {
    @NotNull(message = "username must not be blank")
    private String username;

    @NotNull(message = "roleName must not be blank")
    private String roleName;
}