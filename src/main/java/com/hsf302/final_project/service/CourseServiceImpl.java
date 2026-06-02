package com.hsf302.final_project.service;

import com.hsf302.final_project.constant.ECourseStatus;
import com.hsf302.final_project.dto.request.CourseOverviewForm;
import com.hsf302.final_project.entity.Course;
import com.hsf302.final_project.entity.CourseVersion;
import com.hsf302.final_project.entity.Tag;
import com.hsf302.final_project.repository.CourseRepository;
import com.hsf302.final_project.repository.CourseVersionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseVersionRepository courseVersionRepository;

    @Transactional
    public Long createCourseOverview(CourseOverviewForm form) {
        Course course;
        CourseVersion version;
        // update
        if (form.getCourseId() != null && form.getCourseVersionId() != null) {
            course = courseRepository.findById(form.getCourseId())
                    .orElseThrow(() ->
                            new RuntimeException("Course not found"));
            version = courseVersionRepository.findById(
                            form.getCourseVersionId())
                    .orElseThrow(() ->
                            new RuntimeException("Course version not found"));
        }
        // create
        else {
            course = new Course();
            course.setAverageRating(BigDecimal.ZERO);
            course.setTotalReviews(0);
            course.setTotalStudents(0);
            courseRepository.save(course);
            version = new CourseVersion();
            version.setCourse(course);
            version.setVersionNumber(1);
            version.setStatus(ECourseStatus.DRAFT);
        }
        version.setTitle(form.getTitle());
        version.setSubtitle(form.getSubtitle());
        version.setDescription(form.getDescription());
        version.setPrice(
                form.getPrice() != null
                        ? form.getPrice()
                        : BigDecimal.ZERO
        );
        courseVersionRepository.save(version);
        // chỉ set version cho course khi create hoặc chưa có current draft version
        if (course.getCurrentDraftVersion() == null) {
            course.setCurrentDraftVersion(version);
        }
        courseRepository.save(course);
        return course.getCourseId();
    }

    @Override
    public CourseOverviewForm getCourseOverview(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));
        // ưu tiên lấy draft
        CourseVersion version =
                courseVersionRepository
                        .findByCourse_CourseIdAndStatus(
                                courseId,
                                ECourseStatus.DRAFT
                        )
                        .orElse(
                                course.getCurrentPublishedVersion()
                        );
        CourseOverviewForm form = new CourseOverviewForm();
        form.setCourseId(course.getCourseId());
        if (version != null) {
            form.setCourseVersionId(
                    version.getCourseVersionId()
            );
            form.setTitle(version.getTitle());
            form.setSubtitle(version.getSubtitle());
            form.setDescription(
                    version.getDescription()
            );
            form.setPrice(version.getPrice());
            String tags = version.getTags()
                    .stream()
                    .map(Tag::getTagName)
                    .collect(Collectors.joining(", "));
            form.setTags(tags);
        }
        return form;
    }
}
