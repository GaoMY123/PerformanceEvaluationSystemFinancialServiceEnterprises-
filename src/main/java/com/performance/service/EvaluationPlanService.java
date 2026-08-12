package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.entity.EvaluationPlan;
import com.performance.entity.EvaluationRule;

import java.util.List;

/**
 * 考核方案服务接口
 */
public interface EvaluationPlanService extends IService<EvaluationPlan> {

    /** 分页查询考核方案 */
    Page<EvaluationPlan> pageList(Integer pageNum, Integer pageSize, String keyword, Integer year, String periodType, Integer status);

    /** 新增考核方案 */
    void addPlan(EvaluationPlan plan);

    /** 更新考核方案 */
    void updatePlan(EvaluationPlan plan);

    /** 删除考核方案 */
    void deletePlan(Long id);

    /** 启动考核方案（生成考核任务） */
    void startPlan(Long id);

    /** 完成考核方案（汇总计算最终得分和等级） */
    void completePlan(Long id);

    /** 为考核方案添加评分规则 */
    void addRule(EvaluationRule rule);

    /** 获取考核方案的评分规则列表 */
    List<EvaluationRule> getRules(Long planId);

    /** 更新评分规则 */
    void updateRule(EvaluationRule rule);

    /** 删除评分规则 */
    void deleteRule(Long id);
}
