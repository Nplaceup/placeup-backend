package com.dontworry.admin.controller;

import com.dontworry.admin.controller.dto.AdminSaveKeywordRequest;
import com.dontworry.admin.domain.service.AdminPlaceKeywordsManageService;
import com.dontworry.admin.domain.service.AdminUserDetailService;
import com.dontworry.core.domain.user.enums.ActiveStatus;
import com.dontworry.core.domain.user.enums.PlanTier;
import com.dontworry.core.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminUserDetailController {

    private final AdminUserDetailService adminUserDetailService;
    private final AdminPlaceKeywordsManageService adminPlaceKeywordsManageService;

    // 수정 페이지
    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", adminUserDetailService.getUser(id));
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("plans", PlanTier.values());

        // 사용자 플레이스 + 플레이스별 키워드
        model.addAttribute("userPlaces", adminUserDetailService.getUserPlaces(id));
        model.addAttribute("placeKeywordsMap", adminUserDetailService.getPlaceKeywordsGroupedByPlace(id));

        return "user-edit";
    }

    // 수정 저장
    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @RequestParam UserRole role) {
        adminUserDetailService.updateUser(id, role);
        return "redirect:/admin/users";
    }

    // 활성/비활성 변경
    @PostMapping("/users/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam ActiveStatus isActive) {
        adminUserDetailService.setActiveStatus(id, isActive);
        return "redirect:/admin/users";
    }

    // 사용자 키워드 추가
    @PostMapping("/users/{id}/place-keywords")
    public String addPlaceKeyword(@PathVariable("id") Long userId,
                                  @ModelAttribute @Valid AdminSaveKeywordRequest request) {
        log.info("[ADMIN] add keyword userId={}, req={}", userId, request);
        adminPlaceKeywordsManageService.addKeywordToUserPlace(userId, request);
        return "redirect:/admin/users/" + userId + "/edit";
    }
}