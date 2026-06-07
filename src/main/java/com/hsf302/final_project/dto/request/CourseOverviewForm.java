package com.hsf302.final_project.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CourseOverviewForm {
    Long courseId;
    Long courseVersionId;
    String title;
    String subtitle;
    String description;
    BigDecimal price;
    String tags;
    MultipartFile thumbnailFile;
    String thumbnailUrl;
}
