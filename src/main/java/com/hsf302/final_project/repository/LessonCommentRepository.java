package com.hsf302.final_project.repository;

import com.hsf302.final_project.entity.LessonComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonCommentRepository extends JpaRepository<LessonComment, Long> {
    List<LessonComment> findByLesson_LessonIdOrderByCreatedAtDesc(Long lessonId);
}
