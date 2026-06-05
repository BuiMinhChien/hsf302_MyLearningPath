package com.hsf302.final_project.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionRequest {
    private Long id;
    private Long courseVersionId;
    private String title;
    private Integer displayOrder;
}
