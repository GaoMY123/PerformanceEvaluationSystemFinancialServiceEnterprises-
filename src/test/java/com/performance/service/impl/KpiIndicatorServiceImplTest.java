package com.performance.service.impl;

import com.performance.common.exception.BusinessException;
import com.performance.entity.KpiIndicator;
import com.performance.mapper.KpiIndicatorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KpiIndicatorServiceImplTest {

    private KpiIndicatorMapper mapper;
    private KpiIndicatorServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper = mock(KpiIndicatorMapper.class);
        service = new KpiIndicatorServiceImpl();
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
    }

    @Test
    void addIndicatorRejectsDuplicateName() {
        when(mapper.selectCount(any())).thenReturn(1L);
        KpiIndicator indicator = indicator(1L, "Net interest margin");

        assertThrows(BusinessException.class, () -> service.addIndicator(indicator));

        verify(mapper, never()).insert(any());
    }

    @Test
    void addIndicatorSavesUniqueName() {
        when(mapper.selectCount(any())).thenReturn(0L);
        when(mapper.insert(any())).thenReturn(1);

        service.addIndicator(indicator(1L, "Net interest margin"));

        verify(mapper).insert(any());
    }

    @Test
    void updateIndicatorRejectsNameUsedByAnotherIndicator() {
        when(mapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class,
                () -> service.updateIndicator(indicator(1L, "Duplicate name")));

        verify(mapper, never()).updateById(any());
    }

    @Test
    void updateIndicatorAllowsUnchangedName() {
        when(mapper.selectCount(any())).thenReturn(0L);
        when(mapper.updateById(any())).thenReturn(1);

        service.updateIndicator(indicator(1L, "Net interest margin"));

        verify(mapper).updateById(any());
    }

    @Test
    void listEnabledReturnsOnlyEnabledIndicators() {
        KpiIndicator enabled = indicator(1L, "Net interest margin");
        when(mapper.selectList(any())).thenReturn(Collections.singletonList(enabled));

        List<KpiIndicator> result = service.listEnabled();

        assertEquals(1, result.size());
        assertEquals("Net interest margin", result.get(0).getName());
    }

    private KpiIndicator indicator(Long id, String name) {
        KpiIndicator indicator = new KpiIndicator();
        indicator.setId(id);
        indicator.setName(name);
        return indicator;
    }
}
