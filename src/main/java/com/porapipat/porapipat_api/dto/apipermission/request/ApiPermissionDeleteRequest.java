package com.porapipat.porapipat_api.dto.apipermission.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApiPermissionDeleteRequest {
    @NotNull(message = "User ID must not be null")
    private Integer userId;

    @NotNull(message = "API name must not be null")
    private String apiName;

    @NotNull(message = "Permission must not be null")
    private String permission;
}