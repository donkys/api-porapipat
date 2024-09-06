package com.porapipat.porapipat_api.dto.apipermission.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApiPermissionRequest {
    private Integer userId;
    private String apiName;
    private String permission;
}