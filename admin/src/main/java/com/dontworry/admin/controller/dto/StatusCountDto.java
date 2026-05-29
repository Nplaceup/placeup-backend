package com.dontworry.admin.controller.dto;

import com.dontworry.core.domain.user.enums.ActiveStatus;

public record StatusCountDto(
        ActiveStatus status,
        long count
) {}
