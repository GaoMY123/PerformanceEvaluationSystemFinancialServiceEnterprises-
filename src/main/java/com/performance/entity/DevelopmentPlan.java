package com.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 个人发展计划（IDP）实体类
 */
@Data
@TableName("development_plan")
public class DevelopmentPlan implements Serializable {

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

    /** 优势分析 */
    private String strengths;

    /** 待改进项 */
    private String weaknesses;

    /** 培训建议 */
    private String trainingSuggestion;

    /** 发展目标 */
    private String developmentGoal;

    /** 行动计划 */
    private String actionPlan;

    /** 状态：0-草稿 1-已确认 2-执行中 3-已完成 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
