package com.hsf302.final_project.service;

import com.hsf302.final_project.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    @Override
    public boolean isEnrolled(Long studentId, Long courseId) {
        // Kiểm tra trong bảng enrollments có record không
        return enrollmentRepository
                .existsByStudentUserIdAndCourseCourseId(studentId, courseId);
    }
}
