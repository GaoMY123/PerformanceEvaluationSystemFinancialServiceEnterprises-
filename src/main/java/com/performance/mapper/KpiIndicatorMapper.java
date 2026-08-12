package com.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.performance.entity.KpiIndicator;
import org.apache.ibatis.annotations.Mapper;

/**
 * KPI指标库Mapper接口
 */
@Mapper
public interface KpiIndicatorMapper extends BaseMapper<KpiIndicator> {
}
