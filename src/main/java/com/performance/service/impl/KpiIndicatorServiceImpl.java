package com.performance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.common.exception.BusinessException;
import com.performance.entity.KpiIndicator;
import com.performance.mapper.KpiIndicatorMapper;
import com.performance.service.KpiIndicatorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * KPI指标库服务实现类
 */
@Service
public class KpiIndicatorServiceImpl extends ServiceImpl<KpiIndicatorMapper, KpiIndicator> implements KpiIndicatorService {

    @Override
    public Page<KpiIndicator> pageList(Integer pageNum, Integer pageSize, String keyword, String category, Integer status) {
        Page<KpiIndicator> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<KpiIndicator> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(KpiIndicator::getName, keyword);
        }
        if (StrUtil.isNotBlank(category)) {
            wrapper.eq(KpiIndicator::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(KpiIndicator::getStatus, status);
        }
        wrapper.orderByDesc(KpiIndicator::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public List<KpiIndicator> listEnabled() {
        return list(new LambdaQueryWrapper<KpiIndicator>().eq(KpiIndicator::getStatus, 1));
    }

    @Override
    public void addIndicator(KpiIndicator indicator) {
        // 检查指标名称是否重复
        long count = count(new LambdaQueryWrapper<KpiIndicator>().eq(KpiIndicator::getName, indicator.getName()));
        if (count > 0) {
            throw new BusinessException("指标名称已存在");
        }
        save(indicator);
    }

    @Override
    public void updateIndicator(KpiIndicator indicator) {
        // 检查指标名称是否与其他指标重复
        long count = count(new LambdaQueryWrapper<KpiIndicator>()
                .eq(KpiIndicator::getName, indicator.getName())
                .ne(KpiIndicator::getId, indicator.getId()));
        if (count > 0) {
            throw new BusinessException("指标名称已存在");
        }
        updateById(indicator);
    }

    @Override
    public void deleteIndicator(Long id) {
        removeById(id);
    }
}
