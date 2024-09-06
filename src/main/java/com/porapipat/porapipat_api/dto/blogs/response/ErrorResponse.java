package com.porapipat.porapipat_api.dto.blogs.response;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
}
