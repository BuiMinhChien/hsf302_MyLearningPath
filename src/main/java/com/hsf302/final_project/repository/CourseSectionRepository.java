package com.hsf302.final_project.repository;

import com.hsf302.final_project.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByCourseVersion_CourseVersionIdOrderByDisplayOrder(Long courseVersionId);
}
