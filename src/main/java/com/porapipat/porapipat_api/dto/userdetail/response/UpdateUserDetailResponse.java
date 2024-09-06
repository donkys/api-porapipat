package com.porapipat.porapipat_api.dto.userdetail.response;

import lombok.Data;

@Data
public class UpdateUserDetailResponse {
    private Integer userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String profilePictureUrl;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}