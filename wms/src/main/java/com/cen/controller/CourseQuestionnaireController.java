package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;

import com.cen.service.ICourseQuestionnaireService;
import com.cen.entity.CourseQuestionnaire;

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
@RequestMapping("/courseQuestionnaire")
public class CourseQuestionnaireController {

    @Resource
    private ICourseQuestionnaireService courseQuestionnaireService;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody CourseQuestionnaire courseQuestionnaire) {
        return Result.success(courseQuestionnaireService.saveOrUpdate(courseQuestionnaire));
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody CourseQuestionnaire courseQuestionnaire){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(courseQuestionnaireService.removeById(courseQuestionnaire.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(courseQuestionnaireService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable CourseQuestionnaire courseQuestionnaire) {
        return Result.success(courseQuestionnaireService.getById(courseQuestionnaire.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<CourseQuestionnaire> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id"); //设置id倒序
        return Result.success(courseQuestionnaireService.page(new Page<>(pageNum, pageSize)));
    }
}

