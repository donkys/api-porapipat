package com.porapipat.porapipat_api.repository;


import com.porapipat.porapipat_api.entity.UserDetailsEntity;
import com.porapipat.porapipat_api.repository.searchinterface.SearchUserDetailInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsInterfaceRepository extends JpaRepository<UserDetailsEntity, Integer>, JpaSpecificationExecutor<UserDetailsEntity> {
    UserDetailsEntity findByUserId(Integer userId);
    void deleteByUserId(Integer userId);

    @Query(nativeQuery = true, value = "SELECT " +
            "ud.user_id as userId, " +
            "u.username as username, " +
            "u.email as email, " +
            "ud.first_name as firstName, " +
            "ud.last_name as lastName, " +
            "ud.address as address, " +
            "ud.phone_number as phoneNumber, " +
            "ud.profile_picture_url as profilePictureUrl, " +
            "ud.created_at as createdAt, " +
            "ud.updated_at as updatedAt, " +
            "ud.created_by as createdBy, " +
            "ud.updated_by as updatedBy " +
            "FROM user_details ud " +
            "JOIN users u ON ud.user_id = u.id " +
            "WHERE " +
            "(:username IS NULL OR u.username LIKE CONCAT('%', CAST(:username AS VARCHAR), '%')) AND " +
            "(:email IS NULL OR u.email LIKE CONCAT('%', CAST(:email AS VARCHAR), '%')) AND " +
            "(:firstName IS NULL OR ud.first_name LIKE CONCAT('%', CAST(:firstName AS VARCHAR), '%')) AND " +
            "(:lastName IS NULL OR ud.last_name LIKE CONCAT('%', CAST(:lastName AS VARCHAR), '%'))")
    Page<SearchUserDetailInterface> searchUserDetails(
            @Param("username") String username,
            @Param("email") String email,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable page
    );
}
