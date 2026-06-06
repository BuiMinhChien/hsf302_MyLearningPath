package com.hsf302.final_project.service;

import com.hsf302.final_project.dto.request.CourseOverviewForm;
import com.hsf302.final_project.dto.request.LessonRequest;
import com.hsf302.final_project.dto.request.SectionRequest;
import com.hsf302.final_project.dto.request.VideoLessonRequest;
import com.hsf302.final_project.dto.response.*;
import com.hsf302.final_project.entity.User;

import java.util.List;

public interface CourseService {
    List<CourseCardDTO> getTop5Courses();
    Long createCourseOverview(CourseOverviewForm form);
    CourseOverviewForm getCourseOverview(Long courseId);
    Long saveSection(SectionRequest request);
    List<SectionResponse> getSectionsByCourseVersionId(Long courseVersionId);
    void deleteSection(Long sectionId);
    Long saveLesson(LessonRequest request);
    Long saveVideoLesson(VideoLessonRequest request);
    void deleteLesson(Long lessonId);
    void deleteCourse(Long courseId);
    void publishCourse(Long courseId);
    // ================= COURSE DETAIL =================
    CourseDetailResponse getCourseDetail(Long courseId);
    // ================= COMMENT =================
    List<CommentResponse> getCommentsByLessonId(Long lessonId);
    CommentResponse addComment( Long lessonId, String content, User user );
    // ================= FEEDBACK =================
    List<FeedbackResponse> getFeedbackByCourseId(Long courseId);
    FeedbackResponse addFeedback( Long courseId, Integer rating, String comment, User user );
}