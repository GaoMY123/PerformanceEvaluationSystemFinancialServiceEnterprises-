package com.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.common.exception.BusinessException;
import com.performance.entity.EvaluationTask;
import com.performance.entity.SalaryAdjustment;
import com.performance.mapper.EvaluationTaskMapper;
import com.performance.mapper.SalaryAdjustmentMapper;
import com.performance.service.SalaryAdjustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 薪酬调整服务实现类
 */
@Service
public class SalaryAdjustmentServiceImpl extends ServiceImpl<SalaryAdjustmentMapper, SalaryAdjustment> implements SalaryAdjustmentService {

    @Autowired
    private EvaluationTaskMapper evaluationTaskMapper;

    @Override
    public Page<SalaryAdjustment> pageList(Integer pageNum, Integer pageSize, Long planId, Long departmentId, Integer status, String keyword) {
        Page<SalaryAdjustment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SalaryAdjustment> wrapper = new LambdaQueryWrapper<>();
        if (planId != null) {
            wrapper.eq(SalaryAdjustment::getPlanId, planId);
        }
        if (departmentId != null) {
            // 需要通过userId关联查询部门，这里先简单按userId过滤
            // 实际需要子查询，暂时跳过，由controller层过滤
        }
        if (status != null) {
            wrapper.eq(SalaryAdjustment::getStatus, status);
        }
        wrapper.orderByDesc(SalaryAdjustment::getCreateTime);
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
            long count = count(new LambdaQueryWrapper<SalaryAdjustment>()
                    .eq(SalaryAdjustment::getTaskId, task.getId()));
            if (count > 0) {
                continue; // 跳过已生成的
            }

            SalaryAdjustment adjustment = new SalaryAdjustment();
            adjustment.setTaskId(task.getId());
            adjustment.setUserId(task.getUserId());
            adjustment.setPlanId(planId);
            adjustment.setGrade(task.getGrade());
            adjustment.setFinalScore(task.getFinalScore());
            adjustment.setStatus(0); // 待审批

            // 根据绩效等级自动生成调薪比例和奖金建议
            switch (task.getGrade()) {
                case "A":
                    adjustment.setAdjustmentRate(new BigDecimal("15.00"));
                    adjustment.setSuggestion("绩效优秀，建议大幅调薪并发放年终奖金");
                    break;
                case "B":
                    adjustment.setAdjustmentRate(new BigDecimal("8.00"));
                    adjustment.setSuggestion("绩效良好，建议适度调薪并发放奖金");
                    break;
                case "C":
                    adjustment.setAdjustmentRate(new BigDecimal("3.00"));
                    adjustment.setSuggestion("绩效合格，建议小幅调薪");
                    break;
                case "D":
                    adjustment.setAdjustmentRate(new BigDecimal("0.00"));
                    adjustment.setSuggestion("绩效不合格，建议维持薪资并制定改进计划");
                    break;
                default:
                    adjustment.setAdjustmentRate(BigDecimal.ZERO);
                    adjustment.setSuggestion("待评估");
            }
            save(adjustment);
        }
    }

    @Override
    public void updateAdjustment(SalaryAdjustment adjustment) {
        SalaryAdjustment existing = getById(adjustment.getId());
        if (existing == null) {
            throw new BusinessException("薪酬调整记录不存在");
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException("只有待审批状态才能修改");
        }
        updateById(adjustment);
    }

    @Override
    public void approve(Long id, Integer status) {
        SalaryAdjustment adjustment = getById(id);
        if (adjustment == null) {
            throw new BusinessException("薪酬调整记录不存在");
        }
        if (adjustment.getStatus() != 0) {
            throw new BusinessException("只有待审批状态才能审批");
        }
        adjustment.setStatus(status);
        updateById(adjustment);
    }

    @Override
    public Page<SalaryAdjustment> departmentPageList(Integer pageNum, Integer pageSize, Long departmentId, Integer status) {
        Page<SalaryAdjustment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SalaryAdjustment> wrapper = new LambdaQueryWrapper<>();
        wrapper.inSql(SalaryAdjustment::getUserId, "SELECT id FROM sys_user WHERE department_id = " + departmentId);
        if (status != null) {
            wrapper.eq(SalaryAdjustment::getStatus, status);
        }
        wrapper.orderByDesc(SalaryAdjustment::getCreateTime);
        return page(page, wrapper);
    }
}
