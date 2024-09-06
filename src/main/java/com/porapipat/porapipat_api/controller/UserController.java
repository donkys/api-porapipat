package com.porapipat.porapipat_api.controller;

import com.porapipat.porapipat_api.dto.errorhandle.ControllerErrorResponse;
import com.porapipat.porapipat_api.dto.users.request.UpdateUserRequest;
import com.porapipat.porapipat_api.dto.users.request.UserSearchCriteria;
import com.porapipat.porapipat_api.dto.users.response.GetAllUsersResponse;
import com.porapipat.porapipat_api.dto.users.response.UserResponse;
import com.porapipat.porapipat_api.dto.users.response.UserSearchResponse;
import com.porapipat.porapipat_api.service.users.UsersService;
import com.porapipat.porapipat_api.service.util.PatternLogControllerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UsersService usersService;

    private final PatternLogControllerService patternLogControllerService;

    public UserController(UsersService usersService, PatternLogControllerService patternLogControllerService) {
        this.usersService = usersService;
        this.patternLogControllerService = patternLogControllerService;
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'user_api', 'READ')")
    @GetMapping
    public ResponseEntity<GetAllUsersResponse> getAllUsers(HttpServletRequest httpRequest,
                                                          Authentication auth) {
        patternLogControllerService.logInfo(httpRequest, auth, true);
        GetAllUsersResponse users = usersService.getAllUsers();
        patternLogControllerService.logInfo(httpRequest, auth, false);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'user_api', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable @NotNull Integer id,
                                         HttpServletRequest httpRequest,
                                         Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            UserResponse response = usersService.getUserById(id);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'user_api', 'WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmail(@PathVariable @NotNull Integer id,
                                         @Valid @RequestBody UpdateUserRequest request,
                                         HttpServletRequest httpRequest,
                                         Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            UserResponse updatedUser = usersService.updateEmail(request, id, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'user_api', 'WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable @NotNull Integer id,
                                        HttpServletRequest httpRequest,
                                        Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            String result = usersService.deleteUser(id, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'user_api', 'READ')")
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable @NotNull String username,
                                               HttpServletRequest httpRequest,
                                               Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            Optional<UserResponse> user = usersService.findByUsername(username);
            patternLogControllerService.logInfo(httpRequest, auth, false);

            return ResponseEntity.ok().body(user);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'user_api', 'READ')")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest httpRequest,
                                            Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            UserResponse currentUser = usersService.findByUsername(auth.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(currentUser);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'user_api', 'READ')")
    @PostMapping("/search")
    public ResponseEntity<?> searchUsers(@Valid @RequestBody UserSearchCriteria criteria,
                                         HttpServletRequest httpRequest,
                                         Authentication auth) {
        try {
            patternLogControllerService.logInfoWithRequest(httpRequest, auth, criteria, true);
            UserSearchResponse response = usersService.searchUsers(criteria);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }
}