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
public class CommentResponse {
    private Long commentId;
    private String userName;
    private String userEmail;
    private String avatarUrl;
    private String content;
    private LocalDateTime createdAt;
}
