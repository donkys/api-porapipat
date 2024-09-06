package com.porapipat.porapipat_api.service.apipermission;

import com.porapipat.porapipat_api.dto.apipermission.request.ApiPermissionRequest;
import com.porapipat.porapipat_api.dto.apipermission.request.ApiPermissionSearchCriteria;
import com.porapipat.porapipat_api.dto.apipermission.response.ApiPermissionResponse;
import com.porapipat.porapipat_api.dto.apipermission.response.ApiPermissionSearchResponse;
import com.porapipat.porapipat_api.dto.apipermission.response.CreateApiPermissionResponse;
import com.porapipat.porapipat_api.entity.ApiPermissionsEntity;
import com.porapipat.porapipat_api.repository.ApiPermissionsInterfaceRepository;
import com.porapipat.porapipat_api.repository.UsersInterfaceRepository;
import com.porapipat.porapipat_api.service.util.DateUtilService;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
public class ApiPermissionService {

    private final ApiPermissionsInterfaceRepository apiPermissionsRepository;

    private final UsersInterfaceRepository usersRepository;

    private final DateUtilService dateUtilService;

    public ApiPermissionService(ApiPermissionsInterfaceRepository apiPermissionsRepository, UsersInterfaceRepository usersRepository) {
        this.apiPermissionsRepository = apiPermissionsRepository;
        this.usersRepository = usersRepository;
        dateUtilService = new DateUtilService();
    }

    public CreateApiPermissionResponse createApiPermission(ApiPermissionRequest request, String createdBy) {
        log.info("Creating API permission for user ID: {} and API: {}", request.getUserId(), request.getApiName());
        validateApiPermissionRequest(request);
        ApiPermissionsEntity entity = new ApiPermissionsEntity();
        entity.setUserId(request.getUserId());
        entity.setApiName(request.getApiName());
        entity.setPermission(request.getPermission());
        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(createdBy);
        ApiPermissionsEntity savedEntity = apiPermissionsRepository.save(entity);
        log.info("API permission created successfully for user ID: {} and API: {}", request.getUserId(), request.getApiName());
        return wrapperCreateApiPermissionResponse(savedEntity);
    }

    public List<ApiPermissionResponse> getApiPermissionsByUserId(Integer userId) {
        log.info("Fetching API permissions for user ID: {}", userId);
        List<ApiPermissionsEntity> permissions = apiPermissionsRepository.findByUserId(userId);
        return permissions.stream().map(this::mapToApiPermissionResponse).collect(Collectors.toList());
    }

//    public ApiPermissionResponse updateApiPermission(Integer id, UpdateApiPermissionRequest request, String updatedBy) {
//        log.info("Updating API permission with ID: {}", id);
//        ApiPermissionsEntity entity = apiPermissionsRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("API permission not found with ID: " + id));
//        validateUpdateApiPermissionRequest(id);
//        entity.setApiName(request.getApiName());
//        entity.setPermission(request.getPermission());
//        entity.setUpdatedBy(updatedBy);
//        ApiPermissionsEntity updatedEntity = apiPermissionsRepository.update(entity);
//        log.info("API permission updated successfully for ID: {}", id);
//        return mapToApiPermissionResponse(updatedEntity);
//    }

