package com.performance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.common.exception.BusinessException;
import com.performance.entity.GoalKpi;
import com.performance.entity.PerformanceGoal;
import com.performance.mapper.GoalKpiMapper;
import com.performance.mapper.PerformanceGoalMapper;
import com.performance.service.PerformanceGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 绩效目标服务实现类
 */
@Service
public class PerformanceGoalServiceImpl extends ServiceImpl<PerformanceGoalMapper, PerformanceGoal> implements PerformanceGoalService {

    @Autowired
    private GoalKpiMapper goalKpiMapper;

    @Override
    public Page<PerformanceGoal> pageList(Integer pageNum, Integer pageSize, String keyword, String level, Integer year, Long departmentId, Integer status) {
        Page<PerformanceGoal> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PerformanceGoal> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(PerformanceGoal::getTitle, keyword);
        }
        if (StrUtil.isNotBlank(level)) {
            wrapper.eq(PerformanceGoal::getLevel, level);
        }
        if (year != null) {
            wrapper.eq(PerformanceGoal::getYear, year);
        }
        if (departmentId != null) {
            wrapper.eq(PerformanceGoal::getDepartmentId, departmentId);
        }
        if (status != null) {
            wrapper.eq(PerformanceGoal::getStatus, status);
        }
        wrapper.orderByDesc(PerformanceGoal::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public List<PerformanceGoal> getChildren(Long parentId) {
        return list(new LambdaQueryWrapper<PerformanceGoal>()
                .eq(PerformanceGoal::getParentId, parentId)
                .orderByDesc(PerformanceGoal::getCreateTime));
    }

    @Override
    public void addGoal(PerformanceGoal goal) {
        save(goal);
    }

    @Override
    public void updateGoal(PerformanceGoal goal) {
        updateById(goal);
    }

    @Override
    public void deleteGoal(Long id) {
        // 检查是否有子目标
        long childCount = count(new LambdaQueryWrapper<PerformanceGoal>().eq(PerformanceGoal::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("该目标下存在子目标，无法删除");
        }
        // 删除关联的KPI
        goalKpiMapper.delete(new LambdaQueryWrapper<GoalKpi>().eq(GoalKpi::getGoalId, id));
        removeById(id);
    }

    @Override
    public void bindKpi(GoalKpi goalKpi) {
        // 检查是否已关联
        long count = goalKpiMapper.selectCount(new LambdaQueryWrapper<GoalKpi>()
                .eq(GoalKpi::getGoalId, goalKpi.getGoalId())
                .eq(GoalKpi::getKpiId, goalKpi.getKpiId()));
        if (count > 0) {
            throw new BusinessException("该目标已关联此KPI指标");
        }
        goalKpiMapper.insert(goalKpi);
    }

    @Override
    public List<GoalKpi> getGoalKpis(Long goalId) {
        return goalKpiMapper.selectList(new LambdaQueryWrapper<GoalKpi>().eq(GoalKpi::getGoalId, goalId));
    }

    @Override
    public void unbindKpi(Long id) {
        goalKpiMapper.deleteById(id);
    }
}
