package com.porapipat.porapipat_api.service.userroles;

import com.porapipat.porapipat_api.dto.userroles.request.AssignRoleByNamesRequest;
import com.porapipat.porapipat_api.dto.userroles.request.AssignRoleRequest;
import com.porapipat.porapipat_api.dto.userroles.response.UserRoleDetailResponse;
import com.porapipat.porapipat_api.dto.userroles.response.UserRoleResponse;
import com.porapipat.porapipat_api.entity.RolesEntity;
import com.porapipat.porapipat_api.entity.UserRolesEntity;
import com.porapipat.porapipat_api.entity.UsersEntity;
import com.porapipat.porapipat_api.repository.RolesInterfaceRepository;
import com.porapipat.porapipat_api.repository.UserRolesInterfaceRepository;
import com.porapipat.porapipat_api.repository.UsersInterfaceRepository;
import com.porapipat.porapipat_api.service.util.DateUtilService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
public class UserRoleService {

    private final UserRolesInterfaceRepository userRolesRepository;

    private final DateUtilService dateUtilService;

    private final UsersInterfaceRepository usersRepository;

    private final RolesInterfaceRepository rolesRepository;

    public UserRoleService(UserRolesInterfaceRepository userRolesRepository, UsersInterfaceRepository usersRepository, RolesInterfaceRepository rolesRepository) {
        this.userRolesRepository = userRolesRepository;
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
        dateUtilService = new DateUtilService();
    }

    public List<UserRoleDetailResponse> getAllUserRoles() {
        log.info("Fetching all user roles");
        List<UserRolesEntity> allUserRoles = userRolesRepository.findAll();
        List<UserRoleDetailResponse> responses = new ArrayList<>();

        for (UserRolesEntity userRole : allUserRoles) {
            UsersEntity user = usersRepository.findById(userRole.getUserId()).orElse(null);
            RolesEntity role = rolesRepository.findById(userRole.getRoleId()).orElse(null);

            if (user != null && role != null) {
                UserRoleDetailResponse response = new UserRoleDetailResponse();
                response.setUserId(user.getId());
                response.setUsername(user.getUsername());
                response.setRoleId(role.getId());
                response.setRoleName(role.getName());
                response.setCreatedAt(dateUtilService.setLocalDateTimeFormat(userRole.getCreatedAt().toLocalDateTime()));
                response.setUpdatedAt(dateUtilService.setLocalDateTimeFormat(userRole.getUpdatedAt().toLocalDateTime()));
                response.setCreatedBy(userRole.getCreatedBy());
                response.setUpdatedBy(userRole.getUpdatedBy());
                responses.add(response);
            }
        }

        log.info("Fetched {} user roles", responses.size());
        return responses;
    }

