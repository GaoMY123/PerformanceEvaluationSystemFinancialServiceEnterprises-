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
 * 互评分配实体类（记录谁评价谁）
 */
@Data
@TableName("peer_assignment")
public class PeerAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 考核方案ID */
    private Long planId;

    /** 被考核人的任务ID */
    private Long taskId;

    /** 被评价人ID */
    private Long evaluateeId;

    /** 互评人ID */
    private Long evaluatorId;

    /** 状态：0-待互评 1-已完成 */
    private Integer status;

    /** 评价人姓名（非数据库字段） */
    @TableField(exist = false)
    private String evaluatorName;

    /** 被评价人姓名（非数据库字段） */
    @TableField(exist = false)
    private String evaluateeName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
