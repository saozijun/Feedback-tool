package com.cen.service;

import com.cen.entity.QuestionnaireResponses;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cen.controller.dto.QuestionnaireResponseDetailDTO;
import java.util.List;

/**
 * <p>
 * 问卷答案表 服务类
 * </p>
 *
 * @author volcano
 * @since 2025-02-26
 */
public interface IQuestionnaireResponsesService extends IService<QuestionnaireResponses> {
    // 获取课程问卷的所有填写记录
    List<QuestionnaireResponseDetailDTO> getCourseQuestionnaireResponses(Long courseId, Long questionnaireId);
}
