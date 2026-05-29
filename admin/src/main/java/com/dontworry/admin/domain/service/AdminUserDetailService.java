package com.dontworry.admin.domain.service;

import com.dontworry.admin.domain.repository.PlaceKeywordsRepository;
import com.dontworry.admin.domain.repository.UserPlacesRepository;
import com.dontworry.admin.domain.repository.UsersRepository;
import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.user.enums.ActiveStatus;
import com.dontworry.core.domain.user.enums.UserRole;
import com.dontworry.core.domain.userPlaceKeyword.entity.UserPlaceKeywords;
import com.dontworry.core.domain.userPlace.entity.UserPlaces;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminUserDetailService {
    private final UsersRepository usersRepository;
    private final UserPlacesRepository userPlacesRepository;
    private final PlaceKeywordsRepository placeKeywordsRepository;

    // ====== 수정 페이지용 단건 조회 ======
    @Transactional(readOnly = true)
    public Users getUser(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    // ====== 수정 저장 (edit page submit) ======
    @Transactional
    public void updateUser(Long id, UserRole role) {
        Users u = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // 필요하면 관리자 보호
        // if (u.isAdmin()) throw new IllegalStateException("Admin user cannot be modified.");

        // 엔티티 setter/도메인메서드에 맞게 조정 필요
        u.setRole(role);
    }

    // ====== 활성/비활성 (삭제 대체) ======
    @Transactional
    public void setActiveStatus(Long id, ActiveStatus status) {
        Users u = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // 필요하면 관리자 보호
        // if (u.isAdmin()) throw new IllegalStateException("Admin user cannot be deactivated.");

        u.setIsActive(status);
    }

    // ====== 사용자 플레이스 조회 ======
    @Transactional(readOnly = true)
    public List<UserPlaces> getUserPlaces(Long userId) {
        return userPlacesRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ====== 사용자 키워드 조회 ======
    @Transactional(readOnly = true)
    public Map<Long, List<UserPlaceKeywords>> getPlaceKeywordsGroupedByPlace(Long userId) {
        List<UserPlaces> places = userPlacesRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Long> placeIds = places.stream().map(UserPlaces::getId).toList();

        if (placeIds.isEmpty()) return new LinkedHashMap<>();

        List<UserPlaceKeywords> keywords = placeKeywordsRepository.findByUserPlacesIdInOrderByCreatedAtDesc(placeIds);

        Map<Long, List<UserPlaceKeywords>> map = new LinkedHashMap<>();
        for (Long pid : placeIds) map.put(pid, new ArrayList<>());

        for (UserPlaceKeywords k : keywords) {
            Long pid = k.getUserPlaces().getId();
            map.computeIfAbsent(pid, x -> new ArrayList<>()).add(k);
        }
        return map;
    }


}