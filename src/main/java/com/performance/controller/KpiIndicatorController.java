package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.entity.KpiIndicator;
import com.performance.service.KpiIndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * KPI指标库控制器
 */
@RestController
@RequestMapping("/api/kpi")
public class KpiIndicatorController {

    @Autowired
    private KpiIndicatorService kpiIndicatorService;

    /** 分页查询KPI指标 */
    @GetMapping("/page")
    public Result<Page<KpiIndicator>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return Result.success(kpiIndicatorService.pageList(pageNum, pageSize, keyword, category, status));
    }

    /** 获取所有启用的KPI指标（下拉选择用） */
    @GetMapping("/list")
    public Result<List<KpiIndicator>> listEnabled() {
        return Result.success(kpiIndicatorService.listEnabled());
    }

    /** 根据ID获取指标详情 */
    @GetMapping("/{id}")
    public Result<KpiIndicator> getById(@PathVariable Long id) {
        return Result.success(kpiIndicatorService.getById(id));
    }

    /** 新增KPI指标 */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("新增KPI指标")
    public Result<?> add(@RequestBody KpiIndicator indicator) {
        kpiIndicatorService.addIndicator(indicator);
        return Result.success("新增成功");
    }

    /** 更新KPI指标 */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("更新KPI指标")
    public Result<?> update(@RequestBody KpiIndicator indicator) {
        kpiIndicatorService.updateIndicator(indicator);
        return Result.success("更新成功");
    }

    /** 删除KPI指标 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("删除KPI指标")
    public Result<?> delete(@PathVariable Long id) {
        kpiIndicatorService.deleteIndicator(id);
        return Result.success("删除成功");
    }
}
