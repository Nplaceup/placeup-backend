package com.dontworry.core.domain.userActivity.entity;

import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.userActivity.enums.MissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_activity")
@EntityListeners(AuditingEntityListener.class)
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionType missionType;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static UserActivity build(Users user, MissionType missionType) {
        return UserActivity.builder()
                .user(user)
                .missionType(missionType)
                .build();
    }
}
