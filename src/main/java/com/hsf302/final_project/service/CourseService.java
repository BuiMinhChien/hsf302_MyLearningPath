package com.hsf302.final_project.service;

import com.hsf302.final_project.dto.request.CourseOverviewForm;

public interface CourseService {
    Long createCourseOverview(CourseOverviewForm form);
    CourseOverviewForm getCourseOverview(Long courseId);
}
