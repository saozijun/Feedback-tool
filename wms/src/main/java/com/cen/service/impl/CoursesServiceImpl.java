package com.cen.service.impl;

import com.cen.entity.Courses;
import com.cen.mapper.CoursesMapper;
import com.cen.service.ICoursesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author volcano
 * @since 2025-02-24
 */
@Service
public class CoursesServiceImpl extends ServiceImpl<CoursesMapper, Courses> implements ICoursesService {

    @Resource
    private CoursesMapper coursesMapper;

    @Override
    public List<Courses> getCoursesByStudentId(Long studentId) {
        return coursesMapper.getCoursesByStudentId(studentId);
    }
}
