package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.entity.DevelopmentPlan;

/**
 * 个人发展计划（IDP）服务接口
 */
public interface DevelopmentPlanService extends IService<DevelopmentPlan> {

    /** 分页查询发展计划 */
    Page<DevelopmentPlan> pageList(Integer pageNum, Integer pageSize, Long planId, Long departmentId, Integer status, String keyword);

    /** 根据考核结果自动生成发展计划 */
    void generateByPlan(Long planId);

    /** 新增发展计划 */
    void addPlan(DevelopmentPlan plan);

    /** 更新发展计划 */
    void updatePlan(DevelopmentPlan plan);

    /** 修改状态 */
    void changeStatus(Long id, Integer status);

    /** 查询部门发展计划（部门经理视角） */
    Page<DevelopmentPlan> departmentPlans(Integer pageNum, Integer pageSize, Long departmentId, Integer status);
}
