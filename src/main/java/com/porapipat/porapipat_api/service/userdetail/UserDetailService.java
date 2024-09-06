package com.porapipat.porapipat_api.service.userdetail;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.porapipat.porapipat_api.dto.userdetail.request.UpdateUserDetailRequest;
import com.porapipat.porapipat_api.dto.userdetail.request.UserDetailRequest;
import com.porapipat.porapipat_api.dto.userdetail.request.UserDetailSearchCriteria;
import com.porapipat.porapipat_api.dto.userdetail.response.CreateUserDetailResponse;
import com.porapipat.porapipat_api.dto.userdetail.response.UpdateUserDetailResponse;
import com.porapipat.porapipat_api.dto.userdetail.response.UserDetailSearchResponse;
import com.porapipat.porapipat_api.entity.UserDetailsEntity;
import com.porapipat.porapipat_api.entity.UsersEntity;
import com.porapipat.porapipat_api.repository.UserDetailsInterfaceRepository;
import com.porapipat.porapipat_api.repository.UserRolesInterfaceRepository;
import com.porapipat.porapipat_api.repository.UsersInterfaceRepository;
import com.porapipat.porapipat_api.repository.searchinterface.SearchUserDetailInterface;
import com.porapipat.porapipat_api.service.aws.S3Service;
import com.porapipat.porapipat_api.service.util.DateUtilService;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
public class UserDetailService {

    private final UserDetailsInterfaceRepository userDetailsRepository;

    private final UsersInterfaceRepository usersRepository;

    private final UserRolesInterfaceRepository userRolesRepository;

    private final DateUtilService dateUtilService;

    private final S3Service s3Service;

    public UserDetailService(UserDetailsInterfaceRepository userDetailsRepository,
                             UsersInterfaceRepository usersRepository,
                             UserRolesInterfaceRepository userRolesRepository,
                             S3Service s3Service) {
        this.userDetailsRepository = userDetailsRepository;
        this.usersRepository = usersRepository;
        this.userRolesRepository = userRolesRepository;
        this.s3Service = s3Service;
        dateUtilService = new DateUtilService();
    }

    public CreateUserDetailResponse createUserDetail(UserDetailRequest request, String createdBy) {
        log.info("Creating user detail for user ID: {}", request.getUserId());
        validateCreateUserDetailRequest(request);
        UserDetailsEntity entity = new UserDetailsEntity();
        createUserDetailEntity(entity, request);
        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(createdBy);
        UserDetailsEntity savedEntity = userDetailsRepository.saveAndFlush(entity);
        log.info("User detail created successfully for user ID: {}", request.getUserId());
        return wrapperCreateToUserDetailResponse(savedEntity);
    }

    public UpdateUserDetailResponse getUserDetail(Integer userId) {
        log.info("Fetching user detail for user ID: {}", userId);
        UserDetailsEntity entity = userDetailsRepository.findByUserId(userId);
        if (entity == null) {
            throw new IllegalArgumentException("User detail not found for user ID: " + userId);
        }
        return wrapperUpdateToUserDetailResponse(entity);
    }

    public UpdateUserDetailResponse updateUserDetail(UpdateUserDetailRequest request, Integer id, String updatedBy) {
        log.info("Updating user detail for user ID: {}", id);
        UserDetailsEntity entity = userDetailsRepository.findByUserId(id);
        if (entity == null) {
            throw new IllegalArgumentException("User detail not found for user ID: " + id);
        }
        validateUpdateUserDetailRequest(id);
        updateUserDetailEntity(entity, id, request);
        entity.setUpdatedBy(updatedBy);
        UserDetailsEntity updatedEntity = userDetailsRepository.save(entity);
        log.info("User detail updated successfully for user ID: {}", id);
        return wrapperUpdateToUserDetailResponse(updatedEntity);
    }

    public UserDetailSearchResponse searchUserDetails(UserDetailSearchCriteria criteria) {
        log.info("Searching user details with criteria: {}", criteria);
        UserDetailSearchResponse response = new UserDetailSearchResponse();
        response.setCurrentPage(criteria.getPageNumber() != null ? criteria.getPageNumber() : 0);
        response.setPageSize(criteria.getPageSize() != null ? criteria.getPageSize() : 10);
        Pageable paging = getPageable(criteria);
        Page<SearchUserDetailInterface> searchResult = userDetailsRepository.searchUserDetails(
                criteria.getUsername(),
                criteria.getEmail(),
                criteria.getFirstName(),
                criteria.getLastName(),
                paging
        );
        response.setData(searchResult.stream().map(this::wrapperUpdateToUserDetailResponse).collect(Collectors.toList()));
        response.setTotalRecord((int) searchResult.getTotalElements());
        response.setTotalPage(searchResult.getTotalPages());
        log.info("Search user details with request: {} success", criteria);
        return response;
    }


    private UpdateUserDetailResponse wrapperUpdateToUserDetailResponse(SearchUserDetailInterface entity) {
        UpdateUserDetailResponse response = new UpdateUserDetailResponse();
        response.setUserId(entity.getUserId());
        response.setUsername(entity.getUsername());
        response.setEmail(entity.getEmail());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setAddress(entity.getAddress());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setProfilePictureUrl(entity.getProfilePictureUrl());
        response.setCreatedAt(dateUtilService.setLocalDateTimeFormat(entity.getCreatedAt()));
        response.setUpdatedAt(dateUtilService.setLocalDateTimeFormat(entity.getUpdatedAt()));
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }

