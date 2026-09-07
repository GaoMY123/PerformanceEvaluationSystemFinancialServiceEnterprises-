package com.performance.service.impl;

import com.performance.common.exception.BusinessException;
import com.performance.dto.EvaluationScoreDTO;
import com.performance.entity.EvaluationTask;
import com.performance.entity.SysUser;
import com.performance.mapper.EvaluationAttachmentMapper;
import com.performance.mapper.EvaluationRuleMapper;
import com.performance.mapper.EvaluationScoreMapper;
import com.performance.mapper.EvaluationTaskMapper;
import com.performance.mapper.PeerAssignmentMapper;
import com.performance.mapper.SysUserMapper;
import com.performance.security.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EvaluationTaskServiceImplTest {

    private EvaluationTaskMapper taskMapper;
    private EvaluationScoreMapper scoreMapper;
    private EvaluationAttachmentMapper attachmentMapper;
    private PeerAssignmentMapper peerAssignmentMapper;
    private EvaluationTaskServiceImpl service;

    @BeforeEach
    void setUp() {
        taskMapper = mock(EvaluationTaskMapper.class);
        scoreMapper = mock(EvaluationScoreMapper.class);
        attachmentMapper = mock(EvaluationAttachmentMapper.class);
        peerAssignmentMapper = mock(PeerAssignmentMapper.class);
        service = new EvaluationTaskServiceImpl();

        ReflectionTestUtils.setField(service, "baseMapper", taskMapper);
        ReflectionTestUtils.setField(service, "evaluationScoreMapper", scoreMapper);
        ReflectionTestUtils.setField(service, "evaluationAttachmentMapper", attachmentMapper);
        ReflectionTestUtils.setField(service, "sysUserMapper", mock(SysUserMapper.class));
        ReflectionTestUtils.setField(service, "peerAssignmentMapper", peerAssignmentMapper);
        ReflectionTestUtils.setField(service, "evaluationRuleMapper", mock(EvaluationRuleMapper.class));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void submitScoreRejectsSelfEvaluationOfAnotherEmployee() {
        loginAs(100L);
        EvaluationTask task = task(10L, 200L, 0);
        when(taskMapper.selectById(10L)).thenReturn(task);

        assertThrows(BusinessException.class, () -> service.submitScore(dto(10L, "SELF")));

        verify(scoreMapper, never()).delete(any());
        verify(scoreMapper, never()).insert(any());
    }

    @Test
    void submitScoreRejectsSelfEvaluationOutsideWaitingStatus() {
        loginAs(100L);
        EvaluationTask task = task(10L, 100L, 1);
        when(taskMapper.selectById(10L)).thenReturn(task);

        assertThrows(BusinessException.class, () -> service.submitScore(dto(10L, "SELF")));
    }

    @Test
    void submitScoreRejectsManagerEvaluationWhenTaskNotWaitingForManager() {
        loginAs(100L);
        EvaluationTask task = task(10L, 200L, 0);
        when(taskMapper.selectById(10L)).thenReturn(task);

        assertThrows(BusinessException.class, () -> service.submitScore(dto(10L, "MANAGER")));
    }

    @Test
    void submitScoreRejectsPeerEvaluationWithoutAssignment() {
        loginAs(100L);
        EvaluationTask task = task(10L, 200L, 2);
        when(taskMapper.selectById(10L)).thenReturn(task);
        when(peerAssignmentMapper.selectCount(any())).thenReturn(0L);

        assertThrows(BusinessException.class, () -> service.submitScore(dto(10L, "PEER")));
    }

    private EvaluationScoreDTO dto(Long taskId, String scoreType) {
        EvaluationScoreDTO dto = new EvaluationScoreDTO();
        dto.setTaskId(taskId);
        dto.setScoreType(scoreType);
        return dto;
    }

    private EvaluationTask task(Long id, Long userId, int status) {
        EvaluationTask task = new EvaluationTask();
        task.setId(id);
        task.setPlanId(1L);
        task.setUserId(userId);
        task.setDepartmentId(2L);
        task.setStatus(status);
        return task;
    }

    private void loginAs(Long userId) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setUsername("user" + userId);
        user.setRole("EMPLOYEE");
        user.setStatus(1);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new LoginUser(user),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))));
    }
}
