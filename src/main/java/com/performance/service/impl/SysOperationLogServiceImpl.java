package com.performance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.entity.SysOperationLog;
import com.performance.mapper.SysOperationLogMapper;
import com.performance.service.SysOperationLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现类
 */
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {

    @Override
    public Page<SysOperationLog> pageList(Integer pageNum, Integer pageSize, String keyword, String username, Integer result) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(SysOperationLog::getOperation, keyword);
        }
        if (StrUtil.isNotBlank(username)) {
            wrapper.like(SysOperationLog::getUsername, username);
        }
        if (result != null) {
            wrapper.eq(SysOperationLog::getResult, result);
        }
        wrapper.orderByDesc(SysOperationLog::getCreateTime);
        return page(page, wrapper);
    }
}
