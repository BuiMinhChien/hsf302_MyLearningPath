package com.hsf302.final_project.service;
import com.hsf302.final_project.dto.response.CourseCardDTO;
import com.hsf302.final_project.entity.Course;
import com.hsf302.final_project.entity.CourseVersion;
import com.hsf302.final_project.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import com.hsf302.final_project.constant.ECourseStatus;
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    @Override
    public List<CourseCardDTO> getTop5Courses() {
        return courseRepository.findTop5PublishedCourses(ECourseStatus.APPROVED)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    private CourseCardDTO mapToDTO(Course course) {
        CourseVersion version = course.getCurrentPublishedVersion();
        // Lấy URL thumbnail (nếu có)
        String thumbnailUrl = null;
        if (version.getThumbnail() != null) {
            thumbnailUrl = version.getThumbnail().getFileUrl(); // tuỳ theo field trong AppFile
        }
        return CourseCardDTO.builder()
                .courseId(course.getCourseId())
                .title(version.getTitle())
                .subtitle(version.getSubtitle())
                .instructorName(course.getInstructor().getFullName())
                .price(version.getPrice())
                .averageRating(course.getAverageRating())
                .totalReviews(course.getTotalReviews())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

}