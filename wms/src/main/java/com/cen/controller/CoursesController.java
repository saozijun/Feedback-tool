package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.cen.service.ICoursesService;
import com.cen.entity.Courses;

import org.springframework.web.bind.annotation.RestController;
import lombok.Data;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author volcano
 * @since 2025-02-24
 */
@RestController
@RequestMapping("/courses")
public class CoursesController {

    @Resource
    private ICoursesService coursesService;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody Courses courses) {
        return Result.success(coursesService.saveOrUpdate(courses));
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody Courses courses){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(coursesService.removeById(courses.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(coursesService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable Courses courses) {
        return Result.success(coursesService.getById(courses.getId()));
    }

    // 全部课程列表(按学年学期分组)
    @GetMapping("/allList")
    public Result allList(@RequestParam(required = false) Long teacherId) {
        QueryWrapper<Courses> queryWrapper = new QueryWrapper<>();
        if (teacherId != null) {
            queryWrapper.eq("teacher_id", teacherId);
        }
        // 按学年降序、学期升序排序
        queryWrapper.orderByDesc("academic_year")
                   .orderByAsc("semester")
                   .orderByDesc("id");
        
        List<Courses> coursesList = coursesService.list(queryWrapper);
        
        // 按学年和学期分组，使用自定义比较器实现年份降序
        Map<String, Map<Integer, List<Courses>>> groupedMap = coursesList.stream()
                .filter(c -> c.getAcademicYear() != null && c.getSemester() != null)
                .collect(Collectors.groupingBy(
                        Courses::getAcademicYear,
                        () -> new TreeMap<String, Map<Integer, List<Courses>>>((a, b) -> b.compareTo(a)), // 年份降序
                        Collectors.groupingBy(
                                Courses::getSemester,
                                TreeMap::new,
                                Collectors.toList()
                        )
                ));

        // 转换为数组格式
        List<YearData> resultList = groupedMap.entrySet().stream()
                .map(yearEntry -> {
                    YearData yearData = new YearData();
                    yearData.setYear(yearEntry.getKey());
                    yearData.setSemesters(yearEntry.getValue().entrySet().stream()
                            .map(semesterEntry -> {
                                SemesterData semesterData = new SemesterData();
                                semesterData.setSemester(semesterEntry.getKey());
                                semesterData.setCourses(semesterEntry.getValue());
                                return semesterData;
                            })
                            .collect(Collectors.toList()));
                    return yearData;
                })
                .collect(Collectors.toList());

        return Result.success(resultList);
    }

    @Data
    static class YearData {
        private String year;
        private List<SemesterData> semesters;
    }

    @Data
    static class SemesterData {
        private Integer semester;
        private List<Courses> courses;
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String name
                           ) {
        QueryWrapper<Courses> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name),"name",name);
        queryWrapper.orderByDesc("id"); //设置id倒序
        return Result.success(coursesService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }

    // 查询学生关联的课程列表
    @GetMapping("/student/{studentId}")
    public Result getCoursesByStudentId(@PathVariable Long studentId) {
        return Result.success(coursesService.getCoursesByStudentId(studentId));
    }
}

