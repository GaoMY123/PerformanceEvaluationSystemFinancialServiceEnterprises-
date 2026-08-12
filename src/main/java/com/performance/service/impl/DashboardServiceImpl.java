package com.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.performance.entity.*;
import com.performance.mapper.*;
import com.performance.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;
    @Autowired
    private EvaluationPlanMapper evaluationPlanMapper;
    @Autowired
    private EvaluationTaskMapper evaluationTaskMapper;
    @Autowired
    private EvaluationScoreMapper evaluationScoreMapper;
    @Autowired
    private EvaluationRuleMapper evaluationRuleMapper;
    @Autowired
    private KpiIndicatorMapper kpiIndicatorMapper;
    @Autowired
    private AppealMapper appealMapper;
    @Autowired
    private DevelopmentPlanMapper developmentPlanMapper;
    @Autowired
    private SalaryAdjustmentMapper salaryAdjustmentMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        result.put("userCount", sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1)));
        result.put("departmentCount", sysDepartmentMapper.selectCount(new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getStatus, 1)));
        result.put("planCount", evaluationPlanMapper.selectCount(null));
        result.put("activePlanCount", evaluationPlanMapper.selectCount(new LambdaQueryWrapper<EvaluationPlan>().eq(EvaluationPlan::getStatus, 1)));
        result.put("completedPlanCount", evaluationPlanMapper.selectCount(new LambdaQueryWrapper<EvaluationPlan>().eq(EvaluationPlan::getStatus, 2)));
        result.put("completedTaskCount", evaluationTaskMapper.selectCount(new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getStatus, 3)));
        result.put("pendingAppealCount", appealMapper.selectCount(new LambdaQueryWrapper<Appeal>().in(Appeal::getStatus, 0, 1)));
        result.put("pendingIdpCount", developmentPlanMapper.selectCount(new LambdaQueryWrapper<DevelopmentPlan>().in(DevelopmentPlan::getStatus, 0, 1)));
        result.put("salaryPendingCount", salaryAdjustmentMapper.selectCount(new LambdaQueryWrapper<SalaryAdjustment>().eq(SalaryAdjustment::getStatus, 0)));
        return result;
    }

    @Override
    public Map<String, Object> getMyOverview(Long userId) {
        Map<String, Object> result = new HashMap<>();
        // 我的考核任务
        Long myTaskCount = evaluationTaskMapper.selectCount(new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getUserId, userId));
        Long myCompletedCount = evaluationTaskMapper.selectCount(new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getUserId, userId).eq(EvaluationTask::getStatus, 3));
        Long myPendingCount = evaluationTaskMapper.selectCount(new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getUserId, userId).in(EvaluationTask::getStatus, 1, 2));
        result.put("myTaskCount", myTaskCount);
        result.put("myCompletedCount", myCompletedCount);
        result.put("myPendingCount", myPendingCount);

        // 我的申诉
        Long myAppealCount = appealMapper.selectCount(new LambdaQueryWrapper<Appeal>().eq(Appeal::getUserId, userId));
        Long myPendingAppealCount = appealMapper.selectCount(new LambdaQueryWrapper<Appeal>().eq(Appeal::getUserId, userId).in(Appeal::getStatus, 0, 1));
        result.put("myAppealCount", myAppealCount);
        result.put("myPendingAppealCount", myPendingAppealCount);

        // 我的发展计划
        Long myIdpCount = developmentPlanMapper.selectCount(new LambdaQueryWrapper<DevelopmentPlan>().eq(DevelopmentPlan::getUserId, userId));
        Long myActiveIdpCount = developmentPlanMapper.selectCount(new LambdaQueryWrapper<DevelopmentPlan>().eq(DevelopmentPlan::getUserId, userId).in(DevelopmentPlan::getStatus, 1, 2));
        result.put("myIdpCount", myIdpCount);
        result.put("myActiveIdpCount", myActiveIdpCount);

        // 我的最新考核得分和等级
        List<EvaluationTask> latestTasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getUserId, userId)
                        .eq(EvaluationTask::getStatus, 3)
                        .orderByDesc(EvaluationTask::getUpdateTime)
                        .last("LIMIT 1"));
        if (!latestTasks.isEmpty()) {
            EvaluationTask latest = latestTasks.get(0);
            result.put("myLatestScore", latest.getFinalScore());
            result.put("myLatestGrade", latest.getGrade());
            EvaluationPlan plan = evaluationPlanMapper.selectById(latest.getPlanId());
            result.put("myLatestPlanName", plan != null ? plan.getName() : "");
        }
        return result;
    }

    @Override
    public Map<String, Object> getGradeDistribution(Long planId) {
        Map<String, Object> result = new HashMap<>();
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getPlanId, planId)
                        .eq(EvaluationTask::getStatus, 3));
        // 统计各等级人数
        Map<String, Long> gradeCount = tasks.stream()
                .filter(t -> t.getGrade() != null)
                .collect(Collectors.groupingBy(EvaluationTask::getGrade, Collectors.counting()));
        List<String> grades = Arrays.asList("A", "B", "C", "D");
        List<Map<String, Object>> data = new ArrayList<>();
        for (String grade : grades) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", grade + "级");
            item.put("value", gradeCount.getOrDefault(grade, 0L));
            data.add(item);
        }
        result.put("data", data);
        return result;
    }

    @Override
    public Map<String, Object> getDepartmentAvgScore(Long planId) {
        Map<String, Object> result = new HashMap<>();
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getPlanId, planId)
                        .eq(EvaluationTask::getStatus, 3));
        List<SysDepartment> departments = sysDepartmentMapper.selectList(
                new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getStatus, 1));

        // 按部门分组计算平均分
        Map<Long, List<EvaluationTask>> deptTasks = tasks.stream()
                .filter(t -> t.getDepartmentId() != null && t.getFinalScore() != null)
                .collect(Collectors.groupingBy(EvaluationTask::getDepartmentId));

        List<String> deptNames = new ArrayList<>();
        List<BigDecimal> avgScores = new ArrayList<>();

        for (SysDepartment dept : departments) {
            List<EvaluationTask> deptTaskList = deptTasks.getOrDefault(dept.getId(), Collections.emptyList());
            if (!deptTaskList.isEmpty()) {
                deptNames.add(dept.getName());
                BigDecimal sum = deptTaskList.stream()
                        .map(EvaluationTask::getFinalScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                avgScores.add(sum.divide(BigDecimal.valueOf(deptTaskList.size()), 2, RoundingMode.HALF_UP));
            }
        }
        result.put("departments", deptNames);
        result.put("scores", avgScores);
        return result;
    }

    @Override
    public Map<String, Object> getPersonalRadar(Long taskId) {
        Map<String, Object> result = new HashMap<>();
        EvaluationTask task = evaluationTaskMapper.selectById(taskId);
        if (task == null) {
            return result;
        }
        // 获取方案的评分规则和对应的KPI名称
        List<EvaluationRule> rules = evaluationRuleMapper.selectList(
                new LambdaQueryWrapper<EvaluationRule>().eq(EvaluationRule::getPlanId, task.getPlanId()));

        List<String> indicators = new ArrayList<>();
        List<BigDecimal> scores = new ArrayList<>();

        for (EvaluationRule rule : rules) {
            KpiIndicator kpi = kpiIndicatorMapper.selectById(rule.getKpiId());
            if (kpi != null) {
                indicators.add(kpi.getName());
                // 取该KPI所有评分的平均值
                List<EvaluationScore> kpiScores = evaluationScoreMapper.selectList(
                        new LambdaQueryWrapper<EvaluationScore>()
                                .eq(EvaluationScore::getTaskId, taskId)
                                .eq(EvaluationScore::getKpiId, rule.getKpiId()));
                if (!kpiScores.isEmpty()) {
                    BigDecimal avg = kpiScores.stream()
                            .map(EvaluationScore::getScore)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(kpiScores.size()), 2, RoundingMode.HALF_UP);
                    scores.add(avg);
                } else {
                    scores.add(BigDecimal.ZERO);
                }
            }
        }
        result.put("indicators", indicators);
        result.put("scores", scores);
        return result;
    }

    @Override
    public Map<String, Object> getScoreTrend(Long userId, Integer year) {
        Map<String, Object> result = new HashMap<>();
        // 查询该用户在指定年度的所有已完成考核任务
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getUserId, userId)
                        .eq(EvaluationTask::getStatus, 3));

        // 按考核方案获取周期信息并排序
        List<Map<String, Object>> trendData = new ArrayList<>();
        for (EvaluationTask task : tasks) {
            EvaluationPlan plan = evaluationPlanMapper.selectById(task.getPlanId());
            if (plan != null && plan.getYear().equals(year)) {
                Map<String, Object> item = new HashMap<>();
                item.put("planName", plan.getName());
                item.put("score", task.getFinalScore());
                item.put("grade", task.getGrade());
                item.put("startDate", plan.getStartDate());
                trendData.add(item);
            }
        }
        // 按开始日期排序
        trendData.sort(Comparator.comparing(m -> m.get("startDate").toString()));
        result.put("data", trendData);
        return result;
    }

    @Override
    public Map<String, Object> getDepartmentRanking(Long planId) {
        Map<String, Object> result = new HashMap<>();
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getPlanId, planId)
                        .eq(EvaluationTask::getStatus, 3));

        // 按部门分组计算平均分并排名
        Map<Long, List<EvaluationTask>> deptTasks = tasks.stream()
                .filter(t -> t.getDepartmentId() != null && t.getFinalScore() != null)
                .collect(Collectors.groupingBy(EvaluationTask::getDepartmentId));

        List<Map<String, Object>> rankings = new ArrayList<>();
        deptTasks.forEach((deptId, taskList) -> {
            SysDepartment dept = sysDepartmentMapper.selectById(deptId);
            if (dept != null) {
                BigDecimal sum = taskList.stream()
                        .map(EvaluationTask::getFinalScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal avg = sum.divide(BigDecimal.valueOf(taskList.size()), 2, RoundingMode.HALF_UP);
                Map<String, Object> item = new HashMap<>();
                item.put("departmentName", dept.getName());
                item.put("avgScore", avg);
                item.put("userCount", taskList.size());
                rankings.add(item);
            }
        });
        // 按平均分降序排名
        rankings.sort((a, b) -> ((BigDecimal) b.get("avgScore")).compareTo((BigDecimal) a.get("avgScore")));
        result.put("data", rankings);
        return result;
    }

    @Override
    public Map<String, Object> getDepartmentOverview(Long departmentId) {
        Map<String, Object> result = new HashMap<>();
        // 部门人数
        result.put("deptUserCount", sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getDepartmentId, departmentId).eq(SysUser::getStatus, 1)));
        // 部门考核任务数
        result.put("deptTaskCount", evaluationTaskMapper.selectCount(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getDepartmentId, departmentId)));
        // 部门已完成考核
        result.put("deptCompletedCount", evaluationTaskMapper.selectCount(
                new LambdaQueryWrapper<EvaluationTask>().eq(EvaluationTask::getDepartmentId, departmentId).eq(EvaluationTask::getStatus, 3)));
        // 部门待处理申诉
        result.put("deptPendingAppealCount", appealMapper.selectCount(
                new LambdaQueryWrapper<Appeal>().in(Appeal::getStatus, 0, 1)
                        .inSql(Appeal::getUserId, "SELECT id FROM sys_user WHERE department_id = " + departmentId)));
        // 部门进行中发展计划
        result.put("deptActiveIdpCount", developmentPlanMapper.selectCount(
                new LambdaQueryWrapper<DevelopmentPlan>().in(DevelopmentPlan::getStatus, 1, 2)
                        .inSql(DevelopmentPlan::getUserId, "SELECT id FROM sys_user WHERE department_id = " + departmentId)));
        // 部门平均分
        List<EvaluationTask> completedTasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getDepartmentId, departmentId)
                        .eq(EvaluationTask::getStatus, 3));
        if (!completedTasks.isEmpty()) {
            BigDecimal avg = completedTasks.stream()
                    .filter(t -> t.getFinalScore() != null)
                    .map(EvaluationTask::getFinalScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(completedTasks.stream().filter(t -> t.getFinalScore() != null).count()), 2, RoundingMode.HALF_UP);
            result.put("deptAvgScore", avg);
        }
        return result;
    }

    @Override
    public Map<String, Object> getMultiDimensionTrend(Integer year, String periodType, Long departmentId) {
        Map<String, Object> result = new HashMap<>();
        // 查询符合条件的考核方案
        LambdaQueryWrapper<EvaluationPlan> planWrapper = new LambdaQueryWrapper<EvaluationPlan>()
                .eq(EvaluationPlan::getStatus, 2);
        if (year != null) {
            planWrapper.eq(EvaluationPlan::getYear, year);
        }
        if (periodType != null && !periodType.isEmpty()) {
            planWrapper.eq(EvaluationPlan::getPeriodType, periodType);
        }
        planWrapper.orderByAsc(EvaluationPlan::getStartDate);
        List<EvaluationPlan> plans = evaluationPlanMapper.selectList(planWrapper);

        // 获取部门列表
        List<SysDepartment> departments = sysDepartmentMapper.selectList(
                new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getStatus, 1));
        Map<Long, String> deptNameMap = departments.stream()
                .collect(Collectors.toMap(SysDepartment::getId, SysDepartment::getName));

        // 构建周期标签列表
        List<String> periodLabels = new ArrayList<>();
        // 每个周期的部门均分数据
        List<Map<String, Object>> trendSeries = new ArrayList<>();

        for (EvaluationPlan plan : plans) {
            String label = plan.getYear() + "-"
                    + ("MONTHLY".equals(plan.getPeriodType()) ? "M" + plan.getPeriodNumber()
                    : "QUARTERLY".equals(plan.getPeriodType()) ? "Q" + plan.getPeriodNumber()
                    : "年度");
            periodLabels.add(label);

            // 查询该方案下已完成的考核任务
            LambdaQueryWrapper<EvaluationTask> taskWrapper = new LambdaQueryWrapper<EvaluationTask>()
                    .eq(EvaluationTask::getPlanId, plan.getId())
                    .eq(EvaluationTask::getStatus, 3);
            if (departmentId != null) {
                taskWrapper.eq(EvaluationTask::getDepartmentId, departmentId);
            }
            List<EvaluationTask> tasks = evaluationTaskMapper.selectList(taskWrapper);

            // 按部门分组计算均分
            Map<Long, List<EvaluationTask>> deptTasks = tasks.stream()
                    .filter(t -> t.getDepartmentId() != null && t.getFinalScore() != null)
                    .collect(Collectors.groupingBy(EvaluationTask::getDepartmentId));

            Map<String, BigDecimal> deptAvgMap = new LinkedHashMap<>();
            for (SysDepartment dept : departments) {
                if (departmentId != null && !departmentId.equals(dept.getId())) continue;
                List<EvaluationTask> deptTaskList = deptTasks.getOrDefault(dept.getId(), Collections.emptyList());
                if (!deptTaskList.isEmpty()) {
                    BigDecimal avg = deptTaskList.stream()
                            .map(EvaluationTask::getFinalScore)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(deptTaskList.size()), 2, RoundingMode.HALF_UP);
                    deptAvgMap.put(dept.getName(), avg);
                } else {
                    deptAvgMap.put(dept.getName(), null);
                }
            }
            Map<String, Object> seriesItem = new HashMap<>();
            seriesItem.put("period", label);
            seriesItem.put("planName", plan.getName());
            seriesItem.put("deptAvg", deptAvgMap);
            // 全公司均分
            if (!tasks.isEmpty()) {
                BigDecimal totalAvg = tasks.stream()
                        .filter(t -> t.getFinalScore() != null)
                        .map(EvaluationTask::getFinalScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(tasks.stream().filter(t -> t.getFinalScore() != null).count()), 2, RoundingMode.HALF_UP);
                seriesItem.put("totalAvg", totalAvg);
            }
            trendSeries.add(seriesItem);
        }

        result.put("periods", periodLabels);
        result.put("series", trendSeries);
        result.put("departments", departmentId != null
                ? Collections.singletonList(deptNameMap.getOrDefault(departmentId, "未知部门"))
                : deptNameMap.values().stream().distinct().collect(Collectors.toList()));
        return result;
    }
}
