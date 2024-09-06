package com.porapipat.porapipat_api.dto.users.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NotNull(message = "Username must not be blank")
    private String username;

    @NotNull(message = "Password must not be blank")
    private String password;

    @NotNull(message = "Email must not be blank")
    private String email;
}
