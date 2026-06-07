package com.hsf302.final_project.repository;

import com.hsf302.final_project.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    void deleteByLesson_LessonId(Long lessonId);
    List<QuizQuestion> findByLessonLessonIdOrderByDisplayOrder(Long lessonId);
}
