package com.dontworry.admin.domain.repository;

import com.dontworry.core.domain.payment.entity.UserPayments;
import com.dontworry.core.domain.payment.enums.PaymentStatus;
import com.dontworry.core.domain.payment.enums.UsageType;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

public interface AdminPaymentRepository extends JpaRepository<UserPayments, Long> {
}