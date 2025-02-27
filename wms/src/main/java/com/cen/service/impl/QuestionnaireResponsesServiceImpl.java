package com.cen.service.impl;

import com.cen.entity.QuestionnaireResponses;
import com.cen.mapper.QuestionnaireResponsesMapper;
import com.cen.service.IQuestionnaireResponsesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.cen.controller.dto.QuestionnaireResponseDetailDTO;
import com.cen.entity.User;
import com.cen.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import javax.annotation.Resource;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 问卷答案表 服务实现类
 * </p>
 *
 * @author volcano
 * @since 2025-02-26
 */
@Service
public class QuestionnaireResponsesServiceImpl extends ServiceImpl<QuestionnaireResponsesMapper, QuestionnaireResponses> implements IQuestionnaireResponsesService {

    @Resource
    private UserMapper userMapper;
    
    @Override
    public List<QuestionnaireResponseDetailDTO> getCourseQuestionnaireResponses(Long courseId, Long questionnaireId) {
        // 1. 查询该课程问卷的所有答案记录
        QueryWrapper<QuestionnaireResponses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId)
                   .eq("questionnaire_id", questionnaireId)
                   .orderByDesc("submitted_at");  // 按提交时间倒序
        List<QuestionnaireResponses> responses = this.list(queryWrapper);
        
        if (responses.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 2. 获取所有学生ID
        List<Long> studentIds = responses.stream()
                .map(QuestionnaireResponses::getStudentId)
                .distinct()
                .collect(Collectors.toList());
                
        // 3. 批量查询学生信息
        List<User> students = userMapper.selectBatchIds(studentIds);
        Map<Long, User> studentMap = students.stream()
                .collect(Collectors.toMap(User::getId, student -> student));
                
        // 4. 组装返回数据
        return responses.stream().map(response -> {
            QuestionnaireResponseDetailDTO dto = new QuestionnaireResponseDetailDTO();
            dto.setResponse(response);
            dto.setStudent(studentMap.get(response.getStudentId()));
            return dto;
        }).collect(Collectors.toList());
    }
}
