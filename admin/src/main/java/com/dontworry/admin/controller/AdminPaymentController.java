package com.dontworry.admin.controller;

import com.dontworry.admin.controller.dto.AdminPaymentConfirmRequest;
import com.dontworry.admin.domain.service.AdminPaymentService;
import com.dontworry.core.domain.user.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final AdminPaymentService paymentService;

    /**
     * 사용자 관리 페이지에서 플랜/캐시 저장 버튼 → 여기로 POST
     * (API confirmPayment와 동일한 메서드명 컨셉)
     */
    @PostMapping("/confirm")
    public String confirmPayment(@ModelAttribute @Valid AdminPaymentConfirmRequest request) {
        Users user = paymentService.getUser(request.getUserId());
        paymentService.confirmPayment(user, request);

        // user-edit로 복귀
        return "redirect:/admin/users/" + request.getUserId() + "/edit";
    }
}