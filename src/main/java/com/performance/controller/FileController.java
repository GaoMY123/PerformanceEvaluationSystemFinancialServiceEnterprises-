package com.performance.controller;

import com.performance.common.Result;
import com.performance.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器（头像、证明材料等通用文件上传）
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /** 通用文件上传 */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String path = fileService.upload(file);
        return Result.success("上传成功", path);
    }
}
