package com.porapipat.porapipat_api.dto.apipermission.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApiPermissionRequest {
    @NotNull(message = "User ID must not be null")
    private Integer userId;

    @NotBlank(message = "API name must not be blank")
    private String apiName;

    @NotBlank(message = "Permission must not be blank")
    private String permission;
}