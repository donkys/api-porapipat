package com.porapipat.porapipat_api.repository.searchinterface;

import java.time.LocalDateTime;

public interface SearchUserDetailInterface {
    Integer getUserId();
    String getUsername();
    String getEmail();
    String getFirstName();
    String getLastName();
    String getAddress();
    String getPhoneNumber();
    String getProfilePictureUrl();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getCreatedBy();
    String getUpdatedBy();
}