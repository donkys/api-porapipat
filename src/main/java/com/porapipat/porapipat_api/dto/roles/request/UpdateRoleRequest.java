package com.porapipat.porapipat_api.dto.roles.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull(message = "name must not be blank")
    private String name;
}
