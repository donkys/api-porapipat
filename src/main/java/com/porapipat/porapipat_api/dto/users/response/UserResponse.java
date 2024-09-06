package com.porapipat.porapipat_api.dto.users.response;

import com.porapipat.porapipat_api.entity.enumeration.provider.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
}
