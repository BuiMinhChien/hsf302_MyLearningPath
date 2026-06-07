package com.hsf302.final_project.repository;

import com.hsf302.final_project.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // Kiểm tra học viên đã đăng ký khoá học chưa
    boolean existsByStudentUserIdAndCourseCourseId(Long studentId, Long courseId);
    // Lấy tất cả khoá học đã đăng ký của học viên
    List<Enrollment> findByStudentUserId(Long studentId);
}
