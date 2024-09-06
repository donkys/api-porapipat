package com.porapipat.porapipat_api.dto.userroles.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleRequest {
    @NotNull(message = "userId must not be null")
    private Integer userId;

    @NotNull(message = "roleId must not be null")
    private Integer roleId;
}