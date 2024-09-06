package com.porapipat.porapipat_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_details", schema = "public", catalog = "porapipat")
public class UserDetailsEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Basic
    @Column(name = "first_name", nullable = true, length = 100)
    private String firstName;

    @Basic
    @Column(name = "last_name", nullable = true, length = 100)
    private String lastName;

    @Basic
    @Column(name = "address", nullable = true, length = -1)
    private String address;

    @Basic
    @Column(name = "phone_number", nullable = true, length = 15)
    private String phoneNumber;

    @Basic
    @Column(name = "profile_picture_url", nullable = true, length = 255)
    private String profilePictureUrl;

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

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UsersEntity usersByUserId;

}
