package com.performance.service;

import java.util.Map;

/**
 * 数据仪表盘服务接口 - 为ECharts提供数据
 */
public interface DashboardService {

    /** 获取系统总览数据（用户数、部门数、方案数等） */
    Map<String, Object> getOverview();

    /** 获取当前用户个人总览数据 */
    Map<String, Object> getMyOverview(Long userId);

    /** 获取绩效等级分布数据（饼图） */
    Map<String, Object> getGradeDistribution(Long planId);

    /** 获取部门平均分数据（柱状图/趋势图） */
    Map<String, Object> getDepartmentAvgScore(Long planId);

    /** 获取个人绩效雷达图数据 */
    Map<String, Object> getPersonalRadar(Long taskId);

    /** 获取绩效趋势数据（折线图，按时间维度） */
    Map<String, Object> getScoreTrend(Long userId, Integer year);

    /** 获取部门排名数据 */
    Map<String, Object> getDepartmentRanking(Long planId);

    /** 获取部门总览数据（部门经理视角） */
    Map<String, Object> getDepartmentOverview(Long departmentId);

    /** 多维度趋势分析：按部门+时间周期 */
    Map<String, Object> getMultiDimensionTrend(Integer year, String periodType, Long departmentId);
}
