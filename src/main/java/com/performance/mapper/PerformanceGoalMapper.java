package com.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.performance.entity.PerformanceGoal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 绩效目标Mapper接口
 */
@Mapper
public interface PerformanceGoalMapper extends BaseMapper<PerformanceGoal> {
}
