package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.entity.SysAnnouncement;

import java.util.List;

/**
 * 系统公告服务接口
 */
public interface SysAnnouncementService extends IService<SysAnnouncement> {

    /** 分页查询公告 */
    Page<SysAnnouncement> pageList(Integer pageNum, Integer pageSize, String keyword, Integer status);

    /** 获取最新已发布公告列表（首页展示） */
    List<SysAnnouncement> latestList(Integer limit);

    /** 发布公告 */
    void addAnnouncement(SysAnnouncement announcement);

    /** 更新公告 */
    void updateAnnouncement(SysAnnouncement announcement);

    /** 删除公告 */
    void deleteAnnouncement(Long id);
}
