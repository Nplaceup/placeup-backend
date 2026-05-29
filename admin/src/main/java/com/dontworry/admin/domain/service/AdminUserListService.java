package com.dontworry.admin.domain.service;

import com.dontworry.admin.controller.dto.RoleCountDto;
import com.dontworry.admin.controller.dto.StatusCountDto;
import com.dontworry.admin.domain.repository.UsersRepository;
import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.user.enums.ActiveStatus;
import com.dontworry.core.domain.user.enums.PlanTier;
import com.dontworry.core.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserListService {

    private final UsersRepository usersRepository;

    // ====== 사용자 조회(필터) ======
    @Transactional(readOnly = true)
    public Page<Users> getUsers(UserRole role, PlanTier plan, ActiveStatus status, String email, Pageable pageable) {
        return usersRepository.findAllByFilters(role, plan, status, email, pageable);
    }

    // ====== 사용자 카운트 ======
    @Transactional(readOnly = true)
    public UserCounts getCounts() {
        long total = usersRepository.countBy();
        Map<UserRole, Long> roleCounts = toRoleCountMap(usersRepository.countGroupByRole());
        Map<ActiveStatus, Long> statusCounts = toStatusCountMap(usersRepository.countGroupByStatus());
        return new UserCounts(total, roleCounts, statusCounts);
    }

    @Getter
    @AllArgsConstructor
    public static class UserCounts {
        private final long total;
        private final Map<UserRole, Long> roleCounts;
        private final Map<ActiveStatus, Long> statusCounts;
    }

    // ====== 내부 변환 유틸 ======
    private Map<UserRole, Long> toRoleCountMap(List<RoleCountDto> rows) {
        Map<UserRole, Long> map = new LinkedHashMap<>();
        for (RoleCountDto r : rows) map.put(r.role(), r.count());
        return map;
    }

    private Map<ActiveStatus, Long> toStatusCountMap(List<StatusCountDto> rows) {
        Map<ActiveStatus, Long> map = new LinkedHashMap<>();
        for (StatusCountDto r : rows) map.put(r.status(), r.count());
        return map;
    }
}