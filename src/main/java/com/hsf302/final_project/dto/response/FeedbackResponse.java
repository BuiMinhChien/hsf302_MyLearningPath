package com.hsf302.final_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {
    private Long feedbackId;
    private String studentName;
    private String studentEmail;
    private String studentAvatarUrl;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
