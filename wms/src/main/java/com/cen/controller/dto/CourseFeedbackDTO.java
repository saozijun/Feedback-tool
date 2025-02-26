package com.cen.controller.dto;

import com.cen.entity.CourseFeedback;
import com.cen.entity.User;
import lombok.Data;

@Data
public class CourseFeedbackDTO {
    private CourseFeedback feedback;  // 反馈信息
    private User student;  // 学生信息
} 