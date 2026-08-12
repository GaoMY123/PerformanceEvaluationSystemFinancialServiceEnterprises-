package com.performance.controller;

import com.performance.common.Result;
import com.performance.entity.SysUser;
import com.performance.security.SecurityUtils;
import com.performance.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据仪表盘控制器 - 为ECharts图表提供数据接口
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /** 系统总览数据（仅ADMIN/HR） */
    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<Map<String, Object>> overview() {
        return Result.success(dashboardService.getOverview());
    }

    /** 部门总览数据（MANAGER视角） */
    @GetMapping("/departmentOverview")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<Map<String, Object>> departmentOverview() {
        SysUser user = SecurityUtils.getCurrentUser();
        if (user == null || user.getDepartmentId() == null) {
            return Result.error("未关联部门");
        }
        return Result.success(dashboardService.getDepartmentOverview(user.getDepartmentId()));
    }

    /** 当前用户个人总览数据 */
    @GetMapping("/myOverview")
    public Result<Map<String, Object>> myOverview() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(dashboardService.getMyOverview(userId));
    }

    /** 绩效等级分布（饼图数据） */
    @GetMapping("/gradeDistribution")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public Result<Map<String, Object>> gradeDistribution(@RequestParam Long planId) {
        return Result.success(dashboardService.getGradeDistribution(planId));
    }

    /** 部门平均分（柱状图数据） */
    @GetMapping("/departmentAvgScore")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public Result<Map<String, Object>> departmentAvgScore(@RequestParam Long planId) {
        return Result.success(dashboardService.getDepartmentAvgScore(planId));
    }

    /** 个人绩效雷达图数据 */
    @GetMapping("/personalRadar")
    public Result<Map<String, Object>> personalRadar(@RequestParam Long taskId) {
        return Result.success(dashboardService.getPersonalRadar(taskId));
    }

    /** 个人绩效趋势（折线图数据） */
    @GetMapping("/scoreTrend")
    public Result<Map<String, Object>> scoreTrend(
            @RequestParam Long userId,
            @RequestParam Integer year) {
        return Result.success(dashboardService.getScoreTrend(userId, year));
    }

    /** 部门排名数据 */
    @GetMapping("/departmentRanking")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public Result<Map<String, Object>> departmentRanking(@RequestParam Long planId) {
        return Result.success(dashboardService.getDepartmentRanking(planId));
    }

    /** 多维度趋势分析 */
    @GetMapping("/multiDimensionTrend")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public Result<Map<String, Object>> multiDimensionTrend(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String periodType,
            @RequestParam(required = false) Long departmentId) {
        return Result.success(dashboardService.getMultiDimensionTrend(year, periodType, departmentId));
    }
}
