package com.porapipat.porapipat_api.repository;

import com.porapipat.porapipat_api.entity.UserRolesEntity;
import com.porapipat.porapipat_api.entity.UserRolesEntityPK;
import com.porapipat.porapipat_api.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRolesInterfaceRepository extends JpaRepository<UserRolesEntity, UserRolesEntityPK> {

    List<UserRolesEntity> findByUserId(Integer userId);

    List<UserRolesEntity> findByRoleId(Integer roleId);

    UserRolesEntity findByUserIdAndRoleId(Integer userId, Integer roleId);

    boolean existsByUserIdAndRoleId(Integer userId, Integer roleId);

    void deleteByUserId(Integer userId);

    void deleteByRoleId(Integer roleId);

    long countByUserId(Integer userId);

    long countByRoleId(Integer roleId);
}
