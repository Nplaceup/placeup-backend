package com.dontworry.admin.domain.service;

import com.dontworry.admin.domain.repository.PaymentHistoryRepository;
import com.dontworry.core.domain.payment.entity.UserPayments;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBillingService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional(readOnly = true)
    public Page<UserPayments> getPayments(Long userId, Pageable pageable) {
        if (userId == null) {
            return paymentHistoryRepository.findAll(pageable);
        }
        return paymentHistoryRepository.findByUser_Id(userId, pageable);
    }

}