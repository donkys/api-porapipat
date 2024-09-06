package com.porapipat.porapipat_api.controller;

import com.porapipat.porapipat_api.dto.errorhandle.ControllerErrorResponse;
import com.porapipat.porapipat_api.dto.roles.request.CreateRoleRequest;
import com.porapipat.porapipat_api.dto.roles.request.UpdateRoleRequest;
import com.porapipat.porapipat_api.dto.roles.response.RoleResponse;
import com.porapipat.porapipat_api.service.roles.RoleService;
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
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    private final PatternLogControllerService patternLogControllerService;

    public RoleController(RoleService roleService, PatternLogControllerService patternLogControllerService) {
        this.roleService = roleService;
        this.patternLogControllerService = patternLogControllerService;
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'role_api', 'READ')")
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles(HttpServletRequest httpRequest,
                                                          Authentication auth) {
        patternLogControllerService.logInfo(httpRequest, auth, true);
        List<RoleResponse> roles = roleService.getAllRoles();
        patternLogControllerService.logInfo(httpRequest, auth, false);
        return ResponseEntity.ok(roles);
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'role_api', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable @NotNull Integer id,
                                         HttpServletRequest httpRequest,
                                         Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            RoleResponse response = roleService.getRoleById(id);
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

    @PreAuthorize("@securityService.hasAccess(#auth, 'role_api', 'WRITE')")
    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody CreateRoleRequest request,
                                        HttpServletRequest httpRequest,
                                        Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            RoleResponse createdRole = roleService.createRole(request, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(createdRole);
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

    @PreAuthorize("@securityService.hasAccess(#auth, 'role_api', 'WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable @NotNull Integer id,
                                        @Valid @RequestBody UpdateRoleRequest request,
                                        HttpServletRequest httpRequest,
                                        Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            RoleResponse updatedRole = roleService.updateRole(request, id, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(updatedRole);
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

    @PreAuthorize("@securityService.hasAccess(#auth, 'role_api', 'WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable @NotNull Integer id,
                                        HttpServletRequest httpRequest,
                                        Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            String result = roleService.deleteRole(id);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred or violates foreign key constraint problem"));
        }
    }
}
