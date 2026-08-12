package com.performance.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.performance.common.exception.BusinessException;
import com.performance.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 文件上传服务实现类
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        // 获取原始文件名和后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

        // 按日期分目录存储，使用UUID避免文件名冲突
        String datePath = DateUtil.format(new Date(), "yyyy/MM/dd");
        String fileName = IdUtil.simpleUUID() + suffix;
        String relativePath = datePath + "/" + fileName;

        // 创建目录（使用绝对路径，避免 transferTo 相对路径问题）
        File dest = new File(uploadPath, relativePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest.getAbsoluteFile());
        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }

        // 返回可访问的相对路径
        return "/uploads/" + relativePath;
    }
}
