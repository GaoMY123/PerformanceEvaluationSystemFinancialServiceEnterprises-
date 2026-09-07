package com.performance.security;

import com.performance.entity.SysUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecurityUtilsTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsNullWhenContextHasNoAuthentication() {
        SecurityContextHolder.clearContext();

        assertNull(SecurityUtils.getCurrentUser());
        assertNull(SecurityUtils.getCurrentUserId());
        assertNull(SecurityUtils.getCurrentUsername());
    }

    @Test
    void returnsCurrentLoginUserFromSecurityContext() {
        SysUser user = new SysUser();
        user.setId(7L);
        user.setUsername("alice");
        user.setRealName("Alice");
        user.setRole("EMPLOYEE");
        user.setStatus(1);
        LoginUser loginUser = new LoginUser(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        loginUser,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))));

        assertEquals(user, SecurityUtils.getCurrentUser());
        assertEquals(7L, SecurityUtils.getCurrentUserId().longValue());
        assertEquals("alice", SecurityUtils.getCurrentUsername());
    }
}
