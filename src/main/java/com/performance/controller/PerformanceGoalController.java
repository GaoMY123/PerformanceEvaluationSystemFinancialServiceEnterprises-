package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.entity.GoalKpi;
import com.performance.entity.KpiIndicator;
import com.performance.entity.PerformanceGoal;
import com.performance.entity.SysDepartment;
import com.performance.entity.SysUser;
import com.performance.service.KpiIndicatorService;
import com.performance.service.PerformanceGoalService;
import com.performance.service.SysDepartmentService;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 绩效目标管理控制器（支持公司→部门→个人逐级分解）
 */
@RestController
@RequestMapping("/api/goal")
public class PerformanceGoalController {

    @Autowired
    private PerformanceGoalService performanceGoalService;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private KpiIndicatorService kpiIndicatorService;

    /** 分页查询绩效目标 */
    @GetMapping("/page")
    public Result<Page<PerformanceGoal>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status) {
        Page<PerformanceGoal> page = performanceGoalService.pageList(pageNum, pageSize, keyword, level, year, departmentId, status);
        // 填充部门名称和责任人姓名
        page.getRecords().forEach(this::fillGoalInfo);
        return Result.success(page);
    }

    /** 获取子目标列表 */
    @GetMapping("/children/{parentId}")
    public Result<List<PerformanceGoal>> getChildren(@PathVariable Long parentId) {
        List<PerformanceGoal> list = performanceGoalService.getChildren(parentId);
        list.forEach(this::fillGoalInfo);
        return Result.success(list);
    }

    /** 根据ID获取目标详情 */
    @GetMapping("/{id}")
    public Result<PerformanceGoal> getById(@PathVariable Long id) {
        PerformanceGoal goal = performanceGoalService.getById(id);
        if (goal != null) {
            fillGoalInfo(goal);
        }
        return Result.success(goal);
    }

    /** 新增绩效目标 */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @OperationLog("新增绩效目标")
    public Result<?> add(@RequestBody PerformanceGoal goal) {
        performanceGoalService.addGoal(goal);
        return Result.success("新增成功");
    }

    /** 更新绩效目标 */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @OperationLog("更新绩效目标")
    public Result<?> update(@RequestBody PerformanceGoal goal) {
        performanceGoalService.updateGoal(goal);
        return Result.success("更新成功");
    }

    /** 删除绩效目标 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @OperationLog("删除绩效目标")
    public Result<?> delete(@PathVariable Long id) {
        performanceGoalService.deleteGoal(id);
        return Result.success("删除成功");
    }

    /** 为目标关联KPI指标 */
    @PostMapping("/bindKpi")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @OperationLog("关联KPI指标")
    public Result<?> bindKpi(@RequestBody GoalKpi goalKpi) {
        performanceGoalService.bindKpi(goalKpi);
        return Result.success("关联成功");
    }

    /** 获取目标关联的KPI列表 */
    @GetMapping("/kpis/{goalId}")
    public Result<List<GoalKpi>> getGoalKpis(@PathVariable Long goalId) {
        List<GoalKpi> list = performanceGoalService.getGoalKpis(goalId);
        // 填充KPI指标名称
        list.forEach(gk -> {
            if (gk.getKpiId() != null) {
                KpiIndicator kpi = kpiIndicatorService.getById(gk.getKpiId());
                if (kpi != null) {
                    gk.setKpiName(kpi.getName());
                }
            }
        });
        return Result.success(list);
    }

    /** 取消关联KPI */
    @DeleteMapping("/unbindKpi/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @OperationLog("取消关联KPI")
    public Result<?> unbindKpi(@PathVariable Long id) {
        performanceGoalService.unbindKpi(id);
        return Result.success("取消关联成功");
    }

    /** 填充目标的部门名称和责任人姓名 */
    private void fillGoalInfo(PerformanceGoal goal) {
        if (goal == null) return;
        if (goal.getDepartmentId() != null) {
            SysDepartment dept = sysDepartmentService.getById(goal.getDepartmentId());
            if (dept != null) {
                goal.setDepartmentName(dept.getName());
            }
        }
        if (goal.getUserId() != null) {
            SysUser user = sysUserService.getById(goal.getUserId());
            if (user != null) {
                goal.setUserName(user.getRealName());
            }
        }
    }
}
