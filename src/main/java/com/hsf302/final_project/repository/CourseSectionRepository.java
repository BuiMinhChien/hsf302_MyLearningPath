package com.hsf302.final_project.repository;

import com.hsf302.final_project.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    // Lấy các phần học của 1 courseVersion, sắp xếp theo displayOrder
    List<CourseSection> findByCourseVersionCourseVersionIdOrderByDisplayOrderAsc(
            Long courseVersionId
    );
    List<CourseSection> findByCourseVersion_CourseVersionIdOrderByDisplayOrder(Long courseVersionId);
}
