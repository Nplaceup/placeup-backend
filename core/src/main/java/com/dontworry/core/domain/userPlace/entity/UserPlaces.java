package com.dontworry.core.domain.userPlace.entity;

import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.place.entity.Places;
import com.dontworry.core.domain.userPlaceKeyword.entity.UserPlaceKeywords;
import com.dontworry.core.domain.userPlace.enums.PlaceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_places")
@EntityListeners(AuditingEntityListener.class)
public class UserPlaces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "places_id")
    private Places place;

    @Column(nullable = false)
    private Long naverPlaceId;

    @Column(nullable = false)
    private String naverPlaceName;

    @Column(nullable = false)
    private String naverPlaceUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceStatus placeStatus;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "userPlaces", cascade = CascadeType.REMOVE)
    private List<UserPlaceKeywords> userPlaceKeywords;

    public static UserPlaces build(Long userId, Long naverPlaceId, String naverPlaceName, String naverPlaceUrl) {
        Users user = Users.builder().id(userId).build();
        return UserPlaces.builder()
                .user(user)
                .naverPlaceId(naverPlaceId)
                .naverPlaceName(naverPlaceName)
                .naverPlaceUrl(naverPlaceUrl)
                .placeStatus(PlaceStatus.ACTIVE)
                .build();
    }

    public void modifyStatus() {
        if (this.getPlaceStatus() == PlaceStatus.ACTIVE) {
            this.placeStatus = PlaceStatus.INACTIVE;
        } else if (this.getPlaceStatus() == PlaceStatus.INACTIVE) {
            this.placeStatus = PlaceStatus.ACTIVE;
        }
    }

}
