package com.hsf302.final_project.controller;

import com.hsf302.final_project.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/test-s3")
public class S3TestController {

    @Autowired
    private S3Service s3Service;

    // Hiển thị trang web test
    @GetMapping
    public String showTestPage() {
        // Đã sửa để Spring Boot tìm đúng vào trong thư mục test-s3
        return "test-s3/test-s3";
    }

    // Xử lý khi bấm nút Upload
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Bạn chưa chọn file nào!");
            return "test-s3/test-s3"; // Đã sửa
        }

        try {
            // Gọi hàm S3Service để đẩy file lên mạng
            String url = s3Service.uploadFile(file);
            // Gửi link trả về cho màn hình HTML
            model.addAttribute("fileUrl", url);
            model.addAttribute("success", "Upload thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi upload: " + e.getMessage());
        }

        return "test-s3/test-s3"; // Đã sửa
    }
}