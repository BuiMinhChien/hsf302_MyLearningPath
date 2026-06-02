package com.hsf302.final_project.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * Service gửi email - dùng cho quên mật khẩu và gửi lại mật khẩu mới.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    public void sendNewPasswordEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Mật khẩu mới - My Learning Path");
        message.setText(
                "Xin chào!\n\n"
                        + "Mật khẩu mới của bạn là: " + newPassword + "\n\n"
                        + "Vui lòng đăng nhập và đổi mật khẩu ngay sau đó.\n\n"
                        + "Trân trọng!"
        );
        mailSender.send(message);
    }
}
