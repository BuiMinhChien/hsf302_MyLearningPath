package com.hsf302.final_project.controller;
import com.hsf302.final_project.dto.response.CourseCardDTO;
import com.hsf302.final_project.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final CourseService courseService;
    @GetMapping({"/", "/home"})
    public String homePage(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Lấy username (email) của người đang login
        String username = (userDetails != null) ? userDetails.getUsername() : "Khách";
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Trang chủ - My Learning Path");
        // Lấy danh sách 5 khoá học
        List<CourseCardDTO> courses = courseService.getTop5Courses();
        model.addAttribute("courses", courses);
        return "pages/home"; // → templates/pages/home.html
    }
}