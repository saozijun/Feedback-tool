package com.cen.service;

import com.cen.entity.CourseQuestionnaire;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cen.entity.Questionnaires;
import com.cen.controller.dto.QuestionnaireWithStatusDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author volcano
 * @since 2025-02-24
 */
public interface ICourseQuestionnaireService extends IService<CourseQuestionnaire> {
    List<QuestionnaireWithStatusDTO> getQuestionnaireByCourseId(Long courseId);
    
    boolean bindQuestionnaires(Long courseId, String questionnaireIds);
    
    boolean unbindQuestionnaire(Long courseId, Long questionnaireId);

    // 更新问卷状态
    boolean updateStatus(Long courseId, Long questionnaireId, Integer status);

    // 发布问卷
    boolean publishQuestionnaire(Long courseId, Long questionnaireId);
    
    // 结束问卷
    boolean completeQuestionnaire(Long courseId, Long questionnaireId);

    // 撤回问卷
    boolean recallQuestionnaire(Long courseId, Long questionnaireId);
}
