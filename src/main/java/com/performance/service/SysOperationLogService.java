package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.entity.SysOperationLog;

/**
 * 操作日志服务接口
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    /** 分页查询操作日志 */
    Page<SysOperationLog> pageList(Integer pageNum, Integer pageSize, String keyword, String username, Integer result);
}
