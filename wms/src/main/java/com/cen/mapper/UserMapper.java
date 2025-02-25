package com.cen.mapper;

import com.cen.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author volcano
 * @since 2025-02-17
 */
public interface UserMapper extends BaseMapper<User> {
    IPage<User> getStudentsByCourseId(Page<User> page, Long courseId, String nickname);
}
