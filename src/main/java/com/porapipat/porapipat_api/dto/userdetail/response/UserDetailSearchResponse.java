package com.porapipat.porapipat_api.dto.userdetail.response;

import com.porapipat.porapipat_api.dto.apipermission.response.ApiPermissionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailSearchResponse {
    private Integer currentPage;
    private Integer totalPage;
    private Integer totalRecord;
    private Integer pageSize;
    private List<UpdateUserDetailResponse> data;
}
