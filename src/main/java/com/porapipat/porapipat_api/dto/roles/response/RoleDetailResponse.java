package com.porapipat.porapipat_api.dto.roles.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleDetailResponse {
    private Integer id;
    private String name;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
    private List<String> permissions;
}