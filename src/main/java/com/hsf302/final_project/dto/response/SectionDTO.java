package com.hsf302.final_project.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectionDTO {
    Long sectionId;
    String title;
    Integer displayOrder;
    List<LessonDTO> lessons; // danh sách bài học trong phần này
}
