package com.hsf302.final_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDetailResponse {
    private Long courseId;
    private String title;
    private String subtitle;
    private String description;
    private BigDecimal price;
    private String thumbnailUrl;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private Integer totalStudents;
    private String instructorName;
    private String instructorEmail;
    private String instructorAvatarUrl;
    private List<SectionResponse> sections;
}

