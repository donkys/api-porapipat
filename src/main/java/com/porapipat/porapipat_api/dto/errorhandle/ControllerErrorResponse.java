package com.porapipat.porapipat_api.dto.errorhandle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerErrorResponse {
    private Integer status;
    private String errorMessage;
}
