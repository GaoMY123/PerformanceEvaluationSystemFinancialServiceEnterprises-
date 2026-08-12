package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.common.exception.BusinessException;
import com.performance.dto.RegisterDTO;
import com.performance.entity.SysUser;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器 - 管理员操作
 */
@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /** 分页查询用户列表 */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<Page<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status) {
        return Result.success(sysUserService.pageList(pageNum, pageSize, keyword, role, departmentId, status));
    }

    /** 根据ID获取用户信息 */
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getUserById(id));
    }

    /** 新增用户 */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("新增用户")
    public Result<?> add(@RequestBody SysUser user) {
        // 构建注册DTO
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername(user.getUsername());
        dto.setPassword("123456"); // 默认密码
        dto.setRealName(user.getRealName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setDepartmentId(user.getDepartmentId());
        sysUserService.register(dto);
        // 注册后补充角色和头像
        SysUser created = sysUserService.lambdaQuery().eq(SysUser::getUsername, user.getUsername()).one();
        if (created != null) {
            if (user.getRole() != null) {
                created.setRole(user.getRole());
            }
            if (user.getAvatar() != null) {
                created.setAvatar(user.getAvatar());
            }
            sysUserService.updateById(created);
        }
        return Result.success("新增成功，默认密码: 123456");
    }

    /** 更新用户信息 */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("更新用户信息")
    public Result<?> update(@RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return Result.success("更新成功");
    }

    /** 重置用户密码（支持自定义密码） */
    @PutMapping("/resetPassword/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("重置用户密码")
    public Result<?> resetPassword(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String newPassword = (body != null && body.get("password") != null) ? body.get("password") : "123456";
        sysUserService.resetPassword(id, newPassword);
        return Result.success("密码已重置为: " + newPassword);
    }

    /** 启用/禁用用户 */
    @PutMapping("/status/{id}/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("修改用户状态")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        // 管理员不能被禁用
        SysUser user = sysUserService.getUserById(id);
        if ("ADMIN".equals(user.getRole()) && status == 0) {
            throw new BusinessException("不能禁用管理员账号");
        }
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setStatus(status);
        sysUserService.updateById(updateUser);
        return Result.success("操作成功");
    }

    /** 删除用户 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("删除用户")
    public Result<?> delete(@PathVariable Long id) {
        // 管理员不能被删除
        SysUser user = sysUserService.getUserById(id);
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能删除管理员账号");
        }
        sysUserService.removeById(id);
        return Result.success("删除成功");
    }
}
