package com.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.performance.config.EncryptedBigDecimalTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 薪酬调整实体类
 */
@Data
@TableName(value = "salary_adjustment", autoResultMap = true)
public class SalaryAdjustment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 考核任务ID */
    private Long taskId;

    /** 员工ID */
    private Long userId;

    /** 员工姓名（非数据库字段） */
    @TableField(exist = false)
    private String userName;

    /** 部门名称（非数据库字段） */
    @TableField(exist = false)
    private String departmentName;

    /** 考核方案ID */
    private Long planId;

    /** 方案名称（非数据库字段） */
    @TableField(exist = false)
    private String planName;

    /** 绩效等级 */
    private String grade;

    /** 考核得分 */
    private BigDecimal finalScore;

    /** 基本薪资（加密存储） */
    @TableField(typeHandler = EncryptedBigDecimalTypeHandler.class)
    private BigDecimal baseSalary;

    /** 调薪比例（%）（加密存储） */
    @TableField(typeHandler = EncryptedBigDecimalTypeHandler.class)
    private BigDecimal adjustmentRate;

    /** 奖金金额（加密存储） */
    @TableField(typeHandler = EncryptedBigDecimalTypeHandler.class)
    private BigDecimal bonusAmount;

    /** 薪酬调整建议 */
    private String suggestion;

    /** 状态：0-待审批 1-已审批 2-已驳回 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
