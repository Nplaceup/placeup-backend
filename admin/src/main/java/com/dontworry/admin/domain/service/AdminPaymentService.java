package com.dontworry.admin.domain.service;

import com.dontworry.admin.controller.dto.AdminPaymentConfirmRequest;
import com.dontworry.admin.domain.repository.AdminPaymentRepository;
import com.dontworry.admin.domain.repository.UsersRepository;
import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.payment.entity.UserPayments;
import com.dontworry.core.domain.payment.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminPaymentService {

    private final AdminPaymentRepository paymentRepository;
    private final UsersRepository usersRepository;

    private String createOrderId(String orderType) {
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int randomNum = new Random().nextInt(9000) + 1000;
        return orderType + date + "-" + randomNum;
    }

    /**
     * ✅ Admin에서 “지급/수정” 버튼 눌렀을 때 호출하는 메서드
     * API의 confirmPayment()와 이름/흐름을 맞춤
     */
    @Transactional
    public UserPayments confirmPayment(Users user, AdminPaymentConfirmRequest request) {

        UsageType usageType = request.getOrderName();

        // 관리자 지급은 무조건 DONE 처리
        UserPayments userPayments = UserPayments.builder()
                .user(user)
                .orderId(createOrderId("ADMIN-"))
                .customerKey(null)
                .usageType(usageType)
                .amount(request.getAmount())
                .paymentMethod(PaymentMethod.ADMIN_PROVIDED)
                .paymentStatus(PaymentStatus.DONE)
                .pgProvider(null)
                .build();

        paymentRepository.save(userPayments);

        // API handleSuccess() 흐름 재사용
//        switch (usageType.getCategory()) {
//            case PLAN -> handlePlan(user, userPayments);
//            case CASH -> handleCash(user, userPayments, request.getAmount());
//            default -> throw new IllegalArgumentException("UNSUPPORTED_USAGE_TYPE: " + usageType);
//        }

        return userPayments;
    }

    public Users getUser(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}