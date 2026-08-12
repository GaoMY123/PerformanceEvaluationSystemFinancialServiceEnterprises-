package com.performance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.dto.EvaluationScoreDTO;
import com.performance.entity.*;
import com.performance.mapper.PeerAssignmentMapper;
import com.performance.service.EvaluationPlanService;
import com.performance.service.EvaluationTaskService;
import com.performance.service.FileService;
import com.performance.service.KpiIndicatorService;
import com.performance.service.SysDepartmentService;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 考核任务控制器（评分、附件管理）
 */
@RestController
@RequestMapping("/api/task")
public class EvaluationTaskController {

    @Autowired
    private EvaluationTaskService evaluationTaskService;

    @Autowired
    private EvaluationPlanService evaluationPlanService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private KpiIndicatorService kpiIndicatorService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PeerAssignmentMapper peerAssignmentMapper;

    /** 分页查询所有考核任务（管理视角） */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<Page<EvaluationTask>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<EvaluationTask> page = evaluationTaskService.pageList(pageNum, pageSize, planId, departmentId, status, keyword);
        page.getRecords().forEach(this::fillTaskInfo);
        return Result.success(page);
    }

    /** 查询我的考核任务 */
    @GetMapping("/my")
    public Result<Page<EvaluationTask>> myTasks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long planId) {
        Page<EvaluationTask> page = evaluationTaskService.myTasks(pageNum, pageSize, planId);
        page.getRecords().forEach(this::fillTaskInfo);
        return Result.success(page);
    }

    /** 查询需要我评价的下属任务（部门经理） */
    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'HR')")
    public Result<Page<EvaluationTask>> managerTasks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long planId) {
        Page<EvaluationTask> page = evaluationTaskService.managerTasks(pageNum, pageSize, planId);
        page.getRecords().forEach(this::fillTaskInfo);
        return Result.success(page);
    }

    /** 查询需要我互评的同事任务 */
    @GetMapping("/peer")
    public Result<Page<EvaluationTask>> peerTasks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long planId) {
        Page<EvaluationTask> page = evaluationTaskService.peerTasks(pageNum, pageSize, planId);
        page.getRecords().forEach(this::fillTaskInfo);
        return Result.success(page);
    }

    /** 获取任务详情 */
    @GetMapping("/{id}")
    public Result<EvaluationTask> getById(@PathVariable Long id) {
        EvaluationTask task = evaluationTaskService.getById(id);
        if (task != null) {
            fillTaskInfo(task);
        }
        return Result.success(task);
    }

    /** 提交评分 */
    @PostMapping("/score")
    @OperationLog("提交考核评分")
    public Result<?> submitScore(@Valid @RequestBody EvaluationScoreDTO dto) {
        evaluationTaskService.submitScore(dto);
        return Result.success("评分提交成功");
    }

    /** 获取某任务的评分明细 */
    @GetMapping("/score/{taskId}")
    public Result<List<EvaluationScore>> getScores(
            @PathVariable Long taskId,
            @RequestParam(required = false) String scoreType) {
        List<EvaluationScore> list = evaluationTaskService.getScores(taskId, scoreType);
        list.forEach(this::fillScoreInfo);
        return Result.success(list);
    }

    /** 上传考核附件 */
    @PostMapping("/attachment/upload")
    @OperationLog("上传考核附件")
    public Result<?> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("taskId") Long taskId) {
        String filePath = fileService.upload(file);
        EvaluationAttachment attachment = new EvaluationAttachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFilePath(filePath);
        attachment.setFileSize(file.getSize());
        evaluationTaskService.uploadAttachment(attachment);
        return Result.success("上传成功");
    }

    /** 获取任务的附件列表 */
    @GetMapping("/attachment/{taskId}")
    public Result<List<EvaluationAttachment>> getAttachments(@PathVariable Long taskId) {
        return Result.success(evaluationTaskService.getAttachments(taskId));
    }

    /** 删除附件 */
    @DeleteMapping("/attachment/{id}")
    @OperationLog("删除考核附件")
    public Result<?> deleteAttachment(@PathVariable Long id) {
        evaluationTaskService.deleteAttachment(id);
        return Result.success("删除成功");
    }

    /** 查询方案的互评分配列表 */
    @GetMapping("/peerAssignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<List<PeerAssignment>> peerAssignments(@RequestParam Long planId) {
        List<PeerAssignment> list = peerAssignmentMapper.selectList(
                new LambdaQueryWrapper<PeerAssignment>().eq(PeerAssignment::getPlanId, planId));
        // 填充评价人和被评价人姓名
        list.forEach(pa -> {
            SysUser evaluator = sysUserService.getById(pa.getEvaluatorId());
            SysUser evaluatee = sysUserService.getById(pa.getEvaluateeId());
            if (evaluator != null) pa.setEvaluatorName(evaluator.getRealName());
            if (evaluatee != null) pa.setEvaluateeName(evaluatee.getRealName());
        });
        return Result.success(list);
    }

    /** 填充任务的方案名称、被考核人姓名、部门名称 */
    private void fillTaskInfo(EvaluationTask task) {
        if (task == null) return;
        if (task.getPlanId() != null) {
            EvaluationPlan plan = evaluationPlanService.getById(task.getPlanId());
            if (plan != null) {
                task.setPlanName(plan.getName());
            }
        }
        if (task.getUserId() != null) {
            SysUser user = sysUserService.getById(task.getUserId());
            if (user != null) {
                task.setUserName(user.getRealName());
            }
        }
        if (task.getDepartmentId() != null) {
            SysDepartment dept = sysDepartmentService.getById(task.getDepartmentId());
            if (dept != null) {
                task.setDepartmentName(dept.getName());
            }
        }
    }

    /** 填充评分明细的KPI名称和评分人姓名 */
    private void fillScoreInfo(EvaluationScore score) {
        if (score == null) return;
        if (score.getKpiId() != null) {
            KpiIndicator kpi = kpiIndicatorService.getById(score.getKpiId());
            if (kpi != null) {
                score.setKpiName(kpi.getName());
            }
        }
        if (score.getEvaluatorId() != null) {
            SysUser evaluator = sysUserService.getById(score.getEvaluatorId());
            if (evaluator != null) {
                score.setEvaluatorName(evaluator.getRealName());
            }
        }
    }
}
