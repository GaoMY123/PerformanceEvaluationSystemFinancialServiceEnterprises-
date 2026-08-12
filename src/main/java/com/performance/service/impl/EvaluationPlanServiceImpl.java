package com.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.common.exception.BusinessException;
import com.performance.entity.*;
import com.performance.mapper.*;
import com.performance.security.SecurityUtils;
import com.performance.service.EvaluationPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 考核方案服务实现类
 */
@Service
public class EvaluationPlanServiceImpl extends ServiceImpl<EvaluationPlanMapper, EvaluationPlan> implements EvaluationPlanService {

    @Autowired
    private EvaluationRuleMapper evaluationRuleMapper;

    @Autowired
    private EvaluationTaskMapper evaluationTaskMapper;

    @Autowired
    private EvaluationScoreMapper evaluationScoreMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PeerAssignmentMapper peerAssignmentMapper;

    @Override
    public Page<EvaluationPlan> pageList(Integer pageNum, Integer pageSize, String keyword, Integer year, String periodType, Integer status) {
        Page<EvaluationPlan> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EvaluationPlan> wrapper = new LambdaQueryWrapper<>();
        if (cn.hutool.core.util.StrUtil.isNotBlank(keyword)) {
            wrapper.like(EvaluationPlan::getName, keyword);
        }
        if (year != null) {
            wrapper.eq(EvaluationPlan::getYear, year);
        }
        if (periodType != null && !periodType.isEmpty()) {
            wrapper.eq(EvaluationPlan::getPeriodType, periodType);
        }
        if (status != null) {
            wrapper.eq(EvaluationPlan::getStatus, status);
        }
        wrapper.orderByDesc(EvaluationPlan::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void addPlan(EvaluationPlan plan) {
        plan.setCreatorId(SecurityUtils.getCurrentUserId());
        plan.setStatus(0); // 默认草稿状态
        save(plan);
    }

    @Override
    public void updatePlan(EvaluationPlan plan) {
        EvaluationPlan existing = getById(plan.getId());
        if (existing == null) {
            throw new BusinessException("考核方案不存在");
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的方案才能修改");
        }
        updateById(plan);
    }

    @Override
    public void deletePlan(Long id) {
        EvaluationPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException("考核方案不存在");
        }
        if (plan.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的方案才能删除");
        }
        // 删除关联的规则
        evaluationRuleMapper.delete(new LambdaQueryWrapper<EvaluationRule>().eq(EvaluationRule::getPlanId, id));
        removeById(id);
    }

    @Override
    @Transactional
    public void startPlan(Long id) {
        EvaluationPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException("考核方案不存在");
        }
        if (plan.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的方案才能启动");
        }
        // 检查是否配置了评分规则
        long ruleCount = evaluationRuleMapper.selectCount(
                new LambdaQueryWrapper<EvaluationRule>().eq(EvaluationRule::getPlanId, id));
        if (ruleCount == 0) {
            throw new BusinessException("请先为方案配置评分规则");
        }
        // 为所有启用状态的员工生成考核任务（排除已有任务的员工，防止重复）
        List<EvaluationTask> existingTasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getPlanId, id));
        Set<Long> existingUserIds = existingTasks.stream()
                .map(EvaluationTask::getUserId).collect(Collectors.toSet());
        List<SysUser> users = sysUserMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStatus, 1)
                        .ne(SysUser::getRole, "ADMIN")); // 管理员不参与考核
        for (SysUser user : users) {
            if (existingUserIds.contains(user.getId())) {
                continue; // 该员工已有任务，跳过
            }
            EvaluationTask task = new EvaluationTask();
            task.setPlanId(id);
            task.setUserId(user.getId());
            task.setDepartmentId(user.getDepartmentId());
            task.setStatus(0); // 待自评
            evaluationTaskMapper.insert(task);
        }

        // 自动分配互评对象：同部门内每人分配2个互评对象
        List<EvaluationTask> allTasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getPlanId, id));
        // 按部门分组
        Map<Long, List<EvaluationTask>> deptTaskMap = allTasks.stream()
                .filter(t -> t.getDepartmentId() != null)
                .collect(Collectors.groupingBy(EvaluationTask::getDepartmentId));
        for (Map.Entry<Long, List<EvaluationTask>> entry : deptTaskMap.entrySet()) {
            List<EvaluationTask> deptTasks = entry.getValue();
            if (deptTasks.size() < 2) continue; // 部门人数不足则不分配
            List<Long> userIds = deptTasks.stream().map(EvaluationTask::getUserId).collect(Collectors.toList());
            for (EvaluationTask task : deptTasks) {
                // 从同部门中排除自己，随机选2人作为互评对象
                List<Long> candidates = new ArrayList<>(userIds);
                candidates.remove(task.getUserId());
                Collections.shuffle(candidates);
                int peerCount = Math.min(2, candidates.size());
                for (int i = 0; i < peerCount; i++) {
                    PeerAssignment pa = new PeerAssignment();
                    pa.setPlanId(id);
                    pa.setTaskId(task.getId());
                    pa.setEvaluateeId(task.getUserId());
                    pa.setEvaluatorId(candidates.get(i));
                    pa.setStatus(0);
                    peerAssignmentMapper.insert(pa);
                }
            }
        }

        // 更新方案状态为进行中
        plan.setStatus(1);
        updateById(plan);
    }

    @Override
    @Transactional
    public void completePlan(Long id) {
        EvaluationPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException("考核方案不存在");
        }
        if (plan.getStatus() != 1) {
            throw new BusinessException("只有进行中的方案才能完成");
        }
        // 获取该方案的所有考核任务
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getPlanId, id));
        // 获取评分规则
        List<EvaluationRule> rules = evaluationRuleMapper.selectList(
                new LambdaQueryWrapper<EvaluationRule>().eq(EvaluationRule::getPlanId, id));

        for (EvaluationTask task : tasks) {
            // 计算各维度加权得分
            BigDecimal selfTotal = calculateDimensionScore(task.getId(), "SELF", rules);
            BigDecimal managerTotal = calculateDimensionScore(task.getId(), "MANAGER", rules);
            BigDecimal peerTotal = calculateDimensionScore(task.getId(), "PEER", rules);

            task.setSelfScore(selfTotal);
            task.setManagerScore(managerTotal);
            task.setPeerScore(peerTotal);

            // 按方案配置的维度权重计算最终得分
            BigDecimal finalScore = selfTotal.multiply(plan.getSelfWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                    .add(managerTotal.multiply(plan.getManagerWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                    .add(peerTotal.multiply(plan.getPeerWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            task.setFinalScore(finalScore);

            // 根据分数线确定绩效等级
            if (finalScore.compareTo(plan.getGradeAMin()) >= 0) {
                task.setGrade("A");
            } else if (finalScore.compareTo(plan.getGradeBMin()) >= 0) {
                task.setGrade("B");
            } else if (finalScore.compareTo(plan.getGradeCMin()) >= 0) {
                task.setGrade("C");
            } else {
                task.setGrade("D");
            }
            task.setStatus(3); // 已完成
            evaluationTaskMapper.updateById(task);
        }
        // 更新方案状态为已完成
        plan.setStatus(2);
        updateById(plan);
    }

    /**
     * 计算某维度的加权总分
     * 按评分规则中每个KPI的权重，对该维度的评分进行加权求和
     */
    private BigDecimal calculateDimensionScore(Long taskId, String scoreType, List<EvaluationRule> rules) {
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (EvaluationRule rule : rules) {
            // 查询该维度、该KPI的评分（取平均值，因为可能有多个同事互评）
            List<EvaluationScore> scores = evaluationScoreMapper.selectList(
                    new LambdaQueryWrapper<EvaluationScore>()
                            .eq(EvaluationScore::getTaskId, taskId)
                            .eq(EvaluationScore::getKpiId, rule.getKpiId())
                            .eq(EvaluationScore::getScoreType, scoreType));
            if (!scores.isEmpty()) {
                BigDecimal avgScore = scores.stream()
                        .map(EvaluationScore::getScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
                totalScore = totalScore.add(avgScore.multiply(rule.getWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                totalWeight = totalWeight.add(rule.getWeight());
            }
        }
        // 如果没有任何评分，返回0
        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // 归一化：将实际得分按实际评分的权重比例换算为满分制
        return totalScore.multiply(BigDecimal.valueOf(100)).divide(totalWeight, 2, RoundingMode.HALF_UP);
    }

    @Override
    public void addRule(EvaluationRule rule) {
        // 检查是否已存在相同KPI的规则
        long count = evaluationRuleMapper.selectCount(new LambdaQueryWrapper<EvaluationRule>()
                .eq(EvaluationRule::getPlanId, rule.getPlanId())
                .eq(EvaluationRule::getKpiId, rule.getKpiId()));
        if (count > 0) {
            throw new BusinessException("该方案已配置此KPI的评分规则");
        }
        evaluationRuleMapper.insert(rule);
    }

    @Override
    public List<EvaluationRule> getRules(Long planId) {
        return evaluationRuleMapper.selectList(
                new LambdaQueryWrapper<EvaluationRule>().eq(EvaluationRule::getPlanId, planId));
    }

    @Override
    public void updateRule(EvaluationRule rule) {
        evaluationRuleMapper.updateById(rule);
    }

    @Override
    public void deleteRule(Long id) {
        evaluationRuleMapper.deleteById(id);
    }
}
