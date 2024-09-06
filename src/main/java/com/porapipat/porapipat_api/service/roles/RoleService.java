package com.porapipat.porapipat_api.service.roles;

import com.porapipat.porapipat_api.dto.roles.request.CreateRoleRequest;
import com.porapipat.porapipat_api.dto.roles.request.UpdateRoleRequest;
import com.porapipat.porapipat_api.dto.roles.response.RoleResponse;
import com.porapipat.porapipat_api.entity.RolesEntity;
import com.porapipat.porapipat_api.repository.RolesInterfaceRepository;
import com.porapipat.porapipat_api.service.util.DateUtilService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RoleService {

    private final RolesInterfaceRepository rolesInterfaceRepository;

    private final DateUtilService dateUtilService;

    public RoleService(RolesInterfaceRepository rolesInterfaceRepository) {
        this.rolesInterfaceRepository = rolesInterfaceRepository;
        dateUtilService = new DateUtilService();
    }


    public RoleResponse createRole(CreateRoleRequest request, String name) {
        log.info("Creating role with name: {}", request.getName());
        validateCreateRole(request);
        RolesEntity roleEntity = new RolesEntity();
        roleEntity.setName(request.getName());
        roleEntity.setCreatedBy(name);
        roleEntity.setUpdatedBy(name);
        RolesEntity savedRole = rolesInterfaceRepository.saveAndFlush(roleEntity);
        log.info("Created role: {}", savedRole.getName());
        return wrapperRoleResponseDTO(savedRole);
    }

    private void validateCreateRole(CreateRoleRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Role name is required.");
        }
        if (rolesInterfaceRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Role with name " + request.getName() + " already exists.");
        }
    }

    public List<RoleResponse> getAllRoles() {
        log.info("Fetching all roles");
        List<RolesEntity> roles = rolesInterfaceRepository.findAll();
        log.info("Fetched {} roles", roles.size());
        return roles.stream().map(this::wrapperRoleResponseDTO).collect(Collectors.toList());
    }

    public RoleResponse getRoleById(Integer id) {
        log.info("Fetching role by ID: {}", id);
        RolesEntity role = rolesInterfaceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new IllegalArgumentException("Role not found with ID: " + id);
                });
        return wrapperRoleResponseDTO(role);
    }

    private RoleResponse wrapperRoleResponseDTO(RolesEntity entity) {
        RoleResponse dto = new RoleResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUpdatedAt(dateUtilService.setLocalDateTimeFormat(entity.getUpdatedAt().toLocalDateTime()));
        dto.setCreatedAt(dateUtilService.setLocalDateTimeFormat(entity.getCreatedAt().toLocalDateTime()));
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setCreatedBy(entity.getCreatedBy());
        return dto;
    }

    public RoleResponse updateRole(UpdateRoleRequest request, int id, String updatedBy) {
        log.info("Updating role with ID: {}, By: {}", id, updatedBy);
        RolesEntity role = rolesInterfaceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new IllegalArgumentException("Role not found with ID: " + id);
                });
        role.setName(request.getName());
        role.setUpdatedBy(updatedBy);
        RolesEntity updatedRole = rolesInterfaceRepository.saveAndFlush(role);
        log.info("Updated role: {}", updatedRole.getName());
        return wrapperRoleResponseDTO(updatedRole);
    }

    @Transactional
    public String deleteRole(Integer id) {
        log.info("Attempting to delete role with ID: {}", id);
        if (!rolesInterfaceRepository.existsById(id)) {
            log.error("Role not found with ID: {}", id);
            throw new IllegalArgumentException("Role not found with ID: " + id);
        }

        try {
            rolesInterfaceRepository.deleteById(id);
            log.info("Deleted role with ID: {}", id);
            return "Deleted role with ID: " + id;
        } catch (DataAccessException e) {
            log.error("Cannot delete role with ID: {}. It is still in use.", id);
            throw new IllegalArgumentException("Cannot delete role with ID: " + id + ". It is still in use.");
        }
    }
}
