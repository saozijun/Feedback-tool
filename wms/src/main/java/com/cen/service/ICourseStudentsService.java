package com.cen.service;

import com.cen.entity.CourseStudents;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cen.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author volcano
 * @since 2025-02-24
 */
public interface ICourseStudentsService extends IService<CourseStudents> {
    
    // 分页查询课程关联的学生
    IPage<User> getStudentsByCourseId(Long courseId, Integer pageNum, Integer pageSize, String nickname);
    
    // 批量绑定学生
    boolean bindStudents(Long courseId, String studentIds);
    
    // 解除单个学生绑定
    boolean unbindStudent(Long courseId, Long studentId);
}
