package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.entity.SalaryAdjustment;

/**
 * 薪酬调整服务接口
 */
public interface SalaryAdjustmentService extends IService<SalaryAdjustment> {

    /** 分页查询薪酬调整记录 */
    Page<SalaryAdjustment> pageList(Integer pageNum, Integer pageSize, Long planId, Long departmentId, Integer status, String keyword);

    /** 根据考核结果自动生成薪酬调整建议 */
    void generateByPlan(Long planId);

    /** 更新薪酬调整记录 */
    void updateAdjustment(SalaryAdjustment adjustment);

    /** 审批薪酬调整（通过/驳回） */
    void approve(Long id, Integer status);

    /** 查询部门薪酬调整记录（部门经理视角，只读） */
    Page<SalaryAdjustment> departmentPageList(Integer pageNum, Integer pageSize, Long departmentId, Integer status);
}
