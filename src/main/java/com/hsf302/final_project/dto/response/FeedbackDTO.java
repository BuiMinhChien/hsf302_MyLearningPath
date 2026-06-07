package com.hsf302.final_project.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackDTO {
    String studentName;
    Integer rating;
    String comment;
    LocalDateTime createdAt;
}
