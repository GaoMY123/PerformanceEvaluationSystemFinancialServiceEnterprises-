package com.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.performance.entity.PeerAssignment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 互评分配Mapper接口
 */
@Mapper
public interface PeerAssignmentMapper extends BaseMapper<PeerAssignment> {
}
