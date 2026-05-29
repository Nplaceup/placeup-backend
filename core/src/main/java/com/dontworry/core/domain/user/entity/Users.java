package com.dontworry.core.domain.user.entity;

import com.dontworry.core.domain.user.enums.ActiveStatus;
import com.dontworry.core.domain.user.enums.PlanTier;
import com.dontworry.core.domain.user.enums.UserRole;
import com.dontworry.core.domain.userActivity.entity.UserActivity;
import com.dontworry.core.domain.userPlace.entity.UserPlaces;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column
    private LocalDateTime lastLoginAt;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActiveStatus isActive;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<UserPlaces> userPlaces;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<UserActivity> userActivity;

    public void inactivate() {
        this.isActive = ActiveStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
}
