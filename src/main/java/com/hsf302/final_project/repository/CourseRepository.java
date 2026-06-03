package com.hsf302.final_project.repository;

import com.hsf302.final_project.constant.ECourseStatus;
import com.hsf302.final_project.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Dùng method naming của Spring Data JPA
    // Spring tự hiểu: tìm Course mà deleteFlag = false
    // và currentPublishedVersion.status = status
    // sắp xếp theo createdAt giảm dần
    List<Course> findByDeleteFlagFalseAndCurrentPublishedVersion_StatusOrderByCreatedAtDesc(
            ECourseStatus status
    );
}