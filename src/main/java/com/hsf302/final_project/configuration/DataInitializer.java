package com.hsf302.final_project.configuration;

import com.hsf302.final_project.constant.ECourseStatus;
import com.hsf302.final_project.constant.EFilePurpose;
import com.hsf302.final_project.constant.EFileType;
import com.hsf302.final_project.entity.*;
import com.hsf302.final_project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CourseRepository courseRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final UserRepository userRepository;
    private final AppFileRepository appFileRepository; // ← thêm mới

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        if (courseRepository.count() > 0) {
            log.info("Data đã tồn tại, bỏ qua DataInitializer.");
            return;
        }

        User instructor = userRepository
                .findByEmail("giangvien.java@fcourse.vn")
                .orElse(null);

        if (instructor == null) {
            log.warn("Không tìm thấy instructor, bỏ qua tạo khoá học.");
            return;
        }

        log.info("Bắt đầu tạo dữ liệu khoá học mẫu...");

        createCourse(instructor,
                "Spring Boot 3 - Từ Cơ Bản Đến Nâng Cao",
                "Học Spring Boot 3 với JPA, Security, REST API thực tế",
                "Khoá học Spring Boot đầy đủ nhất bằng tiếng Việt",
                new BigDecimal("259000"),
                new BigDecimal("4.8"), 1250, 3400,
                "/image/gioi.png"
        );

        createCourse(instructor,
                "ReactJS - Xây Dựng Web App Hiện Đại",
                "Học ReactJS, Hooks, Redux, React Router từ đầu",
                "Khoá học ReactJS thực chiến với project thực tế",
                new BigDecimal("299000"),
                new BigDecimal("4.6"), 890, 2100,
                "https://img-c.udemycdn.com/course/480x270/1362070_b9a1_2.jpg"
        );

        createCourse(instructor,
                "Java Fullstack - Spring Boot + React",
                "Xây dựng hệ thống web hoàn chỉnh với Spring Boot và React",
                "Khoá học fullstack Java phổ biến nhất Việt Nam",
                new BigDecimal("349000"),
                new BigDecimal("4.5"), 2300, 5600,
                "https://img-c.udemycdn.com/course/480x270/533682_c10c_4.jpg"
        );

        createCourse(instructor,
                "Docker & Kubernetes Cho Developer",
                "Containerize ứng dụng và deploy với K8s từ A đến Z",
                "DevOps cơ bản đến nâng cao cho Java developer",
                new BigDecimal("199000"),
                new BigDecimal("4.7"), 540, 1200,
                "/image/img.png"
        );

        createCourse(instructor,
                "Microservices Với Spring Cloud",
                "Thiết kế và triển khai hệ thống microservices thực tế",
                "Kiến trúc microservices từ cơ bản đến production",
                new BigDecimal("399000"),
                new BigDecimal("4.4"), 320, 780,
                "/image/tg.png"
        );

        log.info("Đã tạo xong 5 khoá học mẫu!");
    }

    private void createCourse(User instructor,
                              String title, String subtitle, String description,
                              BigDecimal price,
                              BigDecimal rating, int reviews, int students,
                              String imageUrl) {  // ← thêm tham số imageUrl

        // 1. Tạo AppFile lưu link ảnh
        AppFile thumbnail = AppFile.builder()
                .fileName(title)
                .fileUrl(imageUrl)
                .fileType(EFileType.IMAGE)
                .purpose(EFilePurpose.COURSE_THUMBNAIL)
                .build();
        thumbnail = appFileRepository.save(thumbnail);

        // 2. Tạo Course
        Course course = Course.builder()
                .instructor(instructor)
                .averageRating(rating)
                .totalReviews(reviews)
                .totalStudents(students)
                .build();
        course = courseRepository.save(course);

        // 3. Tạo CourseVersion gắn thumbnail
        CourseVersion version = CourseVersion.builder()
                .course(course)
                .versionNumber(1)
                .title(title)
                .subtitle(subtitle)
                .description(description)
                .price(price)
                .status(ECourseStatus.APPROVED)
                .thumbnail(thumbnail)  // ← gắn ảnh vào đây
                .build();
        version = courseVersionRepository.save(version);

        // 4. Gắn version vào course
        course.setCurrentPublishedVersion(version);
        courseRepository.save(course);
    }
}