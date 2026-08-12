package com.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.performance.entity.DevelopmentPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 个人发展计划Mapper接口
 */
@Mapper
public interface DevelopmentPlanMapper extends BaseMapper<DevelopmentPlan> {
}
