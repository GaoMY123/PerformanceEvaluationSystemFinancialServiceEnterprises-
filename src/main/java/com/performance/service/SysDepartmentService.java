package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.entity.SysDepartment;

import java.util.List;

/**
 * 部门服务接口
 */
public interface SysDepartmentService extends IService<SysDepartment> {

    /** 获取所有部门列表 */
    List<SysDepartment> listAll();

    /** 分页查询部门 */
    Page<SysDepartment> pageList(Integer pageNum, Integer pageSize, String keyword, Integer status);

    /** 新增部门 */
    void addDepartment(SysDepartment department);

    /** 更新部门 */
    void updateDepartment(SysDepartment department);

    /** 删除部门 */
    void deleteDepartment(Long id);
}
