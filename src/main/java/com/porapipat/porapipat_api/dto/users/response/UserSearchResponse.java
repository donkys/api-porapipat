package com.porapipat.porapipat_api.dto.users.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponse {
    private Integer currentPage;
    private Integer totalPage;
    private Integer totalRecord;
    private Integer pageSize;
    private List<UserResponse> data;
}