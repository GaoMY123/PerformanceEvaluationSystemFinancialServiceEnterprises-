package com.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考核方案实体类
 */
@Data
@TableName("evaluation_plan")
public class EvaluationPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 方案名称 */
    private String name;

    /** 年度 */
    private Integer year;

    /** 考核周期：MONTHLY-月度 QUARTERLY-季度 ANNUALLY-年度 */
    private String periodType;

    /** 周期编号 */
    private Integer periodNumber;

    /** 考核开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /** 考核结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /** A等级最低分 */
    private BigDecimal gradeAMin;

    /** B等级最低分 */
    private BigDecimal gradeBMin;

    /** C等级最低分 */
    private BigDecimal gradeCMin;

    /** 自评权重（%） */
    private BigDecimal selfWeight;

    /** 上级评权重（%） */
    private BigDecimal managerWeight;

    /** 同事互评权重（%） */
    private BigDecimal peerWeight;

    /** 状态：0-草稿 1-进行中 2-已完成 */
    private Integer status;

    /** 创建人ID */
    private Long creatorId;

    /** 创建人姓名（非数据库字段） */
    @TableField(exist = false)
    private String creatorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
