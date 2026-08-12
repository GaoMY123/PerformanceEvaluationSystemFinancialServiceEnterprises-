package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.entity.*;
import com.performance.security.SecurityUtils;
import com.performance.service.AppealService;
import com.performance.service.EvaluationPlanService;
import com.performance.service.EvaluationTaskService;
import com.performance.service.SysDepartmentService;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 绩效申诉控制器
 */
@RestController
@RequestMapping("/api/appeal")
public class AppealController {

    @Autowired
    private AppealService appealService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private EvaluationTaskService evaluationTaskService;

    @Autowired
    private EvaluationPlanService evaluationPlanService;

    /** 分页查询所有申诉（管理视角） */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public Result<Page<Appeal>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<Appeal> page = appealService.pageList(pageNum, pageSize, departmentId, status, keyword);
        page.getRecords().forEach(this::fillAppealInfo);
        return Result.success(page);
    }

    /** 查询我的申诉 */
    @GetMapping("/my")
    public Result<Page<Appeal>> myAppeals(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Appeal> page = appealService.myAppeals(pageNum, pageSize);
        page.getRecords().forEach(this::fillAppealInfo);
        return Result.success(page);
    }

    /** 查询部门申诉（部门经理视角） */
    @GetMapping("/department")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<Page<Appeal>> departmentAppeals(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        SysUser user = SecurityUtils.getCurrentUser();
        if (user == null || user.getDepartmentId() == null) {
            return Result.error("未关联部门");
        }
        Page<Appeal> page = appealService.departmentAppeals(pageNum, pageSize, user.getDepartmentId(), status);
        page.getRecords().forEach(this::fillAppealInfo);
        return Result.success(page);
    }

    /** 根据ID获取申诉详情 */
    @GetMapping("/{id}")
    public Result<Appeal> getById(@PathVariable Long id) {
        Appeal appeal = appealService.getById(id);
        if (appeal != null) {
            fillAppealInfo(appeal);
        }
        return Result.success(appeal);
    }

    /** 提交申诉（员工） */
    @PostMapping
    @OperationLog("提交绩效申诉")
    public Result<?> submit(@RequestBody Appeal appeal) {
        appealService.submitAppeal(appeal);
        return Result.success("申诉提交成功");
    }

    /** 处理申诉（管理者回复） */
    @PutMapping("/handle/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @OperationLog("处理绩效申诉")
    public Result<?> handle(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String reply = (String) body.get("reply");
        Integer status = (Integer) body.get("status");
        appealService.handleAppeal(id, reply, status);
        return Result.success("处理完成");
    }

    /** 填充申诉人姓名、部门名称、方案名称、处理人姓名 */
    private void fillAppealInfo(Appeal appeal) {
        if (appeal == null) return;
        if (appeal.getUserId() != null) {
            SysUser user = sysUserService.getById(appeal.getUserId());
            if (user != null) {
                appeal.setUserName(user.getRealName());
                if (user.getDepartmentId() != null) {
                    SysDepartment dept = sysDepartmentService.getById(user.getDepartmentId());
                    if (dept != null) {
                        appeal.setDepartmentName(dept.getName());
                    }
                }
            }
        }
        if (appeal.getTaskId() != null) {
            EvaluationTask task = evaluationTaskService.getById(appeal.getTaskId());
            if (task != null && task.getPlanId() != null) {
                EvaluationPlan plan = evaluationPlanService.getById(task.getPlanId());
                if (plan != null) {
                    appeal.setPlanName(plan.getName());
                }
            }
        }
        if (appeal.getHandlerId() != null) {
            SysUser handler = sysUserService.getById(appeal.getHandlerId());
            if (handler != null) {
                appeal.setHandlerName(handler.getRealName());
            }
        }
    }
}
