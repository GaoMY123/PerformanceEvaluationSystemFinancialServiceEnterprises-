package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.entity.SysDepartment;
import com.performance.entity.SysUser;
import com.performance.service.SysDepartmentService;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api/department")
public class SysDepartmentController {

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private SysUserService sysUserService;

    /** 获取所有部门列表（下拉选择用） */
    @GetMapping("/list")
    public Result<List<SysDepartment>> list() {
        return Result.success(sysDepartmentService.listAll());
    }

    /** 分页查询部门 */
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<SysDepartment>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<SysDepartment> page = sysDepartmentService.pageList(pageNum, pageSize, keyword, status);
        // 填充部门经理名称
        page.getRecords().forEach(dept -> {
            if (dept.getManagerId() != null) {
                SysUser manager = sysUserService.getById(dept.getManagerId());
                if (manager != null) {
                    dept.setManagerName(manager.getRealName());
                }
            }
        });
        return Result.success(page);
    }

    /** 根据ID获取部门 */
    @GetMapping("/{id}")
    public Result<SysDepartment> getById(@PathVariable Long id) {
        return Result.success(sysDepartmentService.getById(id));
    }

    /** 新增部门 */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("新增部门")
    public Result<?> add(@RequestBody SysDepartment department) {
        sysDepartmentService.addDepartment(department);
        return Result.success("新增成功");
    }

    /** 更新部门 */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("更新部门")
    public Result<?> update(@RequestBody SysDepartment department) {
        sysDepartmentService.updateDepartment(department);
        return Result.success("更新成功");
    }

    /** 删除部门 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("删除部门")
    public Result<?> delete(@PathVariable Long id) {
        sysDepartmentService.deleteDepartment(id);
        return Result.success("删除成功");
    }
}
