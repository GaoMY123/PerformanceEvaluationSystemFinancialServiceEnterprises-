package com.performance.service.impl;

import com.performance.common.exception.BusinessException;
import com.performance.dto.LoginDTO;
import com.performance.dto.PasswordDTO;
import com.performance.dto.RegisterDTO;
import com.performance.entity.SysUser;
import com.performance.mapper.SysUserMapper;
import com.performance.security.JwtTokenProvider;
import com.performance.security.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysUserServiceImplTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private SysUserMapper sysUserMapper;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private SysUserServiceImpl service;

    @BeforeEach
    void setUp() {
        sysUserMapper = mock(SysUserMapper.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        service = org.mockito.Mockito.spy(new SysUserServiceImpl());

        ReflectionTestUtils.setField(service, "baseMapper", sysUserMapper);
        ReflectionTestUtils.setField(service, "authenticationManager", authenticationManager);
        ReflectionTestUtils.setField(service, "jwtTokenProvider", jwtTokenProvider);
        ReflectionTestUtils.setField(service, "passwordEncoder", passwordEncoder);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerEncodesPasswordAndCreatesEnabledEmployee() {
        when(sysUserMapper.selectCount(any())).thenReturn(0L);
        when(sysUserMapper.insert(any())).thenReturn(1);
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("employee01");
        dto.setPassword("raw-password");
        dto.setRealName("Zhang San");
        dto.setEmail("employee01@example.com");

        service.register(dto);

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(sysUserMapper).insert(userCaptor.capture());
        SysUser saved = userCaptor.getValue();
        assertEquals("employee01", saved.getUsername());
        assertEquals("EMPLOYEE", saved.getRole());
        assertEquals(1, saved.getStatus());
        assertNotEquals("raw-password", saved.getPassword());
        assertTrue(passwordEncoder.matches("raw-password", saved.getPassword()));
    }

    @Test
    void registerRejectsDuplicateUsername() {
        when(sysUserMapper.selectCount(any())).thenReturn(1L);
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("employee01");
        dto.setPassword("raw-password");
        dto.setRealName("Zhang San");

        assertThrows(BusinessException.class, () -> service.register(dto));

        verify(sysUserMapper, never()).insert(any());
    }

    @Test
    void loginReturnsTokenAndCurrentUser() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("alice");
        dto.setPassword("secret");
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("alice");
        user.setRole("ADMIN");
        user.setStatus(1);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(jwtTokenProvider.generateToken("alice")).thenReturn("jwt-token");
        doReturn(user).when(service).getOne(any());

        Map<String, Object> result = service.login(dto);

        assertEquals("jwt-token", result.get("token"));
        assertEquals(user, result.get("user"));
    }

    @Test
    void findPasswordResetsAndSavesEncodedPasswordWhenMatch() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword(passwordEncoder.encode("old-password"));
        doReturn(user).when(service).getOne(any());
        when(sysUserMapper.updateById(any())).thenReturn(1);

        String newPassword = service.findPassword("alice", "alice@example.com");

        assertNotNull(newPassword);
        assertNotEquals("old-password", newPassword);
        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(sysUserMapper).updateById(captor.capture());
        assertTrue(passwordEncoder.matches(newPassword, captor.getValue().getPassword()));
    }

    @Test
    void findPasswordThrowsWhenUsernameAndEmailDoNotMatch() {
        doReturn(null).when(service).getOne(any());

        assertThrows(BusinessException.class,
                () -> service.findPassword("alice", "wrong@example.com"));

        verify(sysUserMapper, never()).updateById(any());
    }

    @Test
    void updatePasswordRejectsWrongOldPassword() {
        SysUser currentUser = new SysUser();
        currentUser.setId(1L);
        currentUser.setUsername("alice");
        currentUser.setPassword(passwordEncoder.encode("correct-old-password"));
        currentUser.setRole("EMPLOYEE");
        currentUser.setStatus(1);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new LoginUser(currentUser),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))));
        PasswordDTO dto = new PasswordDTO();
        dto.setOldPassword("wrong-old-password");
        dto.setNewPassword("new-password");

        assertThrows(BusinessException.class, () -> service.updatePassword(dto));

        verify(sysUserMapper, never()).updateById(any());
    }
}
