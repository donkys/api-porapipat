package com.porapipat.porapipat_api.dto.userroles.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRoleResponse {
    private Integer userId;
    private Integer roleId;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}