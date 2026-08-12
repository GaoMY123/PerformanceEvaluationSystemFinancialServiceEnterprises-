package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.performance.entity.EvaluationPlan;
import com.performance.entity.EvaluationRule;
import com.performance.entity.EvaluationTask;
import com.performance.entity.KpiIndicator;
import com.performance.entity.PeerAssignment;
import com.performance.entity.SysUser;
import com.performance.mapper.EvaluationTaskMapper;
import com.performance.mapper.PeerAssignmentMapper;
import com.performance.service.EvaluationPlanService;
import com.performance.service.KpiIndicatorService;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考核方案控制器（含评分规则配置）
 */
@RestController
@RequestMapping("/api/plan")
public class EvaluationPlanController {

    @Autowired
    private EvaluationPlanService evaluationPlanService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private KpiIndicatorService kpiIndicatorService;

    @Autowired
    private EvaluationTaskMapper evaluationTaskMapper;

    @Autowired
    private PeerAssignmentMapper peerAssignmentMapper;

    /** 分页查询考核方案 */
    @GetMapping("/page")
    public Result<Page<EvaluationPlan>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String periodType,
            @RequestParam(required = false) Integer status) {
        Page<EvaluationPlan> page = evaluationPlanService.pageList(pageNum, pageSize, keyword, year, periodType, status);
        page.getRecords().forEach(this::fillCreatorName);
        return Result.success(page);
    }

    /** 根据ID获取方案详情 */
    @GetMapping("/{id}")
    public Result<EvaluationPlan> getById(@PathVariable Long id) {
        EvaluationPlan plan = evaluationPlanService.getById(id);
        if (plan != null) {
            fillCreatorName(plan);
        }
        return Result.success(plan);
    }

    /** 新增考核方案 */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("新增考核方案")
    public Result<?> add(@RequestBody EvaluationPlan plan) {
        evaluationPlanService.addPlan(plan);
        return Result.success("新增成功");
    }

    /** 更新考核方案 */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("更新考核方案")
    public Result<?> update(@RequestBody EvaluationPlan plan) {
        evaluationPlanService.updatePlan(plan);
        return Result.success("更新成功");
    }

    /** 删除考核方案 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("删除考核方案")
    public Result<?> delete(@PathVariable Long id) {
        evaluationPlanService.deletePlan(id);
        return Result.success("删除成功");
    }

    /** 启动考核方案（为所有员工生成考核任务） */
    @PostMapping("/start/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("启动考核方案")
    public Result<?> start(@PathVariable Long id) {
        evaluationPlanService.startPlan(id);
        return Result.success("方案已启动");
    }

    /** 完成考核方案（汇总计算最终得分和等级） */
    @PostMapping("/complete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("完成考核方案")
    public Result<?> complete(@PathVariable Long id) {
        evaluationPlanService.completePlan(id);
        return Result.success("方案已完成，考核结果已生成");
    }

    // ========== 评分规则管理 ==========

    /** 获取方案的评分规则列表 */
    @GetMapping("/rule/{planId}")
    public Result<List<EvaluationRule>> getRules(@PathVariable Long planId) {
        List<EvaluationRule> list = evaluationPlanService.getRules(planId);
        list.forEach(this::fillKpiName);
        return Result.success(list);
    }

    /** 新增评分规则 */
    @PostMapping("/rule")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("新增评分规则")
    public Result<?> addRule(@RequestBody EvaluationRule rule) {
        evaluationPlanService.addRule(rule);
        return Result.success("新增成功");
    }

    /** 更新评分规则 */
    @PutMapping("/rule")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("更新评分规则")
    public Result<?> updateRule(@RequestBody EvaluationRule rule) {
        evaluationPlanService.updateRule(rule);
        return Result.success("更新成功");
    }

    /** 删除评分规则 */
    @DeleteMapping("/rule/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("删除评分规则")
    public Result<?> deleteRule(@PathVariable Long id) {
        evaluationPlanService.deleteRule(id);
        return Result.success("删除成功");
    }

    /** 获取考核方案进度统计 */
    @GetMapping("/progress/{id}")
    public Result<Map<String, Object>> getProgress(@PathVariable Long id) {
        EvaluationPlan plan = evaluationPlanService.getById(id);
        if (plan == null) {
            return Result.error("方案不存在");
        }
        Map<String, Object> progress = new HashMap<>();
        progress.put("planId", id);
        progress.put("planName", plan.getName());
        progress.put("planStatus", plan.getStatus());

        // 总任务数
        Long totalCount = evaluationTaskMapper.selectCount(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getPlanId, id));
        progress.put("totalCount", totalCount);

        // 各状态任务数
        Long selfEvalCount = evaluationTaskMapper.selectCount(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getPlanId, id).eq(EvaluationTask::getStatus, 0));
        Long managerEvalCount = evaluationTaskMapper.selectCount(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getPlanId, id).eq(EvaluationTask::getStatus, 1));
        Long peerEvalCount = evaluationTaskMapper.selectCount(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getPlanId, id).eq(EvaluationTask::getStatus, 2));
        Long completedCount = evaluationTaskMapper.selectCount(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getPlanId, id).eq(EvaluationTask::getStatus, 3));

        progress.put("selfEvalCount", selfEvalCount);
        progress.put("managerEvalCount", managerEvalCount);
        progress.put("peerEvalCount", peerEvalCount);
        progress.put("completedCount", completedCount);

        // 完成率
        if (totalCount > 0) {
            progress.put("completionRate", Math.round(completedCount * 100.0 / totalCount * 10) / 10.0);
        } else {
            progress.put("completionRate", 0);
        }

        // 各阶段完成率
        if (totalCount > 0) {
            progress.put("selfEvalRate", Math.round((totalCount - selfEvalCount) * 100.0 / totalCount * 10) / 10.0);
            progress.put("managerEvalRate", Math.round((totalCount - selfEvalCount - managerEvalCount) * 100.0 / totalCount * 10) / 10.0);
            // 互评完成率：按互评分配完成比例计算
            Long totalPeerAssignments = peerAssignmentMapper.selectCount(
                    new LambdaQueryWrapper<PeerAssignment>().eq(PeerAssignment::getPlanId, id));
            Long completedPeerAssignments = peerAssignmentMapper.selectCount(
                    new LambdaQueryWrapper<PeerAssignment>().eq(PeerAssignment::getPlanId, id).eq(PeerAssignment::getStatus, 1));
            if (totalPeerAssignments > 0) {
                progress.put("peerEvalRate", Math.round(completedPeerAssignments * 100.0 / totalPeerAssignments * 10) / 10.0);
            } else {
                // 没有互评分配时，互评完成率等于已通过互评阶段的任务比例
                progress.put("peerEvalRate", Math.round(completedCount * 100.0 / totalCount * 10) / 10.0);
            }
        } else {
            progress.put("selfEvalRate", 0);
            progress.put("managerEvalRate", 0);
            progress.put("peerEvalRate", 0);
        }

        return Result.success(progress);
    }

    /** 填充创建人姓名 */
    private void fillCreatorName(EvaluationPlan plan) {
        if (plan != null && plan.getCreatorId() != null) {
            SysUser creator = sysUserService.getById(plan.getCreatorId());
            if (creator != null) {
                plan.setCreatorName(creator.getRealName());
            }
        }
    }

    /** 填充KPI指标名称 */
    private void fillKpiName(EvaluationRule rule) {
        if (rule != null && rule.getKpiId() != null) {
            KpiIndicator kpi = kpiIndicatorService.getById(rule.getKpiId());
            if (kpi != null) {
                rule.setKpiName(kpi.getName());
            }
        }
    }
}
