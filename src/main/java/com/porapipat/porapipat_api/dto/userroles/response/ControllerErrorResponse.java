package com.porapipat.porapipat_api.dto.userroles.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ControllerErrorResponse {
    private int status;
    private String message;
}
