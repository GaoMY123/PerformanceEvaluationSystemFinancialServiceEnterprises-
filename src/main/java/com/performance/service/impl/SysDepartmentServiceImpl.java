package com.performance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.common.exception.BusinessException;
import com.performance.entity.SysDepartment;
import com.performance.entity.SysUser;
import com.performance.mapper.SysDepartmentMapper;
import com.performance.mapper.SysUserMapper;
import com.performance.service.SysDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门服务实现类
 */
@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> implements SysDepartmentService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysDepartment> listAll() {
        return list(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getStatus, 1)
                .orderByAsc(SysDepartment::getSortOrder));
    }

    @Override
    public Page<SysDepartment> pageList(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        Page<SysDepartment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(SysDepartment::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(SysDepartment::getStatus, status);
        }
        wrapper.orderByAsc(SysDepartment::getSortOrder);
        return page(page, wrapper);
    }

    @Override
    public void addDepartment(SysDepartment department) {
        // 检查部门名称是否重复
        long count = count(new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getName, department.getName()));
        if (count > 0) {
            throw new BusinessException("部门名称已存在");
        }
        save(department);
    }

    @Override
    public void updateDepartment(SysDepartment department) {
        // 检查部门名称是否与其他部门重复
        long count = count(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getName, department.getName())
                .ne(SysDepartment::getId, department.getId()));
        if (count > 0) {
            throw new BusinessException("部门名称已存在");
        }
        updateById(department);
    }

    @Override
    public void deleteDepartment(Long id) {
        // 检查部门下是否有员工
        long userCount = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getDepartmentId, id));
        if (userCount > 0) {
            throw new BusinessException("该部门下还有员工，无法删除");
        }
        removeById(id);
    }
}
