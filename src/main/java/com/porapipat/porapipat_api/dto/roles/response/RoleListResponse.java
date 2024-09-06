package com.porapipat.porapipat_api.dto.roles.response;

import lombok.Data;
import java.util.List;

@Data
public class RoleListResponse {
    private List<RoleResponse> roles;
    private int totalCount;
}