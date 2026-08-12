package com.performance.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.common.exception.BusinessException;
import com.performance.dto.LoginDTO;
import com.performance.dto.PasswordDTO;
import com.performance.dto.RegisterDTO;
import com.performance.entity.SysUser;
import com.performance.mapper.SysUserMapper;
import com.performance.security.JwtTokenProvider;
import com.performance.security.SecurityUtils;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        // 使用Spring Security进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        // 认证成功，生成Token
        String token = jwtTokenProvider.generateToken(dto.getUsername());
        // 查询用户信息
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        long count = count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setDepartmentId(dto.getDepartmentId());
        user.setRole("EMPLOYEE"); // 注册用户默认为员工角色
        user.setStatus(1);
        save(user);
    }

    @Override
    public void updatePassword(PasswordDTO dto) {
        SysUser currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("用户未登录");
        }
        // 校验旧密码
        if (!passwordEncoder.matches(dto.getOldPassword(), currentUser.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        // 更新密码
        SysUser updateUser = new SysUser();
        updateUser.setId(currentUser.getId());
        updateUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        updateById(updateUser);
    }

    @Override
    public Page<SysUser> pageList(Integer pageNum, Integer pageSize, String keyword, String role, Long departmentId, Integer status) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 关键字模糊查询（用户名或真实姓名）
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword));
        }
        // 角色筛选
        if (StrUtil.isNotBlank(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        // 部门筛选
        if (departmentId != null) {
            wrapper.eq(SysUser::getDepartmentId, departmentId);
        }
        // 状态筛选
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public SysUser getUserById(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public void updateUser(SysUser user) {
        // 不允许通过此接口修改密码
        user.setPassword(null);
        updateById(user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    public String findPassword(String username, String email) {
        // 根据用户名和邮箱查找用户
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getEmail, email));
        if (user == null) {
            throw new BusinessException("用户名与邮箱不匹配");
        }
        // 生成随机新密码
        String newPassword = RandomUtil.randomString(8);
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        return newPassword;
    }
}
