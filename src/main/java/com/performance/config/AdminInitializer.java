package com.performance.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.performance.entity.SysUser;
import com.performance.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 系统启动时自动创建默认管理员账号
 * 如果数据库中不存在admin用户，则自动创建
 */
@Slf4j
@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 检查是否已存在管理员账号
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
        if (count == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRealName("系统管理员");
            admin.setEmail("admin@finance.com");
            admin.setPhone("13800000000");
            admin.setAvatar("https://api.dicebear.com/7.x/initials/svg?seed=Admin");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            sysUserMapper.insert(admin);
            log.info("========== 默认管理员账号创建成功 ==========");
            log.info("用户名: admin");
            log.info("密码: 123456");
            log.info("==========================================");
        } else {
            log.info("管理员账号已存在，跳过初始化");
        }
    }
}
