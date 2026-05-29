package com.dontworry.admin.controller.dto;

import com.dontworry.core.domain.user.enums.UserRole;

public record RoleCountDto(
        UserRole role,
        long count
) {}
