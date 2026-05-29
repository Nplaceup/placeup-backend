package com.dontworry.core.domain.payment.enums;

import com.dontworry.core.domain.user.enums.PlanTier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum UsageType {

    PLAN_1(Category.PLAN, 0, 30, 49900),
    PLAN_1_PROMOTION(Category.PLAN, 0, 60, 49900),
    PLAN_3(Category.PLAN, 0, 90, 149700),
    PLAN_TRIAL(Category.PLAN, 0, 3, 0),
    PLAN_EVENT(Category.PLAN, 0, 7, 100),

    CASH_100(Category.CASH, 100, 0, 13000),
    CASH_200(Category.CASH, 200, 0, 26000),
    CASH_300(Category.CASH, 300, 0, 39000),
    CASH_400(Category.CASH, 400, 0, 49900),
    CASH_500(Category.CASH, 500, 0, 65000),
    CASH_700(Category.CASH, 700, 0, 91000),
    CASH_770(Category.CASH, 770, 0, 100000),
    CASH_2000(Category.CASH, 2000, 0, 249500),
    CASH_4000(Category.CASH, 4000, 0, 499000),
    CASH_5000(Category.CASH, 5000, 0, 623750),
    CASH_400_PROMOTION(Category.CASH, 40, 0, 49900),
    ADMIN_CASH(Category.CASH, 0, 0, 0)
    ;

    private final Category category;
    private final long cash;
    private final long expireDays;
    private final long amount;

    public enum Category {
        PLAN, CASH
    }

    public PlanTier getPlanTier() {
        return switch (this) {
            case PLAN_TRIAL -> PlanTier.TRIAL;
            case PLAN_1, PLAN_3, PLAN_1_PROMOTION -> PlanTier.PRO;
            case PLAN_EVENT -> PlanTier.EVENT;
            default -> PlanTier.FREE;
        };
    }

    public boolean isPlan() {
        return this.category == Category.PLAN;
    }

    public static final List<UsageType> PLAN_TYPES = List.of(
            PLAN_1, PLAN_3, PLAN_TRIAL, PLAN_1_PROMOTION, PLAN_EVENT
    );

}
