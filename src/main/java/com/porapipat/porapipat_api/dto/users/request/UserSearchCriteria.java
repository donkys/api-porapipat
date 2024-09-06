package com.porapipat.porapipat_api.dto.users.request;

import lombok.Data;

@Data
public class UserSearchCriteria {
    private String username;
    private String email;
    private String sortFieldName;
    private Boolean isDescending;
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
}
