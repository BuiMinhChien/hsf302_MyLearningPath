package com.hsf302.final_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionResponse {
    private Long id;
    private String title;
    private Integer displayOrder;
    private List<LessonResponse> lessons;
}
