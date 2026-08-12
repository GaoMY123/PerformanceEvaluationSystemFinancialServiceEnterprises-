package com.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * KPI指标库实体类（含金融行业特有指标）
 */
@Data
@TableName("kpi_indicator")
public class KpiIndicator implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 指标名称 */
    private String name;

    /** 指标分类：FINANCIAL-财务类 COMPLIANCE-合规类 RISK-风控类 CUSTOMER-客户类 OPERATION-运营类 */
    private String category;

    /** 指标描述 */
    private String description;

    /** 计算公式 */
    private String formula;

    /** 单位 */
    private String unit;

    /** 目标值 */
    private BigDecimal targetValue;

    /** 最高分 */
    private BigDecimal maxScore;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
