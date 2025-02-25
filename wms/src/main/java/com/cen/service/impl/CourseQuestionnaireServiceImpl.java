package com.cen.service.impl;

import com.cen.entity.CourseQuestionnaire;
import com.cen.mapper.CourseQuestionnaireMapper;
import com.cen.service.ICourseQuestionnaireService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.cen.entity.Questionnaires;
import com.cen.mapper.QuestionnairesMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import com.cen.controller.dto.QuestionnaireWithStatusDTO;
import com.cen.enums.QuestionnaireStatus;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author volcano
 * @since 2025-02-24
 */
@Service
public class CourseQuestionnaireServiceImpl extends ServiceImpl<CourseQuestionnaireMapper, CourseQuestionnaire> implements ICourseQuestionnaireService {

    @Resource
    private QuestionnairesMapper questionnairesMapper;
    
    @Override
    public List<QuestionnaireWithStatusDTO> getQuestionnaireByCourseId(Long courseId) {
        // 1. 先查询关联表
        QueryWrapper<CourseQuestionnaire> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        List<CourseQuestionnaire> courseQuestionnaires = this.list(queryWrapper);
        
        // 2. 如果没有关联问卷，返回空列表
        if (courseQuestionnaires.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 3. 获取所有问卷ID
        List<Long> questionnaireIds = courseQuestionnaires.stream()
                .map(CourseQuestionnaire::getQuestionnaireId)
                .collect(Collectors.toList());
                
        // 4. 查询问卷详情
        List<Questionnaires> questionnaires = questionnairesMapper.selectBatchIds(questionnaireIds);
        
        // 5. 创建问卷ID到问卷对象的映射
        Map<Long, Questionnaires> questionnaireMap = questionnaires.stream()
                .collect(Collectors.toMap(Questionnaires::getId, q -> q));
                
        // 6. 组装返回结果
        return courseQuestionnaires.stream()
                .map(cq -> {
                    QuestionnaireWithStatusDTO dto = new QuestionnaireWithStatusDTO();
                    dto.setQuestionnaire(questionnaireMap.get(cq.getQuestionnaireId()));
                    dto.setStatus(cq.getStatus());
                    // 获取状态描述
                    dto.setStatusDescription(Arrays.stream(QuestionnaireStatus.values())
                            .filter(status -> status.getCode() == cq.getStatus())
                            .findFirst()
                            .map(QuestionnaireStatus::getDescription)
                            .orElse("未知状态"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean bindQuestionnaires(Long courseId, String questionnaireIds) {
        // 1. 先删除该课程的所有关联
        QueryWrapper<CourseQuestionnaire> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        this.remove(queryWrapper);
        
        // 2. 如果问卷ID为空，则直接返回true（相当于解除所有关联）
        if (StringUtils.isBlank(questionnaireIds)) {
            return true;
        }
        
        // 3. 将问卷ID字符串转换为List
        List<Long> qIds = Arrays.stream(questionnaireIds.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
                
        // 4. 批量创建新的关联关系，默认状态为待发布
        List<CourseQuestionnaire> relations = qIds.stream()
                .map(qId -> new CourseQuestionnaire(null, courseId, qId, QuestionnaireStatus.PENDING.getCode(), LocalDateTime.now()))
                .collect(Collectors.toList());
                
        // 5. 批量保存
        return this.saveBatch(relations);
    }

    @Override
    @Transactional
    public boolean unbindQuestionnaire(Long courseId, Long questionnaireId) {
        QueryWrapper<CourseQuestionnaire> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId)
                   .eq("questionnaire_id", questionnaireId);
        return this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateStatus(Long courseId, Long questionnaireId, Integer status) {
        // 验证状态值是否有效
        boolean validStatus = Arrays.stream(QuestionnaireStatus.values())
                .anyMatch(s -> s.getCode() == status);
        if (!validStatus) {
            throw new IllegalArgumentException("Invalid status value");
        }

        // 更新状态
        UpdateWrapper<CourseQuestionnaire> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("course_id", courseId)
                    .eq("questionnaire_id", questionnaireId)
                    .set("status", status);
        
        return this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean publishQuestionnaire(Long courseId, Long questionnaireId) {
        // 1. 检查当前状态是否为待发布
        QueryWrapper<CourseQuestionnaire> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId)
                   .eq("questionnaire_id", questionnaireId);
        CourseQuestionnaire questionnaire = this.getOne(queryWrapper);
        
        if (questionnaire == null) {
            throw new IllegalArgumentException("问卷不存在");
        }
        
        if (questionnaire.getStatus() != QuestionnaireStatus.PENDING.getCode()) {
            throw new IllegalArgumentException("只有待发布状态的问卷可以发布");
        }
        
        // 2. 更新状态为进行中
        UpdateWrapper<CourseQuestionnaire> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("course_id", courseId)
                    .eq("questionnaire_id", questionnaireId)
                    .set("status", QuestionnaireStatus.IN_PROGRESS.getCode());
        
        return this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean completeQuestionnaire(Long courseId, Long questionnaireId) {
        // 1. 检查当前状态是否为进行中
        QueryWrapper<CourseQuestionnaire> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId)
                   .eq("questionnaire_id", questionnaireId);
        CourseQuestionnaire questionnaire = this.getOne(queryWrapper);
        
        if (questionnaire == null) {
            throw new IllegalArgumentException("问卷不存在");
        }
        
        if (questionnaire.getStatus() != QuestionnaireStatus.IN_PROGRESS.getCode()) {
            throw new IllegalArgumentException("只有进行中状态的问卷可以结束");
        }
        
        // 2. 更新状态为已完成
        UpdateWrapper<CourseQuestionnaire> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("course_id", courseId)
                    .eq("questionnaire_id", questionnaireId)
                    .set("status", QuestionnaireStatus.COMPLETED.getCode());
        
        return this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean recallQuestionnaire(Long courseId, Long questionnaireId) {
        // 1. 检查当前状态是否为进行中
        QueryWrapper<CourseQuestionnaire> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId)
                   .eq("questionnaire_id", questionnaireId);
        CourseQuestionnaire questionnaire = this.getOne(queryWrapper);
        
        if (questionnaire == null) {
            throw new IllegalArgumentException("问卷不存在");
        }
        
        if (questionnaire.getStatus() != QuestionnaireStatus.IN_PROGRESS.getCode()) {
            throw new IllegalArgumentException("只有进行中状态的问卷可以撤回");
        }
        
        // 2. 更新状态为待发布
        UpdateWrapper<CourseQuestionnaire> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("course_id", courseId)
                    .eq("questionnaire_id", questionnaireId)
                    .set("status", QuestionnaireStatus.PENDING.getCode());
        
        return this.update(updateWrapper);
    }
}