    public UserRoleResponse assignRoleToUser(AssignRoleRequest request, String assignedBy) {
        log.info("Assigning role to user. User ID: {}, Role ID: {}, Assigned by: {}",
                request.getUserId(), request.getRoleId(), assignedBy);

        UsersEntity user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));

        RolesEntity role = rolesRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + request.getRoleId()));

        if (userRolesRepository.existsByUserIdAndRoleId(request.getUserId(), request.getRoleId())) {
            throw new IllegalArgumentException("User already has this role");
        }

        UserRolesEntity userRoleEntity = new UserRolesEntity();
        userRoleEntity.setUserId(request.getUserId());
        userRoleEntity.setRoleId(request.getRoleId());
        userRoleEntity.setCreatedBy(assignedBy);
        userRoleEntity.setUpdatedBy(assignedBy);
        userRoleEntity.setUsersByUserId(user);
        userRoleEntity.setRolesByRoleId(role);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        userRoleEntity.setCreatedAt(now);
        userRoleEntity.setUpdatedAt(now);

        UserRolesEntity savedUserRole = userRolesRepository.save(userRoleEntity);
        log.info("Role assigned successfully. User ID: {}, Role ID: {}",
                savedUserRole.getUserId(), savedUserRole.getRoleId());

        return wrapperUserRoleResponseDTO(savedUserRole);
    }

    public UserRoleResponse assignRoleToUserByNames(AssignRoleByNamesRequest request, String assignedBy) {
        log.info("Assigning role to user. Username: {}, Role name: {}, Assigned by: {}",
                request.getUsername(), request.getRoleName(), assignedBy);

        Optional<UsersEntity> user = usersRepository.findByUsername(request.getUsername());
        if (user.isEmpty()) {
            log.error("User not found with username: {}", request.getUsername());
            throw new IllegalArgumentException("User not found with username: " + request.getUsername());
        }

        Optional<RolesEntity> role = rolesRepository.findByName(request.getRoleName());
        if (role.isEmpty()) {
            log.error("Role not found with name: {}", request.getRoleName());
            throw new IllegalArgumentException("Role not found with name: " + request.getRoleName());
        }

        Integer id = user.get().getId();
        Integer role_id = role.get().getId();

        if (userRolesRepository.existsByUserIdAndRoleId(id, role_id)) {
            log.error("User already has this role. Username: {}, Role name: {}",
                    request.getUsername(), request.getRoleName());
            throw new IllegalArgumentException("User already has this role");
        }

        UserRolesEntity userRoleEntity = new UserRolesEntity();
        userRoleEntity.setUserId(id);
        userRoleEntity.setRoleId(role_id);
        userRoleEntity.setCreatedBy(assignedBy);
        userRoleEntity.setUpdatedBy(assignedBy);

        UserRolesEntity savedUserRole = userRolesRepository.saveAndFlush(userRoleEntity);
        log.info("Role assigned successfully. Username: {}, Role name: {}",
                request.getUsername(), request.getRoleName());

        return wrapperUserRoleResponseDTO(savedUserRole);
    }

    private void validateAssignRole(AssignRoleRequest request) {
        if (!usersRepository.existsById(request.getUserId())) {
            log.error("User not found with ID: {}", request.getUserId());
            throw new IllegalArgumentException("User not found with ID: " + request.getUserId());
        }

        if (!rolesRepository.existsById(request.getRoleId())) {
            log.error("Role not found with ID: {}", request.getRoleId());
            throw new IllegalArgumentException("Role not found with ID: " + request.getRoleId());
        }

        if (userRolesRepository.existsByUserIdAndRoleId(request.getUserId(), request.getRoleId())) {
            log.error("User already has this role. User ID: {}, Role ID: {}",
                    request.getUserId(), request.getRoleId());
            throw new IllegalArgumentException("User already has this role");
        }
    }

    private UserRolesEntity wrapperAssignRole(AssignRoleRequest request, String assignedBy) {
        UserRolesEntity entity = new UserRolesEntity();
        entity.setUserId(request.getUserId());
        entity.setRoleId(request.getRoleId());
        entity.setCreatedBy(assignedBy);
        entity.setUpdatedBy(assignedBy);
        return entity;
    }

    public List<UserRoleResponse> getUserRoles(Integer userId) {
        log.info("Fetching roles for user ID: {}", userId);

        if (!usersRepository.existsById(userId)) {
            log.error("User not found with ID: {}", userId);
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        List<UserRolesEntity> userRoles = userRolesRepository.findByUserId(userId);

        if (userRoles.isEmpty()) {
            log.warn("No roles found for user ID: {}", userId);
            throw new IllegalStateException("No roles found for user ID: " + userId);
        }

        log.info("Fetched {} roles for user ID: {}", userRoles.size(), userId);
        return userRoles.stream()
                .map(this::wrapperUserRoleResponseDTO)
                .collect(Collectors.toList());
    }

    public String removeRoleFromUser(Integer userId, Integer roleId, String removedBy) {
        log.info("Removing role from user. User ID: {}, Role ID: {}, Removed by: {}",
                userId, roleId, removedBy);

        UserRolesEntity userRole = userRolesRepository.findByUserIdAndRoleId(userId, roleId);
        if (userRole == null) {
            log.error("User role not found. User ID: {}, Role ID: {}", userId, roleId);
            throw new IllegalArgumentException("User role not found");
        }

        userRolesRepository.delete(userRole);
        log.info("Role removed successfully. User ID: {}, Role ID: {}", userId, roleId);
        return "Removed role ID: " + roleId + " from user ID: " + userId;
    }

    public String removeRoleFromUserByNames(String username, String roleName, String removedBy) {
        log.info("Removing role from user. Username: {}, Role name: {}, Removed by: {}",
                username, roleName, removedBy);

        Optional<UsersEntity> user = usersRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.error("User not found with username: {}", username);
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        Optional<RolesEntity> role = rolesRepository.findByName(roleName);
        if (role.isEmpty()) {
            log.error("Role not found with name: {}", roleName);
            throw new IllegalArgumentException("Role not found with name: " + roleName);
        }

        UserRolesEntity userRole = userRolesRepository.findByUserIdAndRoleId(user.get().getId(), role.get().getId());
        if (userRole == null) {
            log.error("User role not found. Username: {}, Role name: {}", username, roleName);
            throw new IllegalArgumentException("User role not found");
        }

        userRolesRepository.delete(userRole);
        log.info("Role removed successfully. Username: {}, Role name: {}", username, roleName);
        return "Removed role: " + roleName + " from user: " + username;
    }


    private UserRoleResponse wrapperUserRoleResponseDTO(UserRolesEntity entity) {
        UserRoleResponse dto = new UserRoleResponse();
        dto.setUserId(entity.getUserId());
        dto.setRoleId(entity.getRoleId());

        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(dateUtilService.setLocalDateTimeFormat(entity.getCreatedAt().toLocalDateTime()));
        }

        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(dateUtilService.setLocalDateTimeFormat(entity.getUpdatedAt().toLocalDateTime()));
        }

        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }
}