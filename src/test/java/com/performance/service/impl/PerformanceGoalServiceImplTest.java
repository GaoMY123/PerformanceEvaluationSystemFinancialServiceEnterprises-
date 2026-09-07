package com.performance.service.impl;

import com.performance.common.exception.BusinessException;
import com.performance.entity.GoalKpi;
import com.performance.mapper.GoalKpiMapper;
import com.performance.mapper.PerformanceGoalMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PerformanceGoalServiceImplTest {

    private PerformanceGoalMapper goalMapper;
    private GoalKpiMapper goalKpiMapper;
    private PerformanceGoalServiceImpl service;

    @BeforeEach
    void setUp() {
        goalMapper = mock(PerformanceGoalMapper.class);
        goalKpiMapper = mock(GoalKpiMapper.class);
        service = org.mockito.Mockito.spy(new PerformanceGoalServiceImpl());

        ReflectionTestUtils.setField(service, "baseMapper", goalMapper);
        ReflectionTestUtils.setField(service, "goalKpiMapper", goalKpiMapper);
    }

    @Test
    void deleteGoalRejectsGoalWithChildren() {
        when(goalMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> service.deleteGoal(1L));

        verify(goalKpiMapper, never()).delete(any());
        verify(goalMapper, never()).deleteById(anyLong());
    }

    @Test
    void deleteGoalRemovesLinkedKpisAndGoal() {
        when(goalMapper.selectCount(any())).thenReturn(0L);
        when(goalKpiMapper.delete(any())).thenReturn(1);
        doReturn(true).when(service).removeById(1L);

        service.deleteGoal(1L);

        verify(goalKpiMapper).delete(any());
        verify(service).removeById(1L);
    }

    @Test
    void bindKpiRejectsDuplicateBinding() {
        GoalKpi goalKpi = new GoalKpi();
        goalKpi.setGoalId(1L);
        goalKpi.setKpiId(2L);
        when(goalKpiMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> service.bindKpi(goalKpi));

        verify(goalKpiMapper, never()).insert(any());
    }

    @Test
    void bindKpiSavesWhenNotYetBound() {
        GoalKpi goalKpi = new GoalKpi();
        goalKpi.setGoalId(1L);
        goalKpi.setKpiId(2L);
        when(goalKpiMapper.selectCount(any())).thenReturn(0L);
        when(goalKpiMapper.insert(any())).thenReturn(1);

        service.bindKpi(goalKpi);

        verify(goalKpiMapper).insert(goalKpi);
    }
}
