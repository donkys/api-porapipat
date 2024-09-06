package com.porapipat.porapipat_api.controller;

import com.porapipat.porapipat_api.dto.apipermission.request.ApiPermissionDeleteRequest;
import com.porapipat.porapipat_api.dto.apipermission.request.ApiPermissionRequest;
import com.porapipat.porapipat_api.dto.apipermission.request.ApiPermissionSearchCriteria;
import com.porapipat.porapipat_api.dto.apipermission.response.ApiPermissionOperationResponse;
import com.porapipat.porapipat_api.dto.apipermission.response.ApiPermissionResponse;
import com.porapipat.porapipat_api.dto.apipermission.response.ApiPermissionSearchResponse;
import com.porapipat.porapipat_api.dto.apipermission.response.CreateApiPermissionResponse;
import com.porapipat.porapipat_api.service.apipermission.ApiPermissionService;
import com.porapipat.porapipat_api.service.util.PatternLogControllerService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
public class ApiPermissionController {

    private final ApiPermissionService apiPermissionService;

    private final PatternLogControllerService patternLogControllerService;

    public ApiPermissionController(ApiPermissionService apiPermissionService, PatternLogControllerService patternLogControllerService) {
        this.apiPermissionService = apiPermissionService;
        this.patternLogControllerService = patternLogControllerService;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("@securityService.hasAccess(#auth, 'permission_api', 'READ')")
    public ResponseEntity<?> getApiPermissionsByUserId(@PathVariable @NotNull Integer userId,
                                                       HttpServletRequest httpRequest,
                                                       Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            List<ApiPermissionResponse> permissions = apiPermissionService.getApiPermissionsByUserId(userId);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(permissions);
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiPermissionOperationResponse("An unexpected error occurred", false));
        }
    }
    @PostMapping
    @PreAuthorize("@securityService.hasAccess(#auth, 'permission_api', 'WRITE')")
    public ResponseEntity<?> createApiPermission(@Valid @RequestBody ApiPermissionRequest request,
                                                 HttpServletRequest httpRequest,
                                                 Authentication auth) {
        try {
            patternLogControllerService.logInfoWithRequest(httpRequest, auth, request, true);
            CreateApiPermissionResponse response = apiPermissionService.createApiPermission(request, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.badRequest().body(new ApiPermissionOperationResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiPermissionOperationResponse("An unexpected error occurred", false));
        }
    }


    @PatchMapping("/{id}")
    @PreAuthorize("@securityService.hasAccess(#auth, 'api_permission', 'WRITE')")
    public ResponseEntity<?> partialUpdateApiPermission(@PathVariable Integer id,
                                                        @RequestBody Map<String, Object> updates,
                                                        HttpServletRequest httpRequest,
                                                        Authentication auth) {
        try {
            patternLogControllerService.logInfoWithRequest(httpRequest, auth, updates, true);
            ApiPermissionResponse response = apiPermissionService.updateApiPermission(id, updates);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.badRequest().body(new ApiPermissionOperationResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiPermissionOperationResponse("An unexpected error occurred", false));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("@securityService.hasAccess(#auth, 'permission_api', 'READ')")
    public ResponseEntity<?> searchApiPermissions(@ModelAttribute ApiPermissionSearchCriteria criteria,
                                                  HttpServletRequest httpRequest,
                                                  Authentication auth) {
        try {
            patternLogControllerService.logInfoWithRequest(httpRequest, auth, criteria, true);
            ApiPermissionSearchResponse response = apiPermissionService.searchApiPermissions(criteria);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiPermissionOperationResponse("An unexpected error occurred", false));
        }
    }

    @DeleteMapping
    @PreAuthorize("@securityService.hasAccess(#auth, 'api_permission', 'DELETE')")
    public ResponseEntity<?> deleteApiPermission(@Valid @RequestBody ApiPermissionDeleteRequest request,
                                                 HttpServletRequest httpRequest,
                                                 Authentication auth) {
        try {
            patternLogControllerService.logInfoWithRequest(httpRequest, auth, request, true);
            apiPermissionService.deleteApiPermission(request.getUserId(), request.getApiName(), request.getPermission());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(new ApiPermissionOperationResponse("API permission(s) deleted successfully", true));
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.badRequest().body(new ApiPermissionOperationResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiPermissionOperationResponse("An unexpected error occurred", false));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.hasAccess(#auth, 'api_permission', 'DELETE')")
    public ResponseEntity<?> deleteApiPermission(@PathVariable Integer id,
                                                 HttpServletRequest httpRequest,
                                                 Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            apiPermissionService.deleteApiPermissionById(id);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(new ApiPermissionOperationResponse("API permission(s) deleted successfully", true));
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.badRequest().body(new ApiPermissionOperationResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiPermissionOperationResponse("An unexpected error occurred", false));
        }
    }
}