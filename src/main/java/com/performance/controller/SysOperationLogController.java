package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.common.Result;
import com.performance.entity.SysOperationLog;
import com.performance.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器（满足金融合规性要求）
 */
@RestController
@RequestMapping("/api/log")
public class SysOperationLogController {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    /** 分页查询操作日志 */
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<SysOperationLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer result) {
        return Result.success(sysOperationLogService.pageList(pageNum, pageSize, keyword, username, result));
    }
}
