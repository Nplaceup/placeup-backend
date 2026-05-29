package com.dontworry.admin.domain.repository;

import com.dontworry.core.domain.payment.entity.UserPayments;
import com.dontworry.core.domain.payment.enums.PaymentStatus;
import com.dontworry.core.domain.payment.enums.UsageType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<UserPayments, Long> {

    @EntityGraph(attributePaths = {"user", "planHistory", "cashHistory"})
    Page<UserPayments> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "planHistory", "cashHistory"})
    Page<UserPayments> findByUser_Id(Long userId, Pageable pageable);

}