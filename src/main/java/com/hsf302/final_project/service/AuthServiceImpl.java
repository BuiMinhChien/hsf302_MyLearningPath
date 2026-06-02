package com.hsf302.final_project.service;

import com.hsf302.final_project.constant.EAccountStatus;
import com.hsf302.final_project.dto.request.LoginRequest;
import com.hsf302.final_project.dto.response.UserResponse;
import com.hsf302.final_project.entity.User;
import com.hsf302.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UserResponse login(LoginRequest request) {
        // tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Email does not exist")
                );
        // kiểm tra trạng thái tài khoản
        if (user.getStatus() != EAccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        // kiểm tra password BCrypt
        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );
        if (!matches) {
            throw new RuntimeException("Invalid password");
        }
        // login thành công
        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    @Override
    public void forgotPassword(String email) {
        // 1. Kiểm tra email có trong DB không
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        // 2. Tự sinh mật khẩu mới ngẫu nhiên 8 ký tự
        String newPassword = generateRandomPassword();
        // 3. Mã hoá BCrypt rồi lưu vào DB
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // 4. Gửi mật khẩu CHƯA mã hoá qua email cho user
        emailService.sendNewPasswordEmail(email, newPassword);
    }
    // Hàm tạo mật khẩu ngẫu nhiên
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

