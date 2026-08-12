package com.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.performance.entity.EvaluationTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考核任务Mapper接口
 */
@Mapper
public interface EvaluationTaskMapper extends BaseMapper<EvaluationTask> {
}
