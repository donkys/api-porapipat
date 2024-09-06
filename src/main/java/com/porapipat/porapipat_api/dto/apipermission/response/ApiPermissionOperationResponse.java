package com.porapipat.porapipat_api.dto.apipermission.response;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ApiPermissionOperationResponse {
    private String message;
    private boolean success;
}