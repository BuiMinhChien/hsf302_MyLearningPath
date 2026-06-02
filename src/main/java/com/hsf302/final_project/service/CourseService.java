package com.hsf302.final_project.service;

import com.hsf302.final_project.dto.response.CourseCardDTO;
import java.util.List;
public interface CourseService {
    List<CourseCardDTO> getTop5Courses();
}