package com.porapipat.porapipat_api.dto.roles.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateRoleResponse {
    private Integer id;
    private String name;
}