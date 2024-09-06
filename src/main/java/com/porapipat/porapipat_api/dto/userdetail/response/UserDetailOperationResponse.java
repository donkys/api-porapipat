package com.porapipat.porapipat_api.dto.userdetail.response;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UserDetailOperationResponse {
    private String message;
    private boolean success;
}