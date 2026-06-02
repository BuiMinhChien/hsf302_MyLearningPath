package com.hsf302.final_project.controller;

import com.hsf302.final_project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Đăng nhập");
        return "pages/login";
    }

    // ───── QUÊN MẬT KHẨU ─────

    /** Hiển thị form nhập email */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("pageTitle", "Quên mật khẩu");
        return "pages/forgot-password";
    }

    /** Xử lý khi user submit email */
    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("email") String email,
            Model model
    ) {
        try {
            authService.forgotPassword(email); // ← chỉ truyền email thôi
            model.addAttribute("success", true);
        } catch (RuntimeException e) {
            model.addAttribute("error", true);
        }
        return "pages/forgot-password";
    }
}