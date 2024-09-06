package com.porapipat.porapipat_api.service.users;

import com.porapipat.porapipat_api.dto.userroles.request.AssignRoleByNamesRequest;
import com.porapipat.porapipat_api.dto.userroles.response.UserRoleResponse;
import com.porapipat.porapipat_api.dto.users.request.CreateUserRequest;
import com.porapipat.porapipat_api.dto.users.request.UpdateUserRequest;
import com.porapipat.porapipat_api.dto.users.request.UserSearchCriteria;
import com.porapipat.porapipat_api.dto.users.response.CreateUserResponse;
import com.porapipat.porapipat_api.dto.users.response.GetAllUsersResponse;
import com.porapipat.porapipat_api.dto.users.response.UserResponse;
import com.porapipat.porapipat_api.dto.users.response.UserSearchResponse;
import com.porapipat.porapipat_api.entity.RolesEntity;
import com.porapipat.porapipat_api.entity.UsersEntity;
import com.porapipat.porapipat_api.entity.enumeration.provider.ProviderType;
import com.porapipat.porapipat_api.repository.*;
import com.porapipat.porapipat_api.service.userroles.UserRoleService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
public class UsersService {

    private final UsersInterfaceRepository usersRepository;

    private final UserRolesInterfaceRepository userRolesRepository;

    private final RolesInterfaceRepository rolesRepository;

    private final UserDetailsInterfaceRepository userDetailsRepository;

    private final ApiPermissionsInterfaceRepository apiPermissionsRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRoleService userRoleService;

    public UsersService(UsersInterfaceRepository usersRepository,
                        UserRolesInterfaceRepository userRolesRepository,
                        RolesInterfaceRepository rolesRepository,
                        UserDetailsInterfaceRepository userDetailsRepository,
                        ApiPermissionsInterfaceRepository apiPermissionsRepository,
                        PasswordEncoder passwordEncoder,
                        UserRoleService userRoleService) {
        this.usersRepository = usersRepository;
        this.userRolesRepository = userRolesRepository;
        this.rolesRepository = rolesRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.apiPermissionsRepository = apiPermissionsRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleService = userRoleService;
    }

    //Create User -------------------------------------------------------------
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        log.info("Create user with request {}", request.getUsername());
        validateCreateUser(request);
        UsersEntity userEntity = wrapperCreateUser(request);
        UsersEntity savedUser = usersRepository.saveAndFlush(userEntity);
        log.info("User entity to be saved: {}", userEntity.getUsername());

        UserRoleResponse addRoleCreateUser = assignRoleToCreateUser(request);
        log.info("Add role {} to user {} with request {} : Success",
                addRoleCreateUser.getRoleId(),
                addRoleCreateUser.getUserId(),
                addRoleCreateUser.toString());

