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
 * 绩效目标实体类（支持公司→部门→个人逐级分解）
 */
@Data
@TableName("performance_goal")
public class PerformanceGoal implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 目标标题 */
    private String title;

    /** 目标描述 */
    private String description;

    /** 目标层级：COMPANY-公司级 DEPARTMENT-部门级 PERSONAL-个人级 */
    private String level;

    /** 上级目标ID，0表示顶级 */
    private Long parentId;

    /** 所属部门ID */
    private Long departmentId;

    /** 部门名称（非数据库字段） */
    @TableField(exist = false)
    private String departmentName;

    /** 责任人ID（个人级） */
    private Long userId;

    /** 责任人姓名（非数据库字段） */
    @TableField(exist = false)
    private String userName;

    /** 年度 */
    private Integer year;

    /** 状态：0-草稿 1-已发布 2-已完成 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
