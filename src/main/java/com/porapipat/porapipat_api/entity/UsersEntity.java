package com.porapipat.porapipat_api.entity;

import com.porapipat.porapipat_api.entity.enumeration.provider.ProviderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "public", catalog = "porapipat")
public class UsersEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "username", nullable = true, length = 50)
    private String username;

    @Basic
    @Column(name = "password", nullable = true, length = 100)
    private String password;

    @Basic
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    @Basic
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @CreationTimestamp
    @Column(name = "created_at", nullable = true, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "usersByUserId")
    private Collection<ApiPermissionsEntity> apiPermissionsById;

    @OneToOne(mappedBy = "usersByUserId")
    private UserDetailsEntity userDetailsById;

    @OneToMany(mappedBy = "usersByUserId")
    private Collection<UserRolesEntity> userRolesById;
}
