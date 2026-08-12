package com.performance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.performance.entity.SysAnnouncement;
import com.performance.mapper.SysAnnouncementMapper;
import com.performance.security.SecurityUtils;
import com.performance.service.SysAnnouncementService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统公告服务实现类
 */
@Service
public class SysAnnouncementServiceImpl extends ServiceImpl<SysAnnouncementMapper, SysAnnouncement> implements SysAnnouncementService {

    @Override
    public Page<SysAnnouncement> pageList(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        Page<SysAnnouncement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysAnnouncement> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(SysAnnouncement::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(SysAnnouncement::getStatus, status);
        }
        wrapper.orderByDesc(SysAnnouncement::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public List<SysAnnouncement> latestList(Integer limit) {
        // 查询最新的已发布公告
        return list(new LambdaQueryWrapper<SysAnnouncement>()
                .eq(SysAnnouncement::getStatus, 1)
                .orderByDesc(SysAnnouncement::getCreateTime)
                .last("LIMIT " + limit));
    }

    @Override
    public void addAnnouncement(SysAnnouncement announcement) {
        announcement.setPublisherId(SecurityUtils.getCurrentUserId());
        save(announcement);
    }

    @Override
    public void updateAnnouncement(SysAnnouncement announcement) {
        updateById(announcement);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        removeById(id);
    }
}
