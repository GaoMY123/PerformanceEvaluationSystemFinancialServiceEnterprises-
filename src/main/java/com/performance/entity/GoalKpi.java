package com.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 目标与KPI指标关联实体类
 */
@Data
@TableName("goal_kpi")
public class GoalKpi implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 目标ID */
    private Long goalId;

    /** KPI指标ID */
    private Long kpiId;

    /** KPI指标名称（非数据库字段） */
    @TableField(exist = false)
    private String kpiName;

    /** 该目标下的指标目标值 */
    private BigDecimal targetValue;

    /** 权重（百分比） */
    private BigDecimal weight;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
