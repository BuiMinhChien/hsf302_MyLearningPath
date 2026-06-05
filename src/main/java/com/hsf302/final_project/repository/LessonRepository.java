package com.hsf302.final_project.repository;

import com.hsf302.final_project.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findBySection_SectionIdOrderByDisplayOrder(Long sectionId);
}
