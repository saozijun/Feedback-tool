package com.cen.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 问卷答案表
 * </p>
 *
 * @author volcano
 * @since 2025-02-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_questionnaire_responses")
public class QuestionnaireResponses implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 课程ID
    private Long courseId;

    // 问卷ID
    private Long questionnaireId;

    // 学生ID
    private Long studentId;

    // 答案内容（JSON格式存储）
    private String answers;

    // 提交时间
    private LocalDateTime submittedAt;
}
