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
 * 考核任务实体类（每个被考核人一条记录）
 */
@Data
@TableName("evaluation_task")
public class EvaluationTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 考核方案ID */
    private Long planId;

    /** 方案名称（非数据库字段） */
    @TableField(exist = false)
    private String planName;

    /** 被考核人ID */
    private Long userId;

    /** 被考核人姓名（非数据库字段） */
    @TableField(exist = false)
    private String userName;

    /** 被考核人所属部门ID */
    private Long departmentId;

    /** 部门名称（非数据库字段） */
    @TableField(exist = false)
    private String departmentName;

    /** 自评总分 */
    private BigDecimal selfScore;

    /** 上级评总分 */
    private BigDecimal managerScore;

    /** 同事互评总分 */
    private BigDecimal peerScore;

    /** 最终得分 */
    private BigDecimal finalScore;

    /** 绩效等级：A/B/C/D */
    private String grade;

    /** 状态：0-待自评 1-待上级评 2-待互评 3-已完成 */
    private Integer status;

    /** 备注/评语 */
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
