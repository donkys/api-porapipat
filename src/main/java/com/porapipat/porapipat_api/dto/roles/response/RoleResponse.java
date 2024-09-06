package com.porapipat.porapipat_api.dto.roles.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleResponse {
    private Integer id;
    private String name;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}