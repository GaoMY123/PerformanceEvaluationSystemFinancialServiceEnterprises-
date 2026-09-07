package com.performance.security;

import com.performance.entity.SysUser;
import com.performance.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private SysUserMapper sysUserMapper;
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        sysUserMapper = mock(SysUserMapper.class);
        service = new UserDetailsServiceImpl();
        ReflectionTestUtils.setField(service, "sysUserMapper", sysUserMapper);
    }

    @Test
    void loadUserByUsernameReturnsEnabledUserWithRoleAuthority() {
        SysUser user = user(1L, "alice", "HR", 1);
        when(sysUserMapper.selectOne(any())).thenReturn(user);

        LoginUser loginUser = (LoginUser) service.loadUserByUsername("alice");

        assertEquals("alice", loginUser.getUsername());
        Collection<? extends GrantedAuthority> authorities = loginUser.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.iterator().next().getAuthority().equals("ROLE_HR"));
    }

    @Test
    void loadUserByUsernameRejectsDisabledAccount() {
        when(sysUserMapper.selectOne(any())).thenReturn(user(1L, "alice", "EMPLOYEE", 0));

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("alice"));
    }

    @Test
    void loadUserByUsernameRejectsUnknownUser() {
        when(sysUserMapper.selectOne(any())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
    }

    private SysUser user(Long id, String username, String role, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
