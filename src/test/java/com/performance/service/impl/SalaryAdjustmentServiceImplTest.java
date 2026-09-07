package com.performance.service.impl;

import com.performance.common.exception.BusinessException;
import com.performance.entity.EvaluationTask;
import com.performance.entity.SalaryAdjustment;
import com.performance.mapper.EvaluationTaskMapper;
import com.performance.mapper.SalaryAdjustmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
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

class SalaryAdjustmentServiceImplTest {

    private SalaryAdjustmentMapper adjustmentMapper;
    private EvaluationTaskMapper taskMapper;
    private SalaryAdjustmentServiceImpl service;

    @BeforeEach
    void setUp() {
        adjustmentMapper = mock(SalaryAdjustmentMapper.class);
        taskMapper = mock(EvaluationTaskMapper.class);
        service = new SalaryAdjustmentServiceImpl();

        ReflectionTestUtils.setField(service, "baseMapper", adjustmentMapper);
        ReflectionTestUtils.setField(service, "evaluationTaskMapper", taskMapper);
    }

    @Test
    void generateByPlanCreatesGradeASuggestionWithRate() {
        EvaluationTask task = new EvaluationTask();
        task.setId(10L);
        task.setPlanId(1L);
        task.setUserId(20L);
        task.setGrade("A");
        task.setFinalScore(new BigDecimal("95.00"));
        task.setStatus(3);
        when(taskMapper.selectList(any())).thenReturn(Collections.singletonList(task));
        when(adjustmentMapper.selectCount(any())).thenReturn(0L);
        when(adjustmentMapper.insert(any())).thenReturn(1);

        service.generateByPlan(1L);

        ArgumentCaptor<SalaryAdjustment> captor = ArgumentCaptor.forClass(SalaryAdjustment.class);
        verify(adjustmentMapper).insert(captor.capture());
        SalaryAdjustment saved = captor.getValue();
        assertEquals("A", saved.getGrade());
        assertEquals(0, saved.getStatus());
        assertEquals(0, saved.getAdjustmentRate().compareTo(new BigDecimal("15.00")));
        assertNotNull(saved.getSuggestion());
        assertEquals(10L, saved.getTaskId().longValue());
    }

    @Test
    void generateByPlanSkipsTasksWithExistingAdjustment() {
        EvaluationTask task = new EvaluationTask();
        task.setId(10L);
        task.setPlanId(1L);
        task.setUserId(20L);
        task.setGrade("C");
        task.setStatus(3);
        when(taskMapper.selectList(any())).thenReturn(Collections.singletonList(task));
        when(adjustmentMapper.selectCount(any())).thenReturn(1L);

        service.generateByPlan(1L);

        verify(adjustmentMapper, never()).insert(any());
    }

    @Test
    void updateAdjustmentRejectsAlreadyProcessedRecord() {
        SalaryAdjustment existing = new SalaryAdjustment();
        existing.setId(1L);
        existing.setStatus(2);
        when(adjustmentMapper.selectById(1L)).thenReturn(existing);

        SalaryAdjustment input = new SalaryAdjustment();
        input.setId(1L);
        input.setStatus(2);

        assertThrows(BusinessException.class, () -> service.updateAdjustment(input));
    }

    @Test
    void approveRejectsMissingRecord() {
        when(adjustmentMapper.selectById(anyLong())).thenReturn(null);

        assertThrows(BusinessException.class, () -> service.approve(99L, 1));
    }
}
