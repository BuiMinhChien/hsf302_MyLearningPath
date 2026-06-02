package com.hsf302.final_project.controller;

import com.hsf302.final_project.dto.request.CourseOverviewForm;
import com.hsf302.final_project.service.CourseService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CourseController {
    private static final Logger logger =
            LoggerFactory.getLogger(CourseController.class);
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
    public String editCoursePage(
            @PathVariable String courseId,
            Model model
    ) {
        CourseOverviewForm courseForm;
        if ("new".equals(courseId)) {
            courseForm = new CourseOverviewForm();
        } else {
            Long id = Long.parseLong(courseId);
            courseForm = courseService.getCourseOverview(id);
        }
        model.addAttribute("courseForm", courseForm);
        return "pages/createCourse/createCourse";
    }

    @PostMapping("/create/course-overview")
    public String createCourse(
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

//    @PostMapping("/create")
//    public String createCourse() {
//        Course course = new Course();
//        course.setStatus(CourseStatus.DRAFT);
//        courseRepository.save(course);
//        return "redirect:/instructor/course/" + course.getId() + "/overview";
//    }
}
