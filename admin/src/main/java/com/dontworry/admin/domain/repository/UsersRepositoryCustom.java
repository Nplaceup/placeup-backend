package com.dontworry.admin.domain.repository;

import com.dontworry.admin.controller.dto.RoleCountDto;
import com.dontworry.admin.controller.dto.StatusCountDto;
import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.user.enums.ActiveStatus;
import com.dontworry.core.domain.user.enums.PlanTier;
import com.dontworry.core.domain.user.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsersRepositoryCustom {

    Page<Users> findAllByFilters(
            UserRole role,
            PlanTier plan,
            ActiveStatus status,
            String email,
            Pageable pageable
    );

    List<RoleCountDto> countGroupByRole();

    List<StatusCountDto> countGroupByStatus();
}
