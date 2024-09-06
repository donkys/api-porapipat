package com.porapipat.porapipat_api.dto.apipermission.response;

import lombok.Data;

import java.util.List;

@Data
public class ApiPermissionSearchResponse {
    private List<ApiPermissionResponse> data;
    private int totalRecord;
}