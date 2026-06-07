package com.hsf302.final_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/create-course")
public class InstructorCourseController {

    // Thêm "/new" vào đây để khớp với nút trên thanh Menu
    @GetMapping("/new")
    public String showCreateCoursePage() {
        return "pages/create-course";
    }

}