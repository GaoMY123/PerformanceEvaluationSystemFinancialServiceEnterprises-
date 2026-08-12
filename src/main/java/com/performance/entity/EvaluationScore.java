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
 * 考核评分明细实体类
 */
@Data
@TableName("evaluation_score")
public class EvaluationScore implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 考核任务ID */
    private Long taskId;

    /** KPI指标ID */
    private Long kpiId;

    /** KPI指标名称（非数据库字段） */
    @TableField(exist = false)
    private String kpiName;

    /** 评分人ID */
    private Long evaluatorId;

    /** 评分人姓名（非数据库字段） */
    @TableField(exist = false)
    private String evaluatorName;

    /** 被评分人ID */
    private Long evaluateeId;

    /** 评分类型：SELF-自评 MANAGER-上级评 PEER-同事互评 */
    private String scoreType;

    /** 评分 */
    private BigDecimal score;

    /** 评语 */
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
