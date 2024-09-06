package com.porapipat.porapipat_api.dto.auth.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthRequest {
    @NotNull(message = "username must not be blank")
    private String username;

    @NotNull(message = "password must not be blank")
    private String password;
}

