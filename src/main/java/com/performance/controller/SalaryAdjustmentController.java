package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.entity.EvaluationPlan;
import com.performance.entity.SalaryAdjustment;
import com.performance.entity.SysDepartment;
import com.performance.entity.SysUser;
import com.performance.security.SecurityUtils;
import com.performance.service.EvaluationPlanService;
import com.performance.service.SalaryAdjustmentService;
import com.performance.service.SysDepartmentService;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 薪酬调整控制器
 */
@RestController
@RequestMapping("/api/salary")
public class SalaryAdjustmentController {

    @Autowired
    private SalaryAdjustmentService salaryAdjustmentService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private EvaluationPlanService evaluationPlanService;

    /** 分页查询薪酬调整记录 */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<Page<SalaryAdjustment>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<SalaryAdjustment> page = salaryAdjustmentService.pageList(pageNum, pageSize, planId, departmentId, status, keyword);
        page.getRecords().forEach(this::fillAdjustmentInfo);
        return Result.success(page);
    }

    /** 查询部门薪酬调整记录（部门经理视角，只读） */
    @GetMapping("/department")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<Page<SalaryAdjustment>> departmentPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        SysUser user = SecurityUtils.getCurrentUser();
        if (user == null || user.getDepartmentId() == null) {
            return Result.error("未关联部门");
        }
        Page<SalaryAdjustment> page = salaryAdjustmentService.departmentPageList(pageNum, pageSize, user.getDepartmentId(), status);
        page.getRecords().forEach(this::fillAdjustmentInfo);
        return Result.success(page);
    }

    /** 根据ID获取薪酬调整详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<SalaryAdjustment> getById(@PathVariable Long id) {
        SalaryAdjustment adjustment = salaryAdjustmentService.getById(id);
        if (adjustment != null) {
            fillAdjustmentInfo(adjustment);
        }
        return Result.success(adjustment);
    }

    /** 根据考核方案自动生成薪酬调整建议 */
    @PostMapping("/generate/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("生成薪酬调整建议")
    public Result<?> generate(@PathVariable Long planId) {
        salaryAdjustmentService.generateByPlan(planId);
        return Result.success("薪酬调整建议已生成");
    }

    /** 更新薪酬调整记录 */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("更新薪酬调整")
    public Result<?> update(@RequestBody SalaryAdjustment adjustment) {
        salaryAdjustmentService.updateAdjustment(adjustment);
        return Result.success("更新成功");
    }

    /** 审批薪酬调整 */
    @PutMapping("/approve/{id}/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("审批薪酬调整")
    public Result<?> approve(@PathVariable Long id, @PathVariable Integer status) {
        salaryAdjustmentService.approve(id, status);
        return Result.success("审批完成");
    }

    /** 填充员工姓名、部门名称、方案名称 */
    private void fillAdjustmentInfo(SalaryAdjustment adjustment) {
        if (adjustment == null) return;
        if (adjustment.getUserId() != null) {
            SysUser user = sysUserService.getById(adjustment.getUserId());
            if (user != null) {
                adjustment.setUserName(user.getRealName());
                // 填充部门名称
                if (user.getDepartmentId() != null) {
                    SysDepartment dept = sysDepartmentService.getById(user.getDepartmentId());
                    if (dept != null) {
                        adjustment.setDepartmentName(dept.getName());
                    }
                }
            }
        }
        if (adjustment.getPlanId() != null) {
            EvaluationPlan plan = evaluationPlanService.getById(adjustment.getPlanId());
            if (plan != null) {
                adjustment.setPlanName(plan.getName());
            }
        }
    }
}
