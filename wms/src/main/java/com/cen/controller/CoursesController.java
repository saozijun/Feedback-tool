package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;

import com.cen.service.ICoursesService;
import com.cen.entity.Courses;

import org.springframework.web.bind.annotation.RestController;

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

    // 全部课程列表
    @GetMapping("/allList")
    public Result allList(){
        return Result.success(coursesService.list());
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
}

