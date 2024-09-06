package com.porapipat.porapipat_api.dto.apipermission.response;

import lombok.Data;

@Data
public class CreateApiPermissionResponse {
    private Integer id;
    private Integer userId;
    private String apiName;
    private String permission;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}
