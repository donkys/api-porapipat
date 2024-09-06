package com.porapipat.porapipat_api.dto.userroles.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRoleDetailResponse {
    private Integer userId;
    private String username;
    private Integer roleId;
    private String roleName;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}