    private Pageable getPageable(UserDetailSearchCriteria request) {
        int pageNumber = request.getPageNumber() != null ? request.getPageNumber() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        if (StringUtils.isBlank(request.getSortFieldName())) {
            return PageRequest.of(pageNumber, pageSize);
        }

        Sort sort = request.getIsDescending() != null && request.getIsDescending()
                ? Sort.by(Sort.Direction.DESC, request.getSortFieldName())
                : Sort.by(Sort.Direction.ASC, request.getSortFieldName());

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    @Transactional
    public boolean updateProfilePicture(int userId, byte[] imageBytes, String updatedBy) {
        log.info("Starting update of profile picture for userId: {}", userId);

        UserDetailsEntity userDetail = userDetailsRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with userId: {}", userId);
                    return new IllegalArgumentException("User not found");
                });

        String fileName = generateFileName(userId);
        String profilePictureUrl = s3Service.uploadImage(fileName, imageBytes);

        log.debug("New profile picture URL: {}", profilePictureUrl);
        userDetail.setProfilePictureUrl(profilePictureUrl);
        userDetail.setUpdatedBy(updatedBy);
        userDetail.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        userDetailsRepository.save(userDetail);
        log.info("Profile picture updated successfully for userId: {}", userId);
        return true;
    }

    private String generateFileName(int userId) {
        return "profile-pictures/" + userId + "-" + System.currentTimeMillis() + ".jpg";
    }

    public boolean deleteUserDetail(Integer userId) {
        log.info("Deleting user detail for user ID: {}", userId);
        if (!userDetailsRepository.existsById(userId)) {
            return false;
        }
        userDetailsRepository.deleteById(userId);
        log.info("User detail deleted successfully for user ID: {}", userId);
        return true;
    }

    public boolean isAdminOrOwner(String username, Integer userId) {
        UsersEntity user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        if (user.getId().equals(userId)) {
            return false;
        }

        return userRolesRepository.findByUserId(user.getId())
                .stream()
                .noneMatch(userRole -> "ROLE_ADMIN".equals(userRole.getRolesByRoleId().getName()));
    }

    private void validateUpdateUserDetailRequest(Integer id) {
        if (id == null || !usersRepository.existsById(id)) {
            throw new IllegalArgumentException("Invalid user ID");
        }
    }

    private void validateCreateUserDetailRequest(UserDetailRequest request) {
        if (request.getUserId() == null || !usersRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("Invalid user ID");
        }
    }

    private void createUserDetailEntity(UserDetailsEntity entity, UserDetailRequest request) {
        entity.setUserId(request.getUserId());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setAddress(request.getAddress());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setProfilePictureUrl(request.getProfilePictureUrl());
    }

    private void updateUserDetailEntity(UserDetailsEntity entity, Integer id, UpdateUserDetailRequest request) {
        entity.setUserId(id);
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setAddress(request.getAddress());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setProfilePictureUrl(request.getProfilePictureUrl());
    }

    private CreateUserDetailResponse wrapperCreateToUserDetailResponse(UserDetailsEntity entity) {
        CreateUserDetailResponse response = new CreateUserDetailResponse();
        response.setUserId(entity.getUserId());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setAddress(entity.getAddress());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setProfilePictureUrl(entity.getProfilePictureUrl());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }

    private UpdateUserDetailResponse wrapperUpdateToUserDetailResponse(UserDetailsEntity entity) {
        UpdateUserDetailResponse response = new UpdateUserDetailResponse();
        response.setUserId(entity.getUserId());
        response.setUsername(entity.getUsersByUserId().getUsername());
        response.setEmail(entity.getUsersByUserId().getEmail());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setAddress(entity.getAddress());
        response.setPhoneNumber(entity.getPhoneNumber());

        if (entity.getProfilePictureUrl() != null) {
            response.setProfilePictureUrl(s3Service.getImagePresignedUrl(entity.getProfilePictureUrl()));
        }

        response.setCreatedAt(dateUtilService.setLocalDateTimeFormat(entity.getCreatedAt().toLocalDateTime()));
        response.setUpdatedAt(dateUtilService.setLocalDateTimeFormat(entity.getUpdatedAt().toLocalDateTime()));
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }

    private Specification<UserDetailsEntity> createSpecification(UserDetailSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("usersByUserId").get("username")), "%" + criteria.getUsername().toLowerCase() + "%"));
            }
            if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("usersByUserId").get("email")), "%" + criteria.getEmail().toLowerCase() + "%"));
            }
            if (criteria.getFirstName() != null && !criteria.getFirstName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + criteria.getFirstName().toLowerCase() + "%"));
            }
            if (criteria.getLastName() != null && !criteria.getLastName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + criteria.getLastName().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public UpdateUserDetailResponse getUserOwnDetail(String username) {
        log.info("Fetching user detail for user username: {}", username);
        Optional<UsersEntity> userEntity = usersRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            UserDetailsEntity entity = userEntity.get().getUserDetailsById();
            if (entity == null) {
                throw new IllegalArgumentException("User detail not found for user username: " + username);
            }
            return wrapperUpdateToUserDetailResponse(entity);
        } else {
            throw new IllegalArgumentException("User not found for user username: " + username);
        }
    }
}