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
 * 绩效申诉实体类
 */
@Data
@TableName("appeal")
public class Appeal implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 考核任务ID */
    private Long taskId;

    /** 申诉人ID */
    private Long userId;

    /** 申诉人姓名（非数据库字段） */
    @TableField(exist = false)
    private String userName;

    /** 申诉人部门名称（非数据库字段） */
    @TableField(exist = false)
    private String departmentName;

    /** 考核方案名称（非数据库字段） */
    @TableField(exist = false)
    private String planName;

    /** 申诉原因 */
    private String reason;

    /** 状态：0-待处理 1-处理中 2-已通过 3-已驳回 */
    private Integer status;

    /** 回复内容 */
    private String reply;

    /** 处理人ID */
    private Long handlerId;

    /** 处理人姓名（非数据库字段） */
    @TableField(exist = false)
    private String handlerName;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
