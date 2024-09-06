package com.porapipat.porapipat_api.controller;

import com.porapipat.porapipat_api.dto.userroles.request.AssignRoleByNamesRequest;
import com.porapipat.porapipat_api.dto.userroles.request.AssignRoleRequest;
import com.porapipat.porapipat_api.dto.userroles.response.ControllerErrorResponse;
import com.porapipat.porapipat_api.dto.userroles.response.UserRoleDetailResponse;
import com.porapipat.porapipat_api.dto.userroles.response.UserRoleResponse;
import com.porapipat.porapipat_api.service.userroles.UserRoleService;
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

@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

    private final UserRoleService userRoleService;
    
    private final PatternLogControllerService patternLogControllerService;

    public UserRoleController(UserRoleService userRoleService, PatternLogControllerService patternLogControllerService) {
        this.userRoleService = userRoleService;
        this.patternLogControllerService = patternLogControllerService;
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'userrole_api', 'READ')")
    @GetMapping
    public ResponseEntity<?> getAllUserRoles(
            HttpServletRequest httpRequest,
            Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            List<UserRoleDetailResponse> userRoles = userRoleService.getAllUserRoles();
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(userRoles);
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred while fetching user roles"));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'userrole_api', 'WRITE')")
    @PostMapping("/assign")
    public ResponseEntity<?> assignRoleToUser(
            @Valid @RequestBody AssignRoleRequest request,
            HttpServletRequest httpRequest,
            Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            UserRoleResponse response = userRoleService.assignRoleToUser(request, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'userrole_api', 'WRITE')")
    @PostMapping("/assign-by-names")
    public ResponseEntity<?> assignRoleToUserByNames(
            @Valid @RequestBody AssignRoleByNamesRequest request,
            HttpServletRequest httpRequest,
            Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            UserRoleResponse response = userRoleService.assignRoleToUserByNames(request, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'userrole_api', 'READ')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRoles(
            @PathVariable @NotNull Integer userId,
            HttpServletRequest httpRequest,
            Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            List<UserRoleResponse> roles = userRoleService.getUserRoles(userId);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(roles);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'userrole_api', 'WRITE')")
    @DeleteMapping("/remove-by-id/{userId}/role/{roleId}")
    public ResponseEntity<?> removeRoleFromUser(
            @PathVariable @NotNull Integer userId,
            @PathVariable @NotNull Integer roleId,
            HttpServletRequest httpRequest,
            Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            String result = userRoleService.removeRoleFromUser(userId, roleId, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred"));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'userrole_api', 'WRITE')")
    @DeleteMapping("/remove-by-name/{username}/role/{roleName}")
    public ResponseEntity<?> removeRoleFromUserByNames(
            @PathVariable @NotNull String username,
            @PathVariable @NotNull String roleName,
            HttpServletRequest httpRequest,
            Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            String result = userRoleService.removeRoleFromUserByNames(username, roleName, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred"));
        }
    }
}