package com.hsf302.final_project.repository;

import com.hsf302.final_project.constant.ECourseStatus;
import com.hsf302.final_project.entity.CourseVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseVersionRepository extends JpaRepository<CourseVersion, Long> {
    Optional<CourseVersion> findByCourse_CourseIdAndStatus(
            Long courseId,
            ECourseStatus status
    );
}