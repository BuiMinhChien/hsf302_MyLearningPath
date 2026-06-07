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
public class QuestionRequest {
    private Integer displayOrder;
    private String questionText;
    private List<AnswerRequest> answers;
}
