package com.dontworry.admin.controller;

import com.dontworry.admin.domain.service.AdminBillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminBillingController {

    private final AdminBillingService adminBillingService;

    @GetMapping("/payments")
    public String payments(@RequestParam(required = false) Long userId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(defaultValue = "createdAt") String sort,
                           @RequestParam(defaultValue = "DESC") String dir,
                           Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(dir), sort));
        var pageObj = adminBillingService.getPayments(userId, pageable);

        model.addAttribute("pageObj", pageObj);
        model.addAttribute("items", pageObj.getContent());
        model.addAttribute("userId", userId);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        return "payments";
    }
}