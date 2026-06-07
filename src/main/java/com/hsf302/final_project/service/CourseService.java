package com.hsf302.final_project.service;

import com.hsf302.final_project.dto.response.CourseCardDTO;
import com.hsf302.final_project.dto.response.CourseDetailDTO;
import java.util.List;
public interface CourseService {
    // Lấy 5 khoá học mới nhất cho trang Home
    List<CourseCardDTO> getTop5Courses();
    // Lấy thông tin chi tiết 1 khoá học (cho trang Course Detail)
    CourseDetailDTO getCourseDetail(Long courseId);
}