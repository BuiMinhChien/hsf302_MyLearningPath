package com.hsf302.final_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonRequest {
    private Long id;
    private Long sectionId;
    private Long courseId;
    private String title;
    private Integer displayOrder;
    private String type;
    // article
    private String content;
    // quiz
    private List<QuestionRequest> questions;
}
