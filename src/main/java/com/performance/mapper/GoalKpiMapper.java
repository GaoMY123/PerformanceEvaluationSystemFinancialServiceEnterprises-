package com.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.performance.entity.GoalKpi;
import org.apache.ibatis.annotations.Mapper;

/**
 * 目标与KPI关联Mapper接口
 */
@Mapper
public interface GoalKpiMapper extends BaseMapper<GoalKpi> {
}
