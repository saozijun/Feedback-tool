package com.cen.service;

import com.cen.entity.Courses;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author volcano
 * @since 2025-02-24
 */
public interface ICoursesService extends IService<Courses> {
    // 查询学生关联的课程列表
    List<Courses> getCoursesByStudentId(Long studentId);
}
