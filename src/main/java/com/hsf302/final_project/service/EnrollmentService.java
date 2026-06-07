package com.hsf302.final_project.service;

public interface EnrollmentService {
    // Kiểm tra học viên đã đăng ký khoá học chưa
    boolean isEnrolled(Long studentId, Long courseId);
}