    public ApiPermissionResponse updateApiPermission(Integer id, Map<String, Object> updates) {
        log.info("Partially updating API permission with ID: {}", id);
        ApiPermissionsEntity entity = apiPermissionsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("API permission not found with ID: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "userId":
                    entity.setUserId((Integer) value);
                    break;
                case "apiName":
                    entity.setApiName((String) value);
                    break;
                case "permission":
                    entity.setPermission((String) value);
                    break;
            }
        });

        ApiPermissionsEntity updatedEntity = apiPermissionsRepository.save(entity);
        log.info("API permission partially updated successfully for ID: {}", id);
        return mapToApiPermissionResponse(updatedEntity);
    }

    public ApiPermissionSearchResponse searchApiPermissions(ApiPermissionSearchCriteria criteria) {
        log.info("Searching API permissions with criteria: {}", criteria);

        List<ApiPermissionsEntity> permissions = apiPermissionsRepository.findAll(createSpecification(criteria));

        List<ApiPermissionResponse> sortedPermissions = sortPermissions(permissions, criteria.getSortFieldName(), criteria.getIsDescending());

        ApiPermissionSearchResponse response = new ApiPermissionSearchResponse();
        response.setData(sortedPermissions);
        response.setTotalRecord(sortedPermissions.size());

        log.info("Search API permissions with criteria: {} success", criteria);
        return response;
    }

    private Specification<ApiPermissionsEntity> createSpecification(ApiPermissionSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), criteria.getUserId()));
            }
            if (criteria.getApiName() != null && !criteria.getApiName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("apiName")), "%" + criteria.getApiName().toLowerCase() + "%"));
            }
            if (criteria.getPermission() != null && !criteria.getPermission().isEmpty()) {
                predicates.add(cb.equal(root.get("permission"), criteria.getPermission()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<ApiPermissionResponse> sortPermissions(List<ApiPermissionsEntity> permissions, String sortFieldName, Boolean isDescending) {
        Comparator<ApiPermissionResponse> comparator = Comparator
                .comparing(ApiPermissionResponse::getApiName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ApiPermissionResponse::getUserId, Comparator.nullsLast(Comparator.naturalOrder()));

        if (sortFieldName != null && !sortFieldName.isEmpty()) {
            comparator = getComparator(sortFieldName);
            if (isDescending != null && isDescending) {
                comparator = comparator.reversed();
            }
        }

        return permissions.stream()
                .map(this::mapToApiPermissionResponse)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private Comparator<ApiPermissionResponse> getComparator(String sortFieldName) {
        return switch (sortFieldName) {
            case "userId" ->
                    Comparator.comparing(ApiPermissionResponse::getUserId, Comparator.nullsLast(Comparator.naturalOrder()));
            case "apiName" ->
                    Comparator.comparing(ApiPermissionResponse::getApiName, Comparator.nullsLast(Comparator.naturalOrder()));
            case "permission" ->
                    Comparator.comparing(ApiPermissionResponse::getPermission, Comparator.nullsLast(Comparator.naturalOrder()));
            default ->
                    Comparator.comparing(ApiPermissionResponse::getApiName, Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }

    public void deleteApiPermission(Integer userId, String apiName, String permission) {
        log.info("Deleting API permission for user ID: {}, API: {}, and permission: {}", userId, apiName, permission);
        List<ApiPermissionsEntity> permissions = apiPermissionsRepository.findByUserIdAndApiNameAndPermission(userId, apiName, permission);
        if (permissions.isEmpty()) {
            throw new IllegalArgumentException("API permission not found for user ID: " + userId + ", API: " + apiName + ", and permission: " + permission);
        }
        apiPermissionsRepository.deleteByUserIdAndApiNameAndPermission(userId, apiName, permission);
        log.info("{} API permission(s) deleted successfully for user ID: {}, API: {}, and permission: {}", permissions.size(), userId, apiName, permission);
    }

    public void deleteApiPermissionById(Integer id) {
        log.info("Deleting API permission by ID: {}", id);
        if (apiPermissionsRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("API permission not found id: " + id);
        }
        apiPermissionsRepository.deleteById(id);
        log.info("API permission(s) deleted successfully by ID: {}", id);
    }

    private ApiPermissionsEntity createExampleFromCriteria(ApiPermissionSearchCriteria criteria) {
        ApiPermissionsEntity example = new ApiPermissionsEntity();
        example.setUserId(criteria.getUserId());
        example.setApiName(criteria.getApiName());
        example.setPermission(criteria.getPermission());
        return example;
    }

    private void createValidateApiPermissionRequest(ApiPermissionRequest request) {
        if (request.getUserId() == null || !usersRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (request.getApiName() == null || request.getApiName().trim().isEmpty()) {
            throw new IllegalArgumentException("API name cannot be empty");
        }
        if (request.getPermission() == null || request.getPermission().trim().isEmpty()) {
            throw new IllegalArgumentException("Permission cannot be empty");
        }
    }

    private void validateApiPermissionRequest(ApiPermissionRequest request) {
        if (request.getUserId() == null || !usersRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (request.getApiName() == null || request.getApiName().trim().isEmpty()) {
            throw new IllegalArgumentException("API name cannot be empty");
        }
        if (request.getPermission() == null || request.getPermission().trim().isEmpty()) {
            throw new IllegalArgumentException("Permission cannot be empty");
        }
    }

    private void validateUpdateApiPermissionRequest(Integer idPermission) {
        if (!apiPermissionsRepository.existsById(idPermission)) {
            throw new IllegalArgumentException("Invalid user ID");
        }
    }


    private ApiPermissionResponse mapToApiPermissionResponse(ApiPermissionsEntity entity) {
        ApiPermissionResponse response = new ApiPermissionResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setApiName(entity.getApiName());
        response.setUsername(entity.getUsersByUserId().getUsername());
        response.setPermission(entity.getPermission());
        response.setCreatedAt(dateUtilService.setLocalDateTimeFormat(entity.getCreatedAt().toLocalDateTime()));
        response.setUpdatedAt(dateUtilService.setLocalDateTimeFormat(entity.getUpdatedAt().toLocalDateTime()));
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }

    private CreateApiPermissionResponse wrapperCreateApiPermissionResponse(ApiPermissionsEntity entity) {
        CreateApiPermissionResponse response = new CreateApiPermissionResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setApiName(entity.getApiName());
        response.setPermission(entity.getPermission());
        response.setCreatedAt(dateUtilService.setLocalDateTimeFormat(entity.getCreatedAt().toLocalDateTime()));
        response.setUpdatedAt(dateUtilService.setLocalDateTimeFormat(entity.getUpdatedAt().toLocalDateTime()));
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }
}