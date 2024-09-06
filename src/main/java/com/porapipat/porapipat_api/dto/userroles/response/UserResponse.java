package com.porapipat.porapipat_api.dto.userroles.response;

import com.porapipat.porapipat_api.entity.enumeration.provider.ProviderType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private boolean enabled;
    private ProviderType provider;
    private Set<String> roles;
    private String createdAt;
    private String updatedAt;
}