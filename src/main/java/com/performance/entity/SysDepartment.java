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
 * 部门实体类
 */
@Data
@TableName("sys_department")
public class SysDepartment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 部门名称 */
    private String name;

    /** 上级部门ID，0表示顶级部门 */
    private Long parentId;

    /** 部门经理用户ID */
    private Long managerId;

    /** 部门经理名称（非数据库字段） */
    @TableField(exist = false)
    private String managerName;

    /** 部门描述 */
    private String description;

    /** 排序号 */
    private Integer sortOrder;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
