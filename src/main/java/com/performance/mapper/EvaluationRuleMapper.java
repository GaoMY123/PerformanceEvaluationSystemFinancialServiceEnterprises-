package com.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.performance.entity.EvaluationRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考核评分规则Mapper接口
 */
@Mapper
public interface EvaluationRuleMapper extends BaseMapper<EvaluationRule> {
}
