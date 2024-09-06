package com.porapipat.porapipat_api.dto.register.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotNull(message = "username must not be blank")
    private String username;

    @NotNull(message = "password must not be blank")
    private String password;

    @NotNull(message = "email must not be blank")
    private String email;
}
