package com.hsf302.final_project.controller;

import com.hsf302.final_project.dto.request.CourseOverviewForm;
import com.hsf302.final_project.dto.request.LessonRequest;
import com.hsf302.final_project.dto.request.SectionRequest;
import com.hsf302.final_project.dto.request.VideoLessonRequest;
import com.hsf302.final_project.dto.response.SectionResponse;
import com.hsf302.final_project.service.CourseService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping({"/home", "/"})
    public String homePage(Model model) {
        model.addAttribute(
                "pageTitle",
                "Trang chủ"
        );
        return "pages/home";
    }

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
}
