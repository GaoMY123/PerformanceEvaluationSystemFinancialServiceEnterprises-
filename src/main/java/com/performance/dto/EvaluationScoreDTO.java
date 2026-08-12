package com.performance.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 提交评分请求DTO
 */
@Data
public class EvaluationScoreDTO {

    /** 考核任务ID */
    @NotNull(message = "考核任务ID不能为空")
    private Long taskId;

    /** 评分类型：SELF-自评 MANAGER-上级评 PEER-同事互评 */
    @NotNull(message = "评分类型不能为空")
    private String scoreType;

    /** 总体评语 */
    private String remark;

    /** 各KPI的评分明细 */
    private List<ScoreItem> scores;

    @Data
    public static class ScoreItem {
        /** KPI指标ID */
        private Long kpiId;
        /** 评分 */
        private BigDecimal score;
        /** 评语 */
        private String comment;
    }
}
