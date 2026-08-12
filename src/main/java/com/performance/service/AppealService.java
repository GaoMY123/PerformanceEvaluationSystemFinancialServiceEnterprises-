package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.entity.Appeal;

/**
 * 绩效申诉服务接口
 */
public interface AppealService extends IService<Appeal> {

    /** 分页查询申诉列表 */
    Page<Appeal> pageList(Integer pageNum, Integer pageSize, Long departmentId, Integer status, String keyword);

    /** 提交申诉 */
    void submitAppeal(Appeal appeal);

    /** 处理申诉（回复） */
    void handleAppeal(Long id, String reply, Integer status);

    /** 查询当前用户的申诉列表 */
    Page<Appeal> myAppeals(Integer pageNum, Integer pageSize);

    /** 查询部门申诉列表（部门经理视角） */
    Page<Appeal> departmentAppeals(Integer pageNum, Integer pageSize, Long departmentId, Integer status);
}