        log.info("Create user with request {} : Success", request.getUsername());
        return wrapperCreateUserResponseDTO(savedUser);
    }

    public UserRoleResponse assignRoleToCreateUser(CreateUserRequest request){
        AssignRoleByNamesRequest assignRoleByNamesRequest = new AssignRoleByNamesRequest();
        assignRoleByNamesRequest.setUsername(request.getUsername());
        assignRoleByNamesRequest.setRoleName("ROLE_USER");
        return userRoleService.assignRoleToUserByNames(assignRoleByNamesRequest, request.getUsername());
    }

    private CreateUserResponse wrapperCreateUserResponseDTO(UsersEntity entity) {
        CreateUserResponse dto = new CreateUserResponse();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    private UsersEntity wrapperCreateUser(CreateUserRequest request) {
        UsersEntity entity = new UsersEntity();
        entity.setUsername(request.getUsername());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setEmail(request.getEmail());
        entity.setProvider(ProviderType.LOCAL);
        entity.setEnabled(true);
        return entity;
    }

    private void validateCreateUser(CreateUserRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Create User missing Username.");
        }

        if (usersRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Create User duplicate (already taken) Username: " + request.getUsername() + ".");
        }

        if (!Pattern.matches("^[A-Za-z0-9-_]{1,24}$", request.getUsername())) {
            throw new IllegalArgumentException("Create User Invalid Username format.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Create User missing Email.");
        }

        if (!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", request.getEmail())) {
            throw new IllegalArgumentException("Create User Invalid Email format.");
        }
    }

    //Get All Users -------------------------------------------------------------
    public GetAllUsersResponse getAllUsers() {
        GetAllUsersResponse getAllUsersResponse = new GetAllUsersResponse();
        log.info("Fetching all users");
        List<UsersEntity> users = usersRepository.findAll();
        getAllUsersResponse.setUserResponseList(
                users.stream().map(this::wrapperGetAllUsers).toList());
        getAllUsersResponse.setTotal(users.size());
        log.info("Fetched {} users", users.size());
        return getAllUsersResponse;
    }

    private UserResponse wrapperGetAllUsers(UsersEntity entity) {
        UserResponse dto = new UserResponse();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    //Get Users by ID -------------------------------------------------------------
    public UserResponse getUserById(Integer id) {
        log.info("Fetching user by ID: {}", id);
        UsersEntity user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new IllegalArgumentException("User not found with ID: " + id);
                });
        UserResponse dto = wrapperUserResponseDTO(user);
        log.info("Fetched user: {}", dto);
        return dto;
    }

    //find by username -------------------------------------------------------------
    public Optional<UserResponse> findByUsername(String username) {
        log.info("Finding user by username: {}", username);

        Optional<UsersEntity> userEntity = usersRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            UserResponse userResponse = wrapperUserResponseDTO(userEntity.get());
            log.info("User found: {}", userResponse);
            return Optional.of(userResponse);
        } else {
            log.error("User not found with username: {}", username);
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }

    private UserResponse wrapperUserResponseDTO(UsersEntity entity) {
        UserResponse dto = new UserResponse();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    //updateEmail by id -------------------------------------------------------------
    public UserResponse updateEmail(UpdateUserRequest request, int id, String updatedBy) {
        log.info("Updating user with request: {}, By: {}", request.getEmail(), updatedBy);
        UsersEntity user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new IllegalArgumentException("User not found with ID: " + id);
                });

        if (!user.getUsername().equals(updatedBy) && isAdmin(updatedBy)) {
            log.error("User {} is not authorized to update email user {}", updatedBy, user.getUsername());
            throw new IllegalArgumentException("You are not authorized to update email this user");
        }

        if (user.getEmail().equals(request.getEmail())) {
            log.error("User {} previous email conflicts with the new {}", updatedBy, user.getEmail());
            throw new IllegalArgumentException("Your previous email conflicts with the new one");
        }

        user.setEmail(request.getEmail());
        UsersEntity updatedUser = usersRepository.saveAndFlush(user);
        log.info("Updated email user: {}", updatedUser.getUsername());
        return wrapperUserResponseDTO(updatedUser);
    }

    //delete user by id -------------------------------------------------------------
    public String deleteUser(Integer id, String deletedBy) {
        log.info("Deleting user with ID: {}, By: {}", id, deletedBy);
        UsersEntity user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new IllegalArgumentException("User not found with ID: " + id);
                });

        if (!user.getUsername().equals(deletedBy) && isAdmin(deletedBy)) {
            log.error("User {} is not authorized to delete user {}", deletedBy, user.getUsername());
            throw new IllegalArgumentException("You are not authorized to delete this user");
        }

        deleteRelatedData(id);

        usersRepository.deleteById(id);
        log.info("Deleted user with ID: {}", id);
        return "Deleted user with ID: " + id;
    }


    private void deleteRelatedData(Integer userId) {
        // Delete user roles
        userRolesRepository.deleteByUserId(userId);
        log.info("Deleted user roles for user ID: {}", userId);

        // Delete user details
        userDetailsRepository.deleteByUserId(userId);
        log.info("Deleted user details for user ID: {}", userId);

        // Delete API permissions
        apiPermissionsRepository.deleteByUserId(userId);
        log.info("Deleted API permissions for user ID: {}", userId);
    }

    //search by criteria
    public UserSearchResponse searchUsers(UserSearchCriteria criteria) {
        log.info("Searching users with criteria: {}", criteria);

        validateSearchCriteria(criteria);

        Pageable pageable = createPageable(criteria);
        Page<UsersEntity> searchResult = usersRepository.searchUsers(
                criteria.getUsername(),
                criteria.getEmail(),
                pageable
        );

        List<UserResponse> userResponses = searchResult.getContent().stream()
                .map(this::wrapperUserResponseDTO)
                .collect(Collectors.toList());

        UserSearchResponse response = createSearchResponse(criteria, searchResult, userResponses);

        log.info("Found {} users", searchResult.getTotalElements());
        return response;
    }

    private void validateSearchCriteria(UserSearchCriteria criteria) {
        if (criteria.getPageNumber() == null || criteria.getPageNumber() < 0) {
            throw new IllegalArgumentException("Page number must be a non-negative integer");
        }
        if (criteria.getPageSize() == null || criteria.getPageSize() <= 0) {
            throw new IllegalArgumentException("Page size must be a positive integer");
        }
    }

    private Pageable createPageable(UserSearchCriteria criteria) {
        Sort sort = createSort(criteria);
        return PageRequest.of(
                criteria.getPageNumber(),
                criteria.getPageSize(),
                sort
        );
    }

    private Sort createSort(UserSearchCriteria criteria) {
        if (criteria.getSortFieldName() == null) {
            return Sort.unsorted();
        }
        return criteria.getIsDescending() != null && criteria.getIsDescending()
                ? Sort.by(criteria.getSortFieldName()).descending()
                : Sort.by(criteria.getSortFieldName()).ascending();
    }

    private UserSearchResponse createSearchResponse(UserSearchCriteria criteria,
                                                    Page<UsersEntity> searchResult,
                                                    List<UserResponse> userResponses) {
        UserSearchResponse response = new UserSearchResponse();
        response.setCurrentPage(criteria.getPageNumber());
        response.setPageSize(criteria.getPageSize());
        response.setTotalPage(searchResult.getTotalPages());
        response.setTotalRecord((int) searchResult.getTotalElements());
        response.setData(userResponses);
        return response;
    }

    private boolean isAdmin(String username) {
        UsersEntity user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        return userRolesRepository.findByUserId(user.getId())
                .stream()
                .noneMatch(userRole -> {
                    RolesEntity role = rolesRepository.findById(userRole.getRoleId())
                            .orElseThrow(() -> new IllegalArgumentException("Role not found"));
                    return "ROLE_ADMIN".equals(role.getName());
                });
    }

}