package com.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.entity.SysAnnouncement;
import com.performance.entity.SysUser;
import com.performance.service.SysAnnouncementService;
import com.performance.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统公告控制器
 */
@RestController
@RequestMapping("/api/announcement")
public class SysAnnouncementController {

    @Autowired
    private SysAnnouncementService sysAnnouncementService;

    @Autowired
    private SysUserService sysUserService;

    /** 获取最新公告列表（首页展示） */
    @GetMapping("/latest")
    public Result<List<SysAnnouncement>> latest(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<SysAnnouncement> list = sysAnnouncementService.latestList(limit);
        list.forEach(a -> fillPublisherName(a));
        return Result.success(list);
    }

    /** 分页查询公告 */
    @GetMapping("/page")
    public Result<Page<SysAnnouncement>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<SysAnnouncement> page = sysAnnouncementService.pageList(pageNum, pageSize, keyword, status);
        page.getRecords().forEach(a -> fillPublisherName(a));
        return Result.success(page);
    }

    /** 根据ID获取公告详情 */
    @GetMapping("/{id}")
    public Result<SysAnnouncement> getById(@PathVariable Long id) {
        SysAnnouncement announcement = sysAnnouncementService.getById(id);
        if (announcement != null) {
            fillPublisherName(announcement);
        }
        return Result.success(announcement);
    }

    /** 新增公告（仅管理员） */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("发布公告")
    public Result<?> add(@RequestBody SysAnnouncement announcement) {
        sysAnnouncementService.addAnnouncement(announcement);
        return Result.success("发布成功");
    }

    /** 更新公告 */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("更新公告")
    public Result<?> update(@RequestBody SysAnnouncement announcement) {
        sysAnnouncementService.updateAnnouncement(announcement);
        return Result.success("更新成功");
    }

    /** 删除公告 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog("删除公告")
    public Result<?> delete(@PathVariable Long id) {
        sysAnnouncementService.deleteAnnouncement(id);
        return Result.success("删除成功");
    }

    /** 填充发布人名称 */
    private void fillPublisherName(SysAnnouncement announcement) {
        if (announcement != null && announcement.getPublisherId() != null) {
            SysUser publisher = sysUserService.getById(announcement.getPublisherId());
            if (publisher != null) {
                announcement.setPublisherName(publisher.getRealName());
            }
        }
    }
}
