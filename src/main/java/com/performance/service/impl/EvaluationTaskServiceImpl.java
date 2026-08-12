package com.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.common.exception.BusinessException;
import com.performance.dto.EvaluationScoreDTO;
import com.performance.entity.*;
import com.performance.mapper.EvaluationAttachmentMapper;
import com.performance.mapper.EvaluationRuleMapper;
import com.performance.mapper.EvaluationScoreMapper;
import com.performance.mapper.EvaluationTaskMapper;
import com.performance.mapper.PeerAssignmentMapper;
import com.performance.mapper.SysUserMapper;
import com.performance.security.SecurityUtils;
import com.performance.service.EvaluationTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考核任务服务实现类
 */
@Service
public class EvaluationTaskServiceImpl extends ServiceImpl<EvaluationTaskMapper, EvaluationTask> implements EvaluationTaskService {

    @Autowired
    private EvaluationScoreMapper evaluationScoreMapper;

    @Autowired
    private EvaluationAttachmentMapper evaluationAttachmentMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PeerAssignmentMapper peerAssignmentMapper;

    @Autowired
    private EvaluationRuleMapper evaluationRuleMapper;

    @Override
    public Page<EvaluationTask> pageList(Integer pageNum, Integer pageSize, Long planId, Long departmentId, Integer status, String keyword) {
        Page<EvaluationTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EvaluationTask> wrapper = new LambdaQueryWrapper<>();
        if (planId != null) {
            wrapper.eq(EvaluationTask::getPlanId, planId);
        }
        if (departmentId != null) {
            wrapper.eq(EvaluationTask::getDepartmentId, departmentId);
        }
        if (status != null) {
            wrapper.eq(EvaluationTask::getStatus, status);
        }
        wrapper.orderByDesc(EvaluationTask::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Page<EvaluationTask> myTasks(Integer pageNum, Integer pageSize, Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<EvaluationTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EvaluationTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EvaluationTask::getUserId, userId);
        if (planId != null) {
            wrapper.eq(EvaluationTask::getPlanId, planId);
        }
        wrapper.orderByDesc(EvaluationTask::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Page<EvaluationTask> managerTasks(Integer pageNum, Integer pageSize, Long planId) {
        Long managerId = SecurityUtils.getCurrentUserId();
        SysUser manager = sysUserMapper.selectById(managerId);
        if (manager == null || manager.getDepartmentId() == null) {
            return new Page<>(pageNum, pageSize);
        }
        // 查询经理所在部门的待上级评任务（排除自己）
        Page<EvaluationTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EvaluationTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EvaluationTask::getDepartmentId, manager.getDepartmentId());
        wrapper.ne(EvaluationTask::getUserId, managerId);
        wrapper.eq(EvaluationTask::getStatus, 1); // 只显示待上级评的任务
        if (planId != null) {
            wrapper.eq(EvaluationTask::getPlanId, planId);
        }
        wrapper.orderByDesc(EvaluationTask::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Page<EvaluationTask> peerTasks(Integer pageNum, Integer pageSize, Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        // 查询分配给当前用户的互评任务
        LambdaQueryWrapper<PeerAssignment> paWrapper = new LambdaQueryWrapper<PeerAssignment>()
                .eq(PeerAssignment::getEvaluatorId, userId)
                .eq(PeerAssignment::getStatus, 0);
        if (planId != null) {
            paWrapper.eq(PeerAssignment::getPlanId, planId);
        }
        List<PeerAssignment> assignments = peerAssignmentMapper.selectList(paWrapper);
        if (assignments.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        // 获取被评价人的任务ID列表
        List<Long> taskIds = assignments.stream().map(PeerAssignment::getTaskId).collect(Collectors.toList());
        Page<EvaluationTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EvaluationTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EvaluationTask::getId, taskIds);
        wrapper.orderByDesc(EvaluationTask::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void submitScore(EvaluationScoreDTO dto) {
        EvaluationTask task = getById(dto.getTaskId());
        if (task == null) {
            throw new BusinessException("考核任务不存在");
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String scoreType = dto.getScoreType();

        // 校验评分权限
        if ("SELF".equals(scoreType)) {
            if (!task.getUserId().equals(currentUserId)) {
                throw new BusinessException("只能对自己进行自评");
            }
            if (task.getStatus() != 0) {
                throw new BusinessException("当前状态不允许自评");
            }
        } else if ("MANAGER".equals(scoreType)) {
            if (task.getStatus() != 1) {
                throw new BusinessException("当前状态不允许上级评价");
            }
        } else if ("PEER".equals(scoreType)) {
            if (task.getUserId().equals(currentUserId)) {
                throw new BusinessException("不能对自己进行同事互评");
            }
            // 校验互评分配权限
            Long assignCount = peerAssignmentMapper.selectCount(
                    new LambdaQueryWrapper<PeerAssignment>()
                            .eq(PeerAssignment::getEvaluatorId, currentUserId)
                            .eq(PeerAssignment::getTaskId, dto.getTaskId())
                            .eq(PeerAssignment::getStatus, 0));
            if (assignCount == 0) {
                throw new BusinessException("您未被分配对该同事进行互评");
            }
        }

        // 删除已有的同类型评分（允许重新评分）
        evaluationScoreMapper.delete(new LambdaQueryWrapper<EvaluationScore>()
                .eq(EvaluationScore::getTaskId, dto.getTaskId())
                .eq(EvaluationScore::getEvaluatorId, currentUserId)
                .eq(EvaluationScore::getScoreType, scoreType));

        // 保存各KPI评分明细
        if (dto.getScores() != null) {
            for (EvaluationScoreDTO.ScoreItem item : dto.getScores()) {
                EvaluationScore score = new EvaluationScore();
                score.setTaskId(dto.getTaskId());
                score.setKpiId(item.getKpiId());
                score.setEvaluatorId(currentUserId);
                score.setEvaluateeId(task.getUserId());
                score.setScoreType(scoreType);
                score.setScore(item.getScore());
                score.setComment(item.getComment());
                evaluationScoreMapper.insert(score);
            }
        }

        // 计算并更新对应维度的加权总分
        List<EvaluationRule> rules = evaluationRuleMapper.selectList(
                new LambdaQueryWrapper<EvaluationRule>().eq(EvaluationRule::getPlanId, task.getPlanId()));
        BigDecimal dimensionScore = calculateDimensionScore(dto.getTaskId(), scoreType, rules);

        // 更新任务状态流转和维度得分
        if ("SELF".equals(scoreType)) {
            task.setSelfScore(dimensionScore);
            if (dto.getRemark() != null) {
                task.setRemark(dto.getRemark());
            }
            // 部门经理自评+上级评合一：直接跳到待互评
            SysUser currentUser = sysUserMapper.selectById(currentUserId);
            if (currentUser != null && "MANAGER".equals(currentUser.getRole())) {
                task.setStatus(2); // 经理自评完成 → 待互评
                task.setManagerScore(dimensionScore); // 自评分同时作为上级评分
            } else {
                task.setStatus(1); // 普通员工自评完成 → 待上级评
            }
            updateById(task);
        } else if ("MANAGER".equals(scoreType)) {
            task.setStatus(2); // 上级评完成 → 待互评
            task.setManagerScore(dimensionScore);
            updateById(task);
        } else if ("PEER".equals(scoreType)) {
            // 互评可能有多人，取所有互评的平均值更新
            BigDecimal peerTotal = calculateDimensionScore(dto.getTaskId(), "PEER", rules);
            task.setPeerScore(peerTotal);
            // 标记互评分配为已完成
            peerAssignmentMapper.update(null, new LambdaUpdateWrapper<PeerAssignment>()
                    .eq(PeerAssignment::getEvaluatorId, currentUserId)
                    .eq(PeerAssignment::getTaskId, dto.getTaskId())
                    .eq(PeerAssignment::getStatus, 0)
                    .set(PeerAssignment::getStatus, 1));
            // 检查该任务的所有互评是否都已完成
            Long pendingPeerCount = peerAssignmentMapper.selectCount(
                    new LambdaQueryWrapper<PeerAssignment>()
                            .eq(PeerAssignment::getTaskId, dto.getTaskId())
                            .eq(PeerAssignment::getStatus, 0));
            if (pendingPeerCount == 0) {
                task.setStatus(3); // 互评全部完成 → 已完成
            }
            updateById(task);
        }
    }

    /**
     * 计算某维度的加权总分
     * 按评分规则中每个KPI的权重，对该维度的评分进行加权求和
     */
    private BigDecimal calculateDimensionScore(Long taskId, String scoreType, List<EvaluationRule> rules) {
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (EvaluationRule rule : rules) {
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
        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return totalScore.multiply(BigDecimal.valueOf(100)).divide(totalWeight, 2, RoundingMode.HALF_UP);
    }

    @Override
    public List<EvaluationScore> getScores(Long taskId, String scoreType) {
        LambdaQueryWrapper<EvaluationScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EvaluationScore::getTaskId, taskId);
        if (scoreType != null && !scoreType.isEmpty()) {
            wrapper.eq(EvaluationScore::getScoreType, scoreType);
        }
        return evaluationScoreMapper.selectList(wrapper);
    }

    @Override
    public void uploadAttachment(EvaluationAttachment attachment) {
        attachment.setUploaderId(SecurityUtils.getCurrentUserId());
        evaluationAttachmentMapper.insert(attachment);
    }

    @Override
    public List<EvaluationAttachment> getAttachments(Long taskId) {
        return evaluationAttachmentMapper.selectList(
                new LambdaQueryWrapper<EvaluationAttachment>().eq(EvaluationAttachment::getTaskId, taskId));
    }

    @Override
    public void deleteAttachment(Long id) {
        evaluationAttachmentMapper.deleteById(id);
    }
}
