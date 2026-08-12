package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.entity.GoalKpi;
import com.performance.entity.PerformanceGoal;

import java.util.List;

/**
 * 绩效目标服务接口
 */
public interface PerformanceGoalService extends IService<PerformanceGoal> {

    /** 分页查询绩效目标 */
    Page<PerformanceGoal> pageList(Integer pageNum, Integer pageSize, String keyword, String level, Integer year, Long departmentId, Integer status);

    /** 获取目标的子目标列表 */
    List<PerformanceGoal> getChildren(Long parentId);

    /** 新增目标 */
    void addGoal(PerformanceGoal goal);

    /** 更新目标 */
    void updateGoal(PerformanceGoal goal);

    /** 删除目标 */
    void deleteGoal(Long id);

    /** 为目标关联KPI指标 */
    void bindKpi(GoalKpi goalKpi);

    /** 获取目标关联的KPI列表 */
    List<GoalKpi> getGoalKpis(Long goalId);

    /** 删除目标与KPI的关联 */
    void unbindKpi(Long id);
}
