package com.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.performance.entity.EvaluationScore;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考核评分明细Mapper接口
 */
@Mapper
public interface EvaluationScoreMapper extends BaseMapper<EvaluationScore> {
}
