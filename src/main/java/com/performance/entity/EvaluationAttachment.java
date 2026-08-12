package com.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考核附件实体类（证明材料）
 */
@Data
@TableName("evaluation_attachment")
public class EvaluationAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 考核任务ID */
    private Long taskId;

    /** 文件名 */
    private String fileName;

    /** 文件存储路径 */
    private String filePath;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 上传人ID */
    private Long uploaderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
