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
 * 
 * </p>
 *
 * @author volcano
 * @since 2025-02-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_courses")
public class Courses implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 课程名称
    private String name;

    // 课程代码
    private String code;

    // 授课教师ID
    private Long teacherId;

    // 学年
    private String academicYear;

    // 学期
    private Integer semester;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;
}
