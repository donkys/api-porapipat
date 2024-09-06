package com.porapipat.porapipat_api.dto.userdetail.response;

import lombok.Data;

@Data
public class CreateUserDetailResponse {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String profilePictureUrl;
    private String createdBy;
    private String updatedBy;
}