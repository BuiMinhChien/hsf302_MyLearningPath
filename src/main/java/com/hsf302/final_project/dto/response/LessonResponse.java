package com.hsf302.final_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {
    private Long id;
    private String title;
    private String type;
    private Integer displayOrder;
    // ARTICLE
    private String articleContent;
    // VIDEO
    private String videoUrl;
    private String subtitleUrl;
    // QUIZ
    private List<QuestionResponse> questions;
}
