package com.porapipat.porapipat_api.controller;

import com.porapipat.porapipat_api.dto.errorhandle.ControllerErrorResponse;
import com.porapipat.porapipat_api.dto.userdetail.request.UpdateProfilePictureRequest;
import com.porapipat.porapipat_api.dto.userdetail.request.UpdateUserDetailRequest;
import com.porapipat.porapipat_api.dto.userdetail.request.UserDetailRequest;
import com.porapipat.porapipat_api.dto.userdetail.request.UserDetailSearchCriteria;
import com.porapipat.porapipat_api.dto.userdetail.response.CreateUserDetailResponse;
import com.porapipat.porapipat_api.dto.userdetail.response.UpdateUserDetailResponse;
import com.porapipat.porapipat_api.dto.userdetail.response.UserDetailOperationResponse;
import com.porapipat.porapipat_api.dto.userdetail.response.UserDetailSearchResponse;
import com.porapipat.porapipat_api.service.userdetail.UserDetailService;
import com.porapipat.porapipat_api.service.util.PatternLogControllerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Log4j2
@RestController
@RequestMapping("/api/user-details")
public class UserDetailController {

    private final UserDetailService userDetailService;

    private final PatternLogControllerService patternLogControllerService;

    public UserDetailController(UserDetailService userDetailService, PatternLogControllerService patternLogControllerService) {
        this.userDetailService = userDetailService;
        this.patternLogControllerService = patternLogControllerService;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("@securityService.hasAccess(#auth, 'userdetail_api', 'READ')")
    public ResponseEntity<?> getUserDetail(@PathVariable Integer userId,
                                           HttpServletRequest httpRequest,
                                           Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            UpdateUserDetailResponse response = userDetailService.getUserDetail(userId);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.badRequest().body(new UserDetailOperationResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserDetailOperationResponse("An unexpected error occurred", false));
        }
    }

    @PostMapping
    @PreAuthorize("@securityService.hasAccess(#auth, 'userdetail_api', 'WRITE')")
    public ResponseEntity<?> createUserDetail(@Valid @RequestBody UserDetailRequest request,
                                              HttpServletRequest httpRequest,
                                              Authentication auth) {
        try {
            patternLogControllerService.logInfoWithRequest(httpRequest, auth, request, true);
            if (userDetailService.isAdminOrOwner(auth.getName(), request.getUserId())) {
                throw new IllegalArgumentException("You are not authorized to create this user detail");
            }
            CreateUserDetailResponse response = userDetailService.createUserDetail(request, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.badRequest().body(new UserDetailOperationResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserDetailOperationResponse("An unexpected error occurred", false));
        }
    }


    @PutMapping("/{userId}")
    @PreAuthorize("@securityService.hasAccess(#auth, 'userdetail_api', 'WRITE')")
    public ResponseEntity<?> updateUserDetail(@PathVariable Integer userId,
                                              @Valid @RequestBody UpdateUserDetailRequest request,
                                              HttpServletRequest httpRequest,
                                              Authentication auth) {
        try {
            patternLogControllerService.logInfoWithRequest(httpRequest, auth, request, true);
            if (userDetailService.isAdminOrOwner(auth.getName(), userId)) {
                throw new IllegalArgumentException("You are not authorized to update this user detail");
            }
            UpdateUserDetailResponse response = userDetailService.updateUserDetail(request, userId, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.badRequest().body(new UserDetailOperationResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserDetailOperationResponse("An unexpected error occurred", false));
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("@securityService.hasAccess(#auth, 'userdetail_api', 'WRITE')")
    public ResponseEntity<?> deleteUserDetail(@PathVariable Integer userId,
                                              HttpServletRequest httpRequest,
                                              Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            if (userDetailService.isAdminOrOwner(auth.getName(), userId)) {
                throw new IllegalArgumentException("You are not authorized to delete this user detail");
            }
            boolean deleted = userDetailService.deleteUserDetail(userId);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            if (deleted) {
                return ResponseEntity.ok(new UserDetailOperationResponse("User detail deleted successfully", true));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.badRequest().body(new UserDetailOperationResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserDetailOperationResponse("An unexpected error occurred", false));
        }
    }

    @GetMapping
    @PreAuthorize("@securityService.hasAccess(#auth, 'userdetail_api', 'READ')")
    public ResponseEntity<?> getUserDetails(HttpServletRequest httpRequest,
                                               Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            UpdateUserDetailResponse responses = userDetailService.getUserOwnDetail(auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(responses);
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserDetailOperationResponse("An unexpected error occurred", false));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("@securityService.hasAccess(#auth, 'userdetail_api', 'READ')")
    public ResponseEntity<?> searchUserDetails(@ModelAttribute UserDetailSearchCriteria criteria,
                                               HttpServletRequest httpRequest,
                                               Authentication auth) {
        try {
            patternLogControllerService.logInfoWithRequest(httpRequest, auth, criteria, true);
            UserDetailSearchResponse responses = userDetailService.searchUserDetails(criteria);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(responses);
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserDetailOperationResponse("An unexpected error occurred", false));
        }
    }


    @PreAuthorize("@securityService.hasAccess(#auth, 'user_api', 'UPDATE')")
    @PutMapping("/{userId}/profile-picture")
    public ResponseEntity<?> updateProfilePicture(@PathVariable int userId,
                                                  @RequestParam("file") MultipartFile file,
                                                  HttpServletRequest httpRequest,
                                                  Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            String updatedBy = auth.getName();
            boolean isUpdated = userDetailService.updateProfilePicture(userId, file.getBytes(), updatedBy);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            if (isUpdated) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update profile picture"));
            }
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            log.error("Error updating profile picture: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (IOException ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            log.error("Error reading file: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error reading file"));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            log.error("Unexpected error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }
}