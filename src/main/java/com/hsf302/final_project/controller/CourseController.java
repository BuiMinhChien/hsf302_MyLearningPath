package com.hsf302.final_project.controller;

import com.hsf302.final_project.dto.request.CourseOverviewForm;
import com.hsf302.final_project.dto.request.LessonRequest;
import com.hsf302.final_project.dto.request.SectionRequest;
import com.hsf302.final_project.dto.request.VideoLessonRequest;
import com.hsf302.final_project.dto.response.*;
import com.hsf302.final_project.security.CustomUserDetails;
import com.hsf302.final_project.service.CourseService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CourseController {
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;

    @GetMapping("/create-course/{courseId}")
    public String createCoursePage(@PathVariable String courseId, Model model) {
        CourseOverviewForm courseForm;
        List<SectionResponse> sections = new ArrayList<>();
        if ("new".equals(courseId)) {
            courseForm = new CourseOverviewForm();
        } else {
            Long id = Long.parseLong(courseId);
            courseForm = courseService.getCourseOverview(id);
            // lấy section theo courseVersionId
            sections = courseService.getSectionsByCourseVersionId(courseForm.getCourseVersionId());
        }
        model.addAttribute("courseForm", courseForm);
        // truyền sections lên frontend
        model.addAttribute("sections", sections);
        logger.info("courseForm={}", courseForm);
        logger.info("sections={}", sections);
        return "pages/create-course";
    }

    @PostMapping("/create-course/course-overview")
    public String createCourseOverview(
            @ModelAttribute CourseOverviewForm courseForm,
            RedirectAttributes redirectAttributes
    ) {
        Long courseId = courseService.createCourseOverview(courseForm);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Tạo khoá học thành công!"
        );
        return "redirect:/create-course/" + courseId;
    }

    @DeleteMapping("/create-course/{courseId}")
    @ResponseBody
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-course/section")
    @ResponseBody
    public ResponseEntity<?> saveSection(@RequestBody SectionRequest request) {
        try {
            Long sectionId = courseService.saveSection(request);
            Map<String, Object> response = new HashMap<>();
            response.put("id", sectionId);
            response.put("message", "Lưu chương thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(error);
        }
    }

    @DeleteMapping("/create-course/section/delete/{sectionId}")
    @ResponseBody
    public ResponseEntity<?> deleteSection(@PathVariable Long sectionId) {
        try {
            courseService.deleteSection(sectionId);
            Map<String, Object> response = new HashMap<>();
            response.put(
                    "message",
                    "Xoá chương thành công!"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put(
                    "message",
                    e.getMessage()
            );
            return ResponseEntity
                    .badRequest()
                    .body(error);
        }
    }

    @PostMapping(
            value = "/create-course/lesson",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> saveVideoLesson(
            @RequestParam Long sectionId,
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam Integer displayOrder,
            @RequestParam String type,
            @RequestParam(required = false) Long id,
            @RequestPart(required = false) MultipartFile videoFile,
            @RequestPart(required = false) MultipartFile subtitleFile
    ) {
        try {
            VideoLessonRequest request = new VideoLessonRequest();
            request.setId(id);
            request.setSectionId(sectionId);
            request.setCourseId(courseId);
            request.setTitle(title);
            request.setDisplayOrder(displayOrder);
            request.setType(type);
            request.setVideoFile(videoFile);
            request.setSubtitleFile(subtitleFile);
            Long lessonId = courseService.saveVideoLesson(request);
            Map<String, Object> response = new HashMap<>();
            response.put("id", lessonId);
            response.put(
                    "message",
                    "Lưu bài học video thành công!"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put(
                    "message",
                    e.getMessage()
            );
            return ResponseEntity
                    .badRequest()
                    .body(error);
        }
    }

    @PostMapping(
            value = "/create-course/lesson",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> saveLesson(@RequestBody LessonRequest request) {
        try {
            Long lessonId = courseService.saveLesson(request);
            Map<String, Object> response = new HashMap<>();
            response.put("id", lessonId);
            response.put(
                    "message",
                    "Lưu bài học thành công!"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put(
                    "message",
                    e.getMessage()
            );
            return ResponseEntity
                    .badRequest()
                    .body(error);
        }
    }

    @DeleteMapping("/create-course/lesson/delete/{lessonId}")
    @ResponseBody
    public ResponseEntity<?> deleteLesson(@PathVariable Long lessonId) {
        try {
            courseService.deleteLesson(lessonId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Xoá bài học thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(error);
        }
    }

    @PostMapping("/create-course/publish/{courseId}")
    @ResponseBody
    public Object publishCourse(@PathVariable Long courseId) {
        courseService.publishCourse(courseId);
        return java.util.Map.of(
                "success", true,
                "message", "Khoá học đã được xuất bản",
                "redirectUrl", "/"
        );
    }

    @GetMapping("/course/{courseId}")
    public String courseDetailPage(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long lessonId,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CourseDetailResponse courseDetail = courseService.getCourseDetail(courseId);
        logger.info("hihihihihihhi: {}",courseDetail.toString());
        model.addAttribute("course", courseDetail);

        LessonResponse activeLesson = null;
        if (lessonId != null) {
            for (SectionResponse s : courseDetail.getSections()) {
                for (LessonResponse l : s.getLessons()) {
                    if (l.getId().equals(lessonId)) {
                        activeLesson = l;
                        break;
                    }
                }
                if (activeLesson != null) break;
            }
        }
        if (activeLesson == null && !courseDetail.getSections().isEmpty()) {
            for (SectionResponse s : courseDetail.getSections()) {
                if (!s.getLessons().isEmpty()) {
                    activeLesson = s.getLessons().get(0);
                    break;
                }
            }
        }

        model.addAttribute("activeLesson", activeLesson);

        List<CommentResponse> comments = new ArrayList<>();
        if (activeLesson != null) {
            comments = courseService.getCommentsByLessonId(activeLesson.getId());
        }
        model.addAttribute("comments", comments);

        List<FeedbackResponse> feedbacks = courseService.getFeedbackByCourseId(courseId);
        model.addAttribute("feedbacks", feedbacks);

        String username = (userDetails != null) ? userDetails.getUsername() : "Khách";
        model.addAttribute("username", username);

        model.addAttribute("pageTitle", courseDetail.getTitle() + " - My Learning Path");

        return "pages/course-detail";
    }

    @GetMapping("/api/lessons/{lessonId}/comments")
    @ResponseBody
    public ResponseEntity<List<CommentResponse>> getLessonComments(@PathVariable Long lessonId) {
        return ResponseEntity.ok(courseService.getCommentsByLessonId(lessonId));
    }

    @PostMapping("/api/lessons/{lessonId}/comments")
    @ResponseBody
    public ResponseEntity<?> addLessonComment(
            @PathVariable Long lessonId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập"));
        }
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nội dung bình luận không được để trống"));
        }
        CommentResponse comment = courseService.addComment(lessonId, content, userDetails.getUser());
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/api/courses/{courseId}/feedbacks")
    @ResponseBody
    public ResponseEntity<List<FeedbackResponse>> getCourseFeedbacks(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getFeedbackByCourseId(courseId));
    }

    @PostMapping("/api/courses/{courseId}/feedbacks")
    @ResponseBody
    public ResponseEntity<?> addCourseFeedback(
            @PathVariable Long courseId,
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập"));
        }
        Object ratingObj = payload.get("rating");
        String comment = (String) payload.get("comment");
        if (ratingObj == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng chọn số sao đánh giá"));
        }
        Integer rating = Integer.parseInt(ratingObj.toString());
        FeedbackResponse feedback = courseService.addFeedback(courseId, rating, comment, userDetails.getUser());
        return ResponseEntity.ok(feedback);
    }
}
