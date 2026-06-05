package com.hsf302.final_project.repository;

import com.hsf302.final_project.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    void deleteByQuestion_QuestionId(Long questionId);
    List<QuizAnswer> findByQuestionQuestionIdOrderByDisplayOrder(Long questionId);
}
