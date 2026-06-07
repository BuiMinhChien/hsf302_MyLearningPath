package com.hsf302.final_project.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonDTO {
    Long lessonId;
    String title;
    String lessonType;      // VIDEO, ARTICLE, QUIZ
    Integer durationSeconds; // thời lượng video (giây)
    Integer displayOrder;
}