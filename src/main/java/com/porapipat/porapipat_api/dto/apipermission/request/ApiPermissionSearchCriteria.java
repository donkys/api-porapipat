package com.porapipat.porapipat_api.dto.apipermission.request;

import lombok.Data;

@Data
public class ApiPermissionSearchCriteria {
    private Integer userId;
    private String apiName;
    private String permission;
    private String sortFieldName;
    private Boolean isDescending;
}