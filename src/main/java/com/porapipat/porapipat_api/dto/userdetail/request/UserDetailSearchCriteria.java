package com.porapipat.porapipat_api.dto.userdetail.request;

import lombok.Data;

@Data
public class UserDetailSearchCriteria {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String sortFieldName;
    private Boolean isDescending;
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
}