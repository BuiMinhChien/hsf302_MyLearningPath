package com.hsf302.final_project.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerResponse {
    Long id;
    String text;
    Boolean isCorrect;
    Integer displayOrder;
}
