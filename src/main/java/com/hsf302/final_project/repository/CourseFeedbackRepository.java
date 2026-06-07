package com.hsf302.final_project.repository;

import com.hsf302.final_project.entity.CourseFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseFeedbackRepository extends JpaRepository<CourseFeedback, Long> {
    // Lấy đánh giá của 1 khoá học, mới nhất lên trước
    List<CourseFeedback> findByCourse_CourseIdOrderByCreatedAtDesc(Long courseId);
    Optional<CourseFeedback> findByCourse_CourseIdAndStudent_UserId(Long courseId, Long studentId);
}
