package com.porapipat.porapipat_api.repository;

import com.porapipat.porapipat_api.entity.ApiPermissionsEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiPermissionsInterfaceRepository extends JpaRepository<ApiPermissionsEntity, Integer> {
    List<ApiPermissionsEntity> findByUsersByUserIdUsernameAndApiName(String username, String apiName);
    List<ApiPermissionsEntity> findByUserId(Integer userId);
    List<ApiPermissionsEntity> findByApiName(String apiName);
    ApiPermissionsEntity findByUserIdAndApiName(Integer userId, String apiName);

    List<ApiPermissionsEntity> findByUserIdAndApiNameAndPermission(Integer userId, String apiName, String permission);

    void deleteByUserIdAndApiNameAndPermission(Integer userId, String apiName, String permission);

    void deleteByUserId(Integer userId);

    void deleteById(Integer id);

    boolean existsByUserIdAndApiNameAndPermission(Integer userId, String apiName, String permission);

    List<ApiPermissionsEntity> findAll(Specification<ApiPermissionsEntity> specification);
}
