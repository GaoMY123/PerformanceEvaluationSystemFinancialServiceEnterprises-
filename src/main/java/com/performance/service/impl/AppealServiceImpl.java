package com.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.common.exception.BusinessException;
import com.performance.entity.Appeal;
import com.performance.mapper.AppealMapper;
import com.performance.security.SecurityUtils;
import com.performance.service.AppealService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 绩效申诉服务实现类
 */
@Service
public class AppealServiceImpl extends ServiceImpl<AppealMapper, Appeal> implements AppealService {

    @Override
    public Page<Appeal> pageList(Integer pageNum, Integer pageSize, Long departmentId, Integer status, String keyword) {
        Page<Appeal> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Appeal> wrapper = new LambdaQueryWrapper<>();
        if (departmentId != null) {
            // 需要通过userId关联部门，暂由controller层过滤
        }
        if (status != null) {
            wrapper.eq(Appeal::getStatus, status);
        }
        wrapper.orderByDesc(Appeal::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void submitAppeal(Appeal appeal) {
        Long userId = SecurityUtils.getCurrentUserId();
        // 检查同一任务是否已有未处理的申诉
        long count = count(new LambdaQueryWrapper<Appeal>()
                .eq(Appeal::getTaskId, appeal.getTaskId())
                .eq(Appeal::getUserId, userId)
                .in(Appeal::getStatus, 0, 1)); // 待处理或处理中
        if (count > 0) {
            throw new BusinessException("该考核任务已有未处理的申诉");
        }
        appeal.setUserId(userId);
        appeal.setStatus(0); // 待处理
        save(appeal);
    }

    @Override
    public void handleAppeal(Long id, String reply, Integer status) {
        Appeal appeal = getById(id);
        if (appeal == null) {
            throw new BusinessException("申诉记录不存在");
        }
        if (appeal.getStatus() > 1) {
            throw new BusinessException("该申诉已处理完毕");
        }
        appeal.setReply(reply);
        appeal.setStatus(status);
        appeal.setHandlerId(SecurityUtils.getCurrentUserId());
        appeal.setHandleTime(LocalDateTime.now());
        updateById(appeal);
    }

    @Override
    public Page<Appeal> myAppeals(Integer pageNum, Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<Appeal> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Appeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appeal::getUserId, userId);
        wrapper.orderByDesc(Appeal::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Page<Appeal> departmentAppeals(Integer pageNum, Integer pageSize, Long departmentId, Integer status) {
        Page<Appeal> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Appeal> wrapper = new LambdaQueryWrapper<>();
        // 通过子查询过滤部门成员的申诉
        wrapper.inSql(Appeal::getUserId, "SELECT id FROM sys_user WHERE department_id = " + departmentId);
        if (status != null) {
            wrapper.eq(Appeal::getStatus, status);
        }
        wrapper.orderByDesc(Appeal::getCreateTime);
        return page(page, wrapper);
    }
}
