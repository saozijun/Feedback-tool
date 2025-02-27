package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;

import com.cen.service.IQuestionnaireResponsesService;
import com.cen.entity.QuestionnaireResponses;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 问卷答案表 前端控制器
 * </p>
 *
 * @author volcano
 * @since 2025-02-26
 */
@RestController
@RequestMapping("/questionnaireResponses")
public class QuestionnaireResponsesController {

    @Resource
    private IQuestionnaireResponsesService questionnaireResponsesService;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody QuestionnaireResponses questionnaireResponses) {
        return Result.success(questionnaireResponsesService.saveOrUpdate(questionnaireResponses));
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody QuestionnaireResponses questionnaireResponses){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(questionnaireResponsesService.removeById(questionnaireResponses.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(questionnaireResponsesService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable QuestionnaireResponses questionnaireResponses) {
        return Result.success(questionnaireResponsesService.getById(questionnaireResponses.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<QuestionnaireResponses> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id"); //设置id倒序
        return Result.success(questionnaireResponsesService.page(new Page<>(pageNum, pageSize)));
    }
    // 查询课程问卷的填写情况列表
    @GetMapping("/FillinDetails")
    public Result getCourseQuestionnaireResponses(
            @RequestParam Long courseId,
            @RequestParam Long questionnaireId) {
        return Result.success(questionnaireResponsesService.getCourseQuestionnaireResponses(courseId, questionnaireId));
    }
}

