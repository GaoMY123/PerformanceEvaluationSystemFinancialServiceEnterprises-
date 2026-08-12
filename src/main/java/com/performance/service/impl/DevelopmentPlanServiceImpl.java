package com.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.entity.DevelopmentPlan;
import com.performance.entity.EvaluationTask;
import com.performance.mapper.DevelopmentPlanMapper;
import com.performance.mapper.EvaluationTaskMapper;
import com.performance.service.DevelopmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 个人发展计划（IDP）服务实现类
 */
@Service
public class DevelopmentPlanServiceImpl extends ServiceImpl<DevelopmentPlanMapper, DevelopmentPlan> implements DevelopmentPlanService {

    @Autowired
    private EvaluationTaskMapper evaluationTaskMapper;

    @Override
    public Page<DevelopmentPlan> pageList(Integer pageNum, Integer pageSize, Long planId, Long departmentId, Integer status, String keyword) {
        Page<DevelopmentPlan> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DevelopmentPlan> wrapper = new LambdaQueryWrapper<>();
        if (planId != null) {
            wrapper.eq(DevelopmentPlan::getPlanId, planId);
        }
        if (departmentId != null) {
            // 需要通过userId关联部门，暂由controller层过滤
        }
        if (status != null) {
            wrapper.eq(DevelopmentPlan::getStatus, status);
        }
        wrapper.orderByDesc(DevelopmentPlan::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void generateByPlan(Long planId) {
        // 查询该方案已完成的考核任务
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getPlanId, planId)
                        .eq(EvaluationTask::getStatus, 3)); // 已完成

        for (EvaluationTask task : tasks) {
            // 检查是否已生成过
            long count = count(new LambdaQueryWrapper<DevelopmentPlan>()
                    .eq(DevelopmentPlan::getTaskId, task.getId()));
            if (count > 0) {
                continue;
            }

            DevelopmentPlan plan = new DevelopmentPlan();
            plan.setTaskId(task.getId());
            plan.setUserId(task.getUserId());
            plan.setPlanId(planId);
            plan.setStatus(0); // 草稿

            // 根据绩效等级生成不同的培训建议
            switch (task.getGrade()) {
                case "A":
                    plan.setStrengths("综合表现优秀，各项KPI指标均达到或超过目标");
                    plan.setTrainingSuggestion("建议参加高级管理培训、行业峰会，培养领导力");
                    plan.setDevelopmentGoal("向管理岗位或专家岗位发展");
                    break;
                case "B":
                    plan.setStrengths("整体表现良好，大部分指标达到目标");
                    plan.setWeaknesses("部分指标仍有提升空间");
                    plan.setTrainingSuggestion("建议参加专业技能深化培训，提升薄弱项");
                    plan.setDevelopmentGoal("巩固现有能力，提升短板项至优秀水平");
                    break;
                case "C":
                    plan.setStrengths("基本能完成工作任务");
                    plan.setWeaknesses("多项指标未达预期，需要重点改进");
                    plan.setTrainingSuggestion("建议参加基础技能培训和合规培训，加强指导带教");
                    plan.setDevelopmentGoal("尽快提升至合格以上水平，缩小与团队平均的差距");
                    break;
                case "D":
                    plan.setWeaknesses("综合绩效不达标，多项指标存在明显不足");
                    plan.setTrainingSuggestion("建议安排系统性培训，指定导师一对一辅导");
                    plan.setDevelopmentGoal("三个月内各项核心指标达到合格标准");
                    plan.setActionPlan("1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训");
                    break;
                default:
                    plan.setTrainingSuggestion("待评估");
            }
            save(plan);
        }
    }

    @Override
    public void updatePlan(DevelopmentPlan plan) {
        updateById(plan);
    }

    @Override
    public void addPlan(DevelopmentPlan plan) {
        save(plan);
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        DevelopmentPlan plan = getById(id);
        if (plan != null) {
            plan.setStatus(status);
            updateById(plan);
        }
    }

    @Override
    public Page<DevelopmentPlan> departmentPlans(Integer pageNum, Integer pageSize, Long departmentId, Integer status) {
        Page<DevelopmentPlan> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DevelopmentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.inSql(DevelopmentPlan::getUserId, "SELECT id FROM sys_user WHERE department_id = " + departmentId);
        if (status != null) {
            wrapper.eq(DevelopmentPlan::getStatus, status);
        }
        wrapper.orderByDesc(DevelopmentPlan::getCreateTime);
        return page(page, wrapper);
    }
}
