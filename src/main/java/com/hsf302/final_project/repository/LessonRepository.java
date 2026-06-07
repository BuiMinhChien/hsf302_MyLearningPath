package com.hsf302.final_project.repository;


import com.hsf302.final_project.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    // Lấy các bài học của 1 section, sắp xếp theo displayOrder
    List<Lesson> findBySectionSectionIdOrderByDisplayOrderAsc(Long sectionId);
}
