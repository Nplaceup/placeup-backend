package com.dontworry.core.domain.userPlaceKeyword.entity;

import com.dontworry.core.domain.keyword.entity.Keywords;
import com.dontworry.core.domain.userPlaceKeyword.enums.PlaceKeywordStatus;
import com.dontworry.core.domain.userPlace.entity.UserPlaces;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_place_keywords")
@EntityListeners(AuditingEntityListener.class)
public class UserPlaceKeywords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_places_id", nullable = false)
    private UserPlaces userPlaces;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keywords_id", nullable = false)
    private Keywords keyword;

    @Column(nullable = false)
    private String keywordName;  // 원본 키워드명 (공백 유지)

    @Column(nullable = false)
    private Boolean favoriteFlag;

    @Enumerated(EnumType.STRING)
    @Column
    private PlaceKeywordStatus status;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    public static UserPlaceKeywords build(UserPlaces userPlaces, Keywords keyword, String keywordName) {
        return UserPlaceKeywords.builder()
                .userPlaces(userPlaces)
                .keyword(keyword)
                .keywordName(keywordName)  // 원본 저장
                .favoriteFlag(false)
                .status(PlaceKeywordStatus.ACTIVE)
                .build();
    }

    public static UserPlaceKeywords buildInitial(UserPlaces userPlaces, Keywords keyword, String keywordName) {
        return UserPlaceKeywords.builder()
                .userPlaces(userPlaces)
                .keyword(keyword)
                .keywordName(keywordName)
                .favoriteFlag(true)
                .status(PlaceKeywordStatus.ACTIVE)
                .build();
    }

    public void toggleFavoriteFlag() {
        this.favoriteFlag = !this.favoriteFlag;
    }

    public void markFavorite() {
        this.favoriteFlag = true;
    }

    public void inactivePlaceKeywords() {
        this.status = PlaceKeywordStatus.INACTIVE;
        this.deletedAt = LocalDateTime.now();
    }
}