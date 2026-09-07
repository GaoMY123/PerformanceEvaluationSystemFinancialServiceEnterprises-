package com.performance.service.impl;

import com.performance.common.exception.BusinessException;
import com.performance.entity.Appeal;
import com.performance.entity.SysUser;
import com.performance.mapper.AppealMapper;
import com.performance.security.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppealServiceImplTest {

    private AppealMapper appealMapper;
    private AppealServiceImpl service;

    @BeforeEach
    void setUp() {
        appealMapper = mock(AppealMapper.class);
        service = new AppealServiceImpl();
        ReflectionTestUtils.setField(service, "baseMapper", appealMapper);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void submitAppealRejectsAnotherPendingAppealForSameTask() {
        loginAs(100L);
        when(appealMapper.selectCount(any())).thenReturn(1L);
        Appeal appeal = new Appeal();
        appeal.setTaskId(10L);
        appeal.setReason("Score is incorrect");

        assertThrows(BusinessException.class, () -> service.submitAppeal(appeal));

        verify(appealMapper, never()).insert(any());
    }

    @Test
    void submitAppealSetsCurrentUserAndPendingStatus() {
        loginAs(100L);
        when(appealMapper.selectCount(any())).thenReturn(0L);
        when(appealMapper.insert(any())).thenReturn(1);
        Appeal appeal = new Appeal();
        appeal.setTaskId(10L);
        appeal.setReason("Score is incorrect");

        service.submitAppeal(appeal);

        ArgumentCaptor<Appeal> captor = ArgumentCaptor.forClass(Appeal.class);
        verify(appealMapper).insert(captor.capture());
        Appeal saved = captor.getValue();
        assertEquals(100L, saved.getUserId().longValue());
        assertEquals(0, saved.getStatus());
        assertEquals("Score is incorrect", saved.getReason());
    }

    @Test
    void handleAppealRejectsMissingRecord() {
        when(appealMapper.selectById(anyLong())).thenReturn(null);

        assertThrows(BusinessException.class, () -> service.handleAppeal(1L, "Approved", 2));
    }

    @Test
    void handleAppealRejectsAlreadyFinishedRecord() {
        Appeal appeal = new Appeal();
        appeal.setId(1L);
        appeal.setStatus(2);
        when(appealMapper.selectById(1L)).thenReturn(appeal);

        assertThrows(BusinessException.class, () -> service.handleAppeal(1L, "Approved", 2));
    }

    @Test
    void handleAppealUpdatesReplyStatusAndHandler() {
        loginAs(100L);
        Appeal appeal = new Appeal();
        appeal.setId(1L);
        appeal.setStatus(0);
        when(appealMapper.selectById(1L)).thenReturn(appeal);
        when(appealMapper.updateById(any())).thenReturn(1);

        service.handleAppeal(1L, "Approved after review", 2);

        ArgumentCaptor<Appeal> captor = ArgumentCaptor.forClass(Appeal.class);
        verify(appealMapper).updateById(captor.capture());
        Appeal updated = captor.getValue();
        assertEquals("Approved after review", updated.getReply());
        assertEquals(2, updated.getStatus());
        assertEquals(100L, updated.getHandlerId().longValue());
        assertNotNull(updated.getHandleTime());
    }

    private void loginAs(Long userId) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setUsername("user" + userId);
        user.setRole("MANAGER");
        user.setStatus(1);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new LoginUser(user),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER"))));
    }
}
