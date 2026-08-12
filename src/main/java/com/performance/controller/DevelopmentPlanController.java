package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.entity.DevelopmentPlan;
import com.performance.entity.EvaluationPlan;
import com.performance.entity.SysDepartment;
import com.performance.entity.SysUser;
import com.performance.security.SecurityUtils;
import com.performance.service.DevelopmentPlanService;
import com.performance.service.EvaluationPlanService;
import com.performance.service.SysDepartmentService;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 个人发展计划（IDP）控制器
 */
@RestController
@RequestMapping("/api/idp")
public class DevelopmentPlanController {

    @Autowired
    private DevelopmentPlanService developmentPlanService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private EvaluationPlanService evaluationPlanService;

    /** 分页查询发展计划（管理视角） */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public Result<Page<DevelopmentPlan>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<DevelopmentPlan> page = developmentPlanService.pageList(pageNum, pageSize, planId, departmentId, status, keyword);
        page.getRecords().forEach(this::fillPlanInfo);
        return Result.success(page);
    }

    /** 查询我的发展计划 */
    @GetMapping("/my")
    public Result<Page<DevelopmentPlan>> myPlans(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<DevelopmentPlan> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DevelopmentPlan> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(DevelopmentPlan::getUserId, userId);
        wrapper.orderByDesc(DevelopmentPlan::getCreateTime);
        Page<DevelopmentPlan> result = developmentPlanService.page(page, wrapper);
        result.getRecords().forEach(this::fillPlanInfo);
        return Result.success(result);
    }

    /** 查询部门发展计划（部门经理视角） */
    @GetMapping("/department")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<Page<DevelopmentPlan>> departmentPlans(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        SysUser user = SecurityUtils.getCurrentUser();
        if (user == null || user.getDepartmentId() == null) {
            return Result.error("未关联部门");
        }
        Page<DevelopmentPlan> page = developmentPlanService.departmentPlans(pageNum, pageSize, user.getDepartmentId(), status);
        page.getRecords().forEach(this::fillPlanInfo);
        return Result.success(page);
    }

    /** 根据ID获取发展计划详情 */
    @GetMapping("/{id}")
    public Result<DevelopmentPlan> getById(@PathVariable Long id) {
        DevelopmentPlan plan = developmentPlanService.getById(id);
        if (plan != null) {
            fillPlanInfo(plan);
        }
        return Result.success(plan);
    }

    /** 根据考核方案自动生成发展计划 */
    @PostMapping("/generate/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("生成个人发展计划")
    public Result<?> generate(@PathVariable Long planId) {
        developmentPlanService.generateByPlan(planId);
        return Result.success("个人发展计划已生成");
    }

    /** 新增发展计划 */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("新增个人发展计划")
    public Result<?> add(@RequestBody DevelopmentPlan plan) {
        developmentPlanService.addPlan(plan);
        return Result.success("新增成功");
    }

    /** 更新发展计划 */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @OperationLog("更新个人发展计划")
    public Result<?> update(@RequestBody DevelopmentPlan plan) {
        developmentPlanService.updatePlan(plan);
        return Result.success("更新成功");
    }

    /** 修改状态 */
    @PutMapping("/status/{id}/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("修改发展计划状态")
    public Result<?> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        developmentPlanService.changeStatus(id, status);
        return Result.success("状态修改成功");
    }

    /** 填充员工姓名、部门名称、方案名称 */
    private void fillPlanInfo(DevelopmentPlan plan) {
        if (plan == null) return;
        if (plan.getUserId() != null) {
            SysUser user = sysUserService.getById(plan.getUserId());
            if (user != null) {
                plan.setUserName(user.getRealName());
                if (user.getDepartmentId() != null) {
                    SysDepartment dept = sysDepartmentService.getById(user.getDepartmentId());
                    if (dept != null) {
                        plan.setDepartmentName(dept.getName());
                    }
                }
            }
        }
        if (plan.getPlanId() != null) {
            EvaluationPlan evalPlan = evaluationPlanService.getById(plan.getPlanId());
            if (evalPlan != null) {
                plan.setPlanName(evalPlan.getName());
            }
        }
    }
}
