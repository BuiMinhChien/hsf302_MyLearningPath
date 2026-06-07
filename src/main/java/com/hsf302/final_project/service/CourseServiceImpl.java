package com.hsf302.final_project.service;

import com.hsf302.final_project.constant.ECourseStatus;
import com.hsf302.final_project.dto.response.CourseCardDTO;
import com.hsf302.final_project.entity.Course;
import com.hsf302.final_project.entity.CourseVersion;
import com.hsf302.final_project.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hsf302.final_project.dto.response.*;
import com.hsf302.final_project.entity.*;
import com.hsf302.final_project.repository.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseCardDTO> getTop5Courses() {

        // Lấy danh sách khoá học đã duyệt, không bị xoá
        List<Course> courses = courseRepository
                .findByDeleteFlagFalseAndCurrentPublishedVersion_StatusOrderByCreatedAtDesc(
                        ECourseStatus.APPROVED
                );

        // Giới hạn 5 khoá học đầu tiên
        // rồi chuyển từng Course sang CourseCardDTO
        return courses.stream()
                .limit(5)
                .map(this::chuyenDoiSangDTO)
                .toList();
    }

    // Hàm chuyển đổi từ Course (entity) → CourseCardDTO
    private CourseCardDTO chuyenDoiSangDTO(Course course) {

        CourseVersion phienBan = course.getCurrentPublishedVersion();

        // Lấy link ảnh nếu có, không thì để null
        String anhThumbnail = null;
        if (phienBan.getThumbnail() != null) {
            anhThumbnail = phienBan.getThumbnail().getFileUrl();
        }

        // Trả về DTO với các thông tin cần hiển thị
        return CourseCardDTO.builder()
                .courseId(course.getCourseId())
                .title(phienBan.getTitle())
                .subtitle(phienBan.getSubtitle())
                .instructorName(course.getInstructor().getFullName())
                .price(phienBan.getPrice())
                .averageRating(course.getAverageRating())
                .totalReviews(course.getTotalReviews())
                .thumbnailUrl(anhThumbnail)
                .build();
    }

    // ===== THÊM CÁC FIELD MỚI VÀO ĐẦU CLASS =====
    private final CourseSectionRepository courseSectionRepository;
    private final LessonRepository lessonRepository;
    private final CourseFeedbackRepository courseFeedbackRepository;


    @Override
    @Transactional(readOnly = true)
    public CourseDetailDTO getCourseDetail(Long courseId) {

        // 1. Lấy khoá học từ DB
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoá học!"));

        CourseVersion version = course.getCurrentPublishedVersion();

        // 2. Lấy URL ảnh thumbnail
        String thumbnailUrl = null;
        if (version.getThumbnail() != null) {
            thumbnailUrl = version.getThumbnail().getFileUrl();
        }

        // 3. Lấy danh sách phần học (sections) theo thứ tự
        List<CourseSection> danhSachPhan = courseSectionRepository
                .findByCourseVersionCourseVersionIdOrderByDisplayOrderAsc(
                        version.getCourseVersionId()
                );

        // 4. Với từng phần, lấy danh sách bài học (lessons)
        List<SectionDTO> sectionDTOs = new ArrayList<>();
        for (CourseSection phan : danhSachPhan) {

            List<Lesson> danhSachBaiHoc = lessonRepository
                    .findBySectionSectionIdOrderByDisplayOrderAsc(phan.getSectionId());

            List<LessonDTO> lessonDTOs = danhSachBaiHoc.stream()
                    .map(baiHoc -> LessonDTO.builder()
                            .lessonId(baiHoc.getLessonId())
                            .title(baiHoc.getTitle())
                            .lessonType(baiHoc.getLessonType() != null
                                    ? baiHoc.getLessonType().name() : "VIDEO")
                            .durationSeconds(baiHoc.getDurationSeconds())
                            .displayOrder(baiHoc.getDisplayOrder())
                            .build())
                    .toList();

            sectionDTOs.add(SectionDTO.builder()
                    .sectionId(phan.getSectionId())
                    .title(phan.getTitle())
                    .displayOrder(phan.getDisplayOrder())
                    .lessons(lessonDTOs)
                    .build());
        }

        // 5. Lấy 4 đánh giá mới nhất
        List<CourseFeedback> danhSachDanhGia = courseFeedbackRepository
                .findByCourse_CourseIdOrderByCreatedAtDesc(courseId);

        List<FeedbackDTO> feedbackDTOs = danhSachDanhGia.stream()
                .limit(4)
                .map(danhGia -> FeedbackDTO.builder()
                        .studentName(danhGia.getStudent().getFullName())
                        .rating(danhGia.getRating())
                        .comment(danhGia.getComment())
                        .createdAt(danhGia.getCreatedAt())
                        .build())
                .toList();

        // 6. Trả về CourseDetailDTO
        return CourseDetailDTO.builder()
                .courseId(course.getCourseId())
                .title(version.getTitle())
                .subtitle(version.getSubtitle())
                .description(version.getDescription())
                .price(version.getPrice())
                .thumbnailUrl(thumbnailUrl)
                .averageRating(course.getAverageRating())
                .totalReviews(course.getTotalReviews())
                .totalStudents(course.getTotalStudents())
                .instructorName(course.getInstructor().getFullName())
                .courseVersionId(version.getCourseVersionId())
                .sections(sectionDTOs)
                .feedbacks(feedbackDTOs)
                .build();
    }
}