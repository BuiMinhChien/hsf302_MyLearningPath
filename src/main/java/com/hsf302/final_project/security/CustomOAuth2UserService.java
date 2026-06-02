package com.hsf302.final_project.security;

import com.hsf302.final_project.constant.EAccountStatus;
import com.hsf302.final_project.constant.ERole;
import com.hsf302.final_project.entity.User;
import com.hsf302.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Service xử lý thông tin user từ Google OAuth2.
 * Tự động tạo account nếu user chưa tồn tại trong hệ thống.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Lấy thông tin user từ Google
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email    = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");

        // Tìm user trong DB, nếu chưa có thì tạo mới
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .fullName(fullName)
                            // Password rỗng vì đăng nhập qua Google (không dùng password)
                            .password("")
                            .role(ERole.STUDENT)
                            .status(EAccountStatus.ACTIVE)
                            .build();
                    return userRepository.save(newUser);
                });

        return new CustomOAuth2User(oAuth2User, user);
    }
}
