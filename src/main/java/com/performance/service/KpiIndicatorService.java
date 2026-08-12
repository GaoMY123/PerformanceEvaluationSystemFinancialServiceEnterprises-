package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.entity.KpiIndicator;

import java.util.List;

/**
 * KPI指标库服务接口
 */
public interface KpiIndicatorService extends IService<KpiIndicator> {

    /** 分页查询KPI指标 */
    Page<KpiIndicator> pageList(Integer pageNum, Integer pageSize, String keyword, String category, Integer status);

    /** 获取所有启用的指标列表 */
    List<KpiIndicator> listEnabled();

    /** 新增指标 */
    void addIndicator(KpiIndicator indicator);

    /** 更新指标 */
    void updateIndicator(KpiIndicator indicator);

    /** 删除指标 */
    void deleteIndicator(Long id);
}
