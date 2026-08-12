package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.dto.LoginDTO;
import com.performance.dto.PasswordDTO;
import com.performance.dto.RegisterDTO;
import com.performance.entity.SysUser;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /** 用户登录，返回Token等信息 */
    Map<String, Object> login(LoginDTO dto);

    /** 用户注册 */
    void register(RegisterDTO dto);

    /** 修改密码 */
    void updatePassword(PasswordDTO dto);

    /** 分页查询用户列表 */
    Page<SysUser> pageList(Integer pageNum, Integer pageSize, String keyword, String role, Long departmentId, Integer status);

    /** 根据ID获取用户信息 */
    SysUser getUserById(Long id);

    /** 更新用户信息 */
    void updateUser(SysUser user);

    /** 重置密码（指定新密码） */
    void resetPassword(Long userId, String newPassword);

    /** 通过邮箱找回密码（重置为随机密码并返回） */
    String findPassword(String username, String email);
}
