package com.porapipat.porapipat_api.repository;

import com.porapipat.porapipat_api.entity.UsersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersInterfaceRepository extends JpaRepository<UsersEntity, Integer>, JpaSpecificationExecutor<UsersEntity> {

    Optional<UsersEntity> findByUsername(String username);
    Optional<UsersEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT * FROM users u " +
            "WHERE (:username IS NULL OR u.username LIKE CONCAT('%', CAST(:username AS VARCHAR), '%')) " +
            "AND (:email IS NULL OR u.email LIKE CONCAT('%', CAST(:email AS VARCHAR), '%'))")
    Page<UsersEntity> searchUsers(
            @Param("username") String username,
            @Param("email") String email,
            Pageable pageable
    );
}
