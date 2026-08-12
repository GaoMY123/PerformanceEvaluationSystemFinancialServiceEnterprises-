package com.performance.controller;

import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.dto.LoginDTO;
import com.performance.dto.PasswordDTO;
import com.performance.dto.RegisterDTO;
import com.performance.entity.SysUser;
import com.performance.security.SecurityUtils;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器 - 登录、注册、密码管理
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    /** 用户登录 */
    @PostMapping("/login")
    @OperationLog("用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        Map<String, Object> data = sysUserService.login(dto);
        return Result.success("登录成功", data);
    }

    /** 用户注册 */
    @PostMapping("/register")
    @OperationLog("用户注册")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        sysUserService.register(dto);
        return Result.success("注册成功");
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/info")
    public Result<SysUser> getCurrentUser() {
        SysUser user = SecurityUtils.getCurrentUser();
        return Result.success(user);
    }

    /** 修改密码 */
    @PutMapping("/password")
    @OperationLog("修改密码")
    public Result<?> updatePassword(@Valid @RequestBody PasswordDTO dto) {
        sysUserService.updatePassword(dto);
        return Result.success("密码修改成功");
    }

    /** 密码找回（通过用户名+邮箱验证） */
    @PostMapping("/findPassword")
    public Result<String> findPassword(@RequestParam String username, @RequestParam String email) {
        String newPassword = sysUserService.findPassword(username, email);
        return Result.success("密码已重置，新密码为：" + newPassword, newPassword);
    }

    /** 更新个人信息 */
    @PutMapping("/profile")
    @OperationLog("更新个人信息")
    public Result<?> updateProfile(@RequestBody SysUser user) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        user.setId(currentUserId);
        // 不允许修改角色和状态
        user.setRole(null);
        user.setStatus(null);
        sysUserService.updateUser(user);
        return Result.success("更新成功");
    }
}
