package com.dontworry.admin.controller;

import com.dontworry.admin.domain.service.AdminUserListService;
import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.user.enums.ActiveStatus;
import com.dontworry.core.domain.user.enums.PlanTier;
import com.dontworry.core.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserListController {

    private final AdminUserListService adminUserListService;

    @GetMapping("/users")
    public String users(@RequestParam(required = false) UserRole role,
                        @RequestParam(required = false) PlanTier plan,
                        @RequestParam(required = false) ActiveStatus status,
                        @RequestParam(required = false) String email,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(defaultValue = "id") String sort,
                        @RequestParam(defaultValue = "DESC") String dir,
                        Model model) {

        Sort.Direction direction = Sort.Direction.fromString(dir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<Users> usersPage = adminUserListService.getUsers(role, plan, status, email, pageable);

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("users", usersPage.getContent()); // 기존 템플릿 유지용
        model.addAttribute("counts", adminUserListService.getCounts());

        // 필터 유지
        model.addAttribute("role", role);
        model.addAttribute("plan", plan);
        model.addAttribute("status", status);
        model.addAttribute("email", email);

        // 페이징/정렬 유지
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        // 옵션
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("plans", PlanTier.values());
        model.addAttribute("statuses", ActiveStatus.values());

        return "users";
    }
}