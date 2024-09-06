package com.porapipat.porapipat_api.repository;

import com.porapipat.porapipat_api.entity.ApiPermissionsEntity;
import com.porapipat.porapipat_api.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesInterfaceRepository extends JpaRepository<RolesEntity, Integer> {
    Optional<RolesEntity> findByName(String name);
    boolean existsByName(String name);
}
