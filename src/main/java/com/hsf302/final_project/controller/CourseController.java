package com.hsf302.final_project.controller;

import com.hsf302.final_project.dto.response.CourseDetailDTO;
import com.hsf302.final_project.security.CustomUserDetails;
import com.hsf302.final_project.service.CourseService;
import com.hsf302.final_project.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    // GET /course/{id} — Trang chi tiết khoá học
    @GetMapping("/course/{id}")
    public String courseDetail(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CourseDetailDTO course = courseService.getCourseDetail(id);
        model.addAttribute("course", course);

        boolean enrolled = false;
        if (userDetails != null) {
            enrolled = enrollmentService.isEnrolled(
                    userDetails.getUser().getUserId(),
                    id
            );
        }
        model.addAttribute("enrolled", enrolled);
        return "pages/course-detail";
    }

    // GET /payment/{courseId} — Trang thanh toán (placeholder)
    @GetMapping("/payment/{courseId}")
    public String paymentPage(
            @PathVariable Long courseId,
            Model model
    ) {
        CourseDetailDTO course = courseService.getCourseDetail(courseId);
        model.addAttribute("course", course);
        return "pages/payment";
    }

    // GET /learn/{courseId} — Trang học (placeholder)
    @GetMapping("/learn/{courseId}")
    public String learnPage(
            @PathVariable Long courseId,
            Model model
    ) {
        CourseDetailDTO course = courseService.getCourseDetail(courseId);
        model.addAttribute("course", course);
        return "pages/learn";
    }

    // Khoá học không tồn tại → redirect về trang chủ thay vì hiện blank page
    @ExceptionHandler(RuntimeException.class)
    public String handleCourseNotFound(RuntimeException ex) {
        return "redirect:/";
    }
}
