package com.hsf302.final_project.repository;
import com.hsf302.final_project.constant.ECourseStatus;
import com.hsf302.final_project.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.currentPublishedVersion v
        LEFT JOIN FETCH v.thumbnail t
        JOIN FETCH c.instructor i
        WHERE c.deleteFlag = false
          AND v.status = :status
        ORDER BY c.createdAt DESC
        LIMIT 5
    """)
    List<Course> findTop5PublishedCourses(@Param("status") ECourseStatus status);
}