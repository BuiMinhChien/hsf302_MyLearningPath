package com.hsf302.final_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoLessonRequest {
    private Long id;
    private Long sectionId;
    private Long courseId;
    private String title;
    private Integer displayOrder;
    private String type;
    private MultipartFile videoFile;
    private MultipartFile subtitleFile;
}
