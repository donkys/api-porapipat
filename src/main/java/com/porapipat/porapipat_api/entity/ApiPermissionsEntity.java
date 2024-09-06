package com.porapipat.porapipat_api.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api_permissions", schema = "public", catalog = "porapipat")
public class ApiPermissionsEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Basic
    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @Basic
    @Column(name = "permission", nullable = false, length = 50)
    private String permission;

    @CreationTimestamp
    @Column(name = "created_at", nullable = true, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private Timestamp updatedAt;

    @Basic
    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @Basic
    @Column(name = "updated_by", nullable = true, length = 50)
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UsersEntity usersByUserId;

}
