package com.dontworry.admin.controller.dto;

import com.dontworry.core.domain.payment.enums.UsageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminPaymentConfirmRequest {

    @NotNull
    private Long userId;

    @NotNull
    private UsageType orderName;

    @NotNull
    private Long amount; // CASH는 지급액, PLAN은 0
}