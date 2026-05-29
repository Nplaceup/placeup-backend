package com.dontworry.core.domain.place.entity;

import com.dontworry.core.domain.ranking.entity.Rankings;
import com.dontworry.core.domain.userPlace.entity.UserPlaces;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "places")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Places {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long naverPlaceId;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String address;

    @Column
    private String placeUrl;

    @Column
    private Integer saveCount;

    @Column
    private String phoneNumber;

    @Column
    private Boolean reservationAvailable;

    @Column
    private Boolean naverPayAvailable;

    @Column
    private String talktalkUrl;

    @Column
    private Integer couponCount;

    @Column
    private Integer blogCafeReviewCount;

    @Column
    private Integer imageReviewCount;

    @Column
    private Integer newsPostCount;

    @Column(columnDefinition = "text")
    private String description;

    @Column
    private String menuList;

    @Column
    private String cidList;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    private List<UserPlaces> userPlaces;

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    private List<PlaceReviewAnalysis> placeReviewAnalysis;

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    private List<PlaceReviews> placeReviews;

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    private List<Rankings> rankings;

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    private List<PlaceCrawling> placeCrawlings;

    public static Places build(Long naverPlaceId, String placeName, String category, String address) {
        return Places.builder()
                .naverPlaceId(naverPlaceId)
                .placeName(placeName)
                .category(category)
                .address(address)
                .build();
    }

    public static Places buildWithDetails(
            Long naverPlaceId, String placeName, String category, String address,
            String placeUrl, Integer saveCount, String phoneNumber, Boolean reservationAvailable,
            Boolean naverPayAvailable, String talktalkUrl, Integer couponCount, String cidList) {

        return Places.builder()
                .naverPlaceId(naverPlaceId)
                .placeName(placeName)
                .category(category)
                .address(address)
                .placeUrl(placeUrl)
                .saveCount(saveCount)
                .phoneNumber(phoneNumber)
                .reservationAvailable(reservationAvailable)
                .naverPayAvailable(naverPayAvailable)
                .talktalkUrl(talktalkUrl)
                .couponCount(couponCount)
                .cidList(cidList)
                .build();
    }

    public void update(String placeName, String category, String address,
                       String phoneNumber, String talktalkUrl, Boolean reservationAvailable) {
        if (placeName != null && !placeName.isBlank()) this.placeName = placeName;
        if (category != null && !category.isBlank()) this.category = category;
        if (address != null && !address.isBlank()) this.address = address;
        if (phoneNumber != null && !phoneNumber.isBlank()) this.phoneNumber = phoneNumber;
        if (talktalkUrl != null && !talktalkUrl.isBlank()) this.talktalkUrl = talktalkUrl;
        if (reservationAvailable != null) this.reservationAvailable = reservationAvailable;
    }

    public void updateWithDetails(
            String placeName, String category, String address,
            String placeUrl, Integer saveCount, String phoneNumber, Boolean reservationAvailable,
            Boolean naverPayAvailable, String talktalkUrl, Integer couponCount, String cidList) {

        this.placeName = placeName;
        this.category = category;
        this.address = address;
        this.placeUrl = placeUrl;
        this.saveCount = saveCount;
        this.phoneNumber = phoneNumber;
        this.reservationAvailable = reservationAvailable;
        this.naverPayAvailable = naverPayAvailable;
        this.talktalkUrl = talktalkUrl;
        this.couponCount = couponCount;
        this.cidList = cidList;
    }

    public void updateCrawlingData(Integer blogCafeReviewCount, Integer imageReviewCount, Integer newsPostCount, String description, String menuList) {
        if (blogCafeReviewCount != null) this.blogCafeReviewCount = blogCafeReviewCount;
        if (imageReviewCount != null) this.imageReviewCount = imageReviewCount;
        if (newsPostCount != null) this.newsPostCount = newsPostCount;
        if (description != null) this.description = description;
        if (menuList != null) this.menuList = menuList;
    }
}