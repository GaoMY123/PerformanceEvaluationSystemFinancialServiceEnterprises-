package com.performance.service.impl;

import com.performance.common.exception.BusinessException;
import com.performance.entity.EvaluationPlan;
import com.performance.entity.EvaluationRule;
import com.performance.entity.EvaluationScore;
import com.performance.entity.EvaluationTask;
import com.performance.mapper.EvaluationPlanMapper;
import com.performance.mapper.EvaluationRuleMapper;
import com.performance.mapper.EvaluationScoreMapper;
import com.performance.mapper.EvaluationTaskMapper;
import com.performance.mapper.PeerAssignmentMapper;
import com.performance.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EvaluationPlanServiceImplTest {

    private EvaluationPlanMapper planMapper;
    private EvaluationRuleMapper ruleMapper;
    private EvaluationTaskMapper taskMapper;
    private EvaluationScoreMapper scoreMapper;
    private EvaluationPlanServiceImpl service;

    @BeforeEach
    void setUp() {
        planMapper = mock(EvaluationPlanMapper.class);
        ruleMapper = mock(EvaluationRuleMapper.class);
        taskMapper = mock(EvaluationTaskMapper.class);
        scoreMapper = mock(EvaluationScoreMapper.class);
        service = new EvaluationPlanServiceImpl();

        ReflectionTestUtils.setField(service, "baseMapper", planMapper);
        ReflectionTestUtils.setField(service, "evaluationRuleMapper", ruleMapper);
        ReflectionTestUtils.setField(service, "evaluationTaskMapper", taskMapper);
        ReflectionTestUtils.setField(service, "evaluationScoreMapper", scoreMapper);
        ReflectionTestUtils.setField(service, "sysUserMapper", mock(SysUserMapper.class));
        ReflectionTestUtils.setField(service, "peerAssignmentMapper", mock(PeerAssignmentMapper.class));
    }

    @Test
    void completePlanCalculatesWeightedFinalScoreAndGrade() {
        EvaluationPlan plan = activePlan();
        EvaluationTask task = new EvaluationTask();
        task.setId(10L);
        task.setPlanId(1L);
        task.setUserId(20L);
        EvaluationRule rule = new EvaluationRule();
        rule.setId(1L);
        rule.setPlanId(1L);
        rule.setKpiId(100L);
        rule.setWeight(new BigDecimal("100"));

        when(planMapper.selectById(1L)).thenReturn(plan);
        when(taskMapper.selectList(any())).thenReturn(Collections.singletonList(task));
        when(ruleMapper.selectList(any())).thenReturn(Collections.singletonList(rule));
        when(scoreMapper.selectList(any())).thenReturn(
                Collections.singletonList(score(new BigDecimal("90"))),
                Collections.singletonList(score(new BigDecimal("80"))),
                Collections.singletonList(score(new BigDecimal("70"))));
        when(planMapper.updateById(plan)).thenReturn(1);

        service.completePlan(1L);

        ArgumentCaptor<EvaluationTask> taskCaptor = ArgumentCaptor.forClass(EvaluationTask.class);
        verify(taskMapper).updateById(taskCaptor.capture());
        EvaluationTask updated = taskCaptor.getValue();
        assertEquals(3, updated.getStatus());
        assertEquals("B", updated.getGrade());
        assertEquals(0, updated.getFinalScore().compareTo(new BigDecimal("83.00")));
        assertEquals(2, plan.getStatus());
    }

    @Test
    void completePlanRejectsPlanNotInProgress() {
        EvaluationPlan plan = activePlan();
        plan.setStatus(0);
        when(planMapper.selectById(1L)).thenReturn(plan);

        assertThrows(BusinessException.class, () -> service.completePlan(1L));

        verify(taskMapper, never()).selectList(any());
    }

    @Test
    void addRuleRejectsDuplicateKpiForSamePlan() {
        EvaluationRule rule = new EvaluationRule();
        rule.setPlanId(1L);
        rule.setKpiId(100L);
        when(ruleMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> service.addRule(rule));

        verify(ruleMapper, never()).insert(any());
    }

    @Test
    void deletePlanRejectsStartedPlan() {
        EvaluationPlan plan = activePlan();
        when(planMapper.selectById(anyLong())).thenReturn(plan);

        assertThrows(BusinessException.class, () -> service.deletePlan(1L));

        verify(ruleMapper, never()).delete(any());
        verify(planMapper, never()).deleteById(anyLong());
    }

    private EvaluationPlan activePlan() {
        EvaluationPlan plan = new EvaluationPlan();
        plan.setId(1L);
        plan.setName("2026 annual review");
        plan.setStatus(1);
        plan.setSelfWeight(new BigDecimal("50"));
        plan.setManagerWeight(new BigDecimal("30"));
        plan.setPeerWeight(new BigDecimal("20"));
        plan.setGradeAMin(new BigDecimal("90"));
        plan.setGradeBMin(new BigDecimal("80"));
        plan.setGradeCMin(new BigDecimal("70"));
        return plan;
    }

    private EvaluationScore score(BigDecimal value) {
        EvaluationScore score = new EvaluationScore();
        score.setScore(value);
        return score;
    }
}
