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
 * 考核评分规则实体类（方案与KPI的权重配置）
 */
@Data
@TableName("evaluation_rule")
public class EvaluationRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 考核方案ID */
    private Long planId;

    /** KPI指标ID */
    private Long kpiId;

    /** KPI指标名称（非数据库字段） */
    @TableField(exist = false)
    private String kpiName;

    /** 该指标在方案中的权重（%） */
    private BigDecimal weight;

    /** 目标值 */
    private BigDecimal targetValue;

    /** 评分方式：WEIGHTED_SUM-加权求和 PERCENTILE-百分位排名 */
    private String scoringFormula;

    /** 该指标最高分 */
    private BigDecimal maxScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
