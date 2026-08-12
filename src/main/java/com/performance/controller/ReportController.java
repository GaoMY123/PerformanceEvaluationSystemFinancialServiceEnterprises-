package com.performance.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.performance.annotation.OperationLog;
import com.performance.common.Result;
import com.performance.entity.*;
import com.performance.mapper.*;
import com.performance.service.SalaryAdjustmentService;
import com.performance.service.DevelopmentPlanService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表导出控制器 - 支持Excel和PDF导出
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private EvaluationTaskMapper evaluationTaskMapper;
    @Autowired
    private EvaluationPlanMapper evaluationPlanMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;
    @Autowired
    private SalaryAdjustmentService salaryAdjustmentService;
    @Autowired
    private DevelopmentPlanService developmentPlanService;

    /** 预览考核结果数据 */
    @GetMapping("/preview/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<List<Map<String, Object>>> preview(@PathVariable Long planId,
                                                      @RequestParam(required = false) Long departmentId) {
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getPlanId, planId)
                        .eq(EvaluationTask::getStatus, 3)
                        .orderByDesc(EvaluationTask::getFinalScore));

        List<Map<String, Object>> rows = new ArrayList<>();
        int seq = 1;
        for (EvaluationTask task : tasks) {
            SysUser user = sysUserMapper.selectById(task.getUserId());
            if (departmentId != null && (user == null || !departmentId.equals(user.getDepartmentId()))) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("seq", seq++);
            row.put("userName", user != null ? user.getRealName() : "");
            SysDepartment dept = (user != null && user.getDepartmentId() != null)
                    ? sysDepartmentMapper.selectById(user.getDepartmentId()) : null;
            row.put("departmentName", dept != null ? dept.getName() : "");
            row.put("selfScore", task.getSelfScore());
            row.put("managerScore", task.getManagerScore());
            row.put("peerScore", task.getPeerScore());
            row.put("finalScore", task.getFinalScore());
            row.put("grade", task.getGrade());
            rows.add(row);
        }
        return Result.success(rows);
    }

    /** 获取考核结果统计摘要 */
    @GetMapping("/stats/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<Map<String, Object>> stats(@PathVariable Long planId) {
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getPlanId, planId)
                        .eq(EvaluationTask::getStatus, 3));

        Map<String, Object> result = new HashMap<>();
        result.put("total", tasks.size());

        // 平均分
        double avgScore = tasks.stream()
                .filter(t -> t.getFinalScore() != null)
                .mapToDouble(t -> t.getFinalScore().doubleValue())
                .average().orElse(0);
        result.put("avgScore", Math.round(avgScore * 100.0) / 100.0);

        // 等级分布
        Map<String, Long> gradeDist = tasks.stream()
                .filter(t -> t.getGrade() != null)
                .collect(Collectors.groupingBy(EvaluationTask::getGrade, Collectors.counting()));
        result.put("gradeDist", gradeDist);

        // 部门平均分
        Map<String, Double> deptAvg = new LinkedHashMap<>();
        for (EvaluationTask task : tasks) {
            SysUser user = sysUserMapper.selectById(task.getUserId());
            if (user != null && user.getDepartmentId() != null) {
                SysDepartment dept = sysDepartmentMapper.selectById(user.getDepartmentId());
                if (dept != null) {
                    deptAvg.merge(dept.getName(),
                            task.getFinalScore() != null ? task.getFinalScore().doubleValue() : 0,
                            Double::sum);
                }
            }
        }
        // 计算部门人数和平均
        Map<String, int[]> deptCount = new HashMap<>();
        for (EvaluationTask task : tasks) {
            SysUser user = sysUserMapper.selectById(task.getUserId());
            if (user != null && user.getDepartmentId() != null) {
                SysDepartment dept = sysDepartmentMapper.selectById(user.getDepartmentId());
                if (dept != null) {
                    deptCount.merge(dept.getName(), new int[]{1}, (a, b) -> new int[]{a[0] + b[0]});
                }
            }
        }
        Map<String, Double> deptAvgFinal = new LinkedHashMap<>();
        deptAvg.forEach((name, sum) -> {
            int[] count = deptCount.getOrDefault(name, new int[]{1});
            deptAvgFinal.put(name, Math.round(sum / count[0] * 100.0) / 100.0);
        });
        result.put("deptAvg", deptAvgFinal);

        return Result.success(result);
    }

    /** 导出考核结果Excel */
    @GetMapping("/exportExcel/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("导出考核结果Excel")
    public void exportExcel(@PathVariable Long planId,
                            @RequestParam(required = false) Long departmentId,
                            HttpServletResponse response) throws IOException {
        EvaluationPlan plan = evaluationPlanMapper.selectById(planId);
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getPlanId, planId)
                        .eq(EvaluationTask::getStatus, 3)
                        .orderByDesc(EvaluationTask::getFinalScore));

        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("考核结果");

        // 创建标题样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // 写入表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "员工姓名", "所属部门", "自评分", "上级评分", "同事互评分", "最终得分", "绩效等级"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        // 写入数据行
        int rowNum = 1;
        for (EvaluationTask task : tasks) {
            SysUser user = sysUserMapper.selectById(task.getUserId());
            if (departmentId != null && (user == null || !departmentId.equals(user.getDepartmentId()))) {
                continue;
            }
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(user != null ? user.getRealName() : "");

            SysDepartment dept = (user != null && user.getDepartmentId() != null)
                    ? sysDepartmentMapper.selectById(user.getDepartmentId()) : null;
            row.createCell(2).setCellValue(dept != null ? dept.getName() : "");

            row.createCell(3).setCellValue(task.getSelfScore() != null ? task.getSelfScore().doubleValue() : 0);
            row.createCell(4).setCellValue(task.getManagerScore() != null ? task.getManagerScore().doubleValue() : 0);
            row.createCell(5).setCellValue(task.getPeerScore() != null ? task.getPeerScore().doubleValue() : 0);
            row.createCell(6).setCellValue(task.getFinalScore() != null ? task.getFinalScore().doubleValue() : 0);
            row.createCell(7).setCellValue(task.getGrade() != null ? task.getGrade() : "");

            rowNum++;
        }

        // 设置响应头
        String fileName = (plan != null ? plan.getName() : "考核结果") + "_" + DateUtil.format(new Date(), "yyyyMMdd") + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /** 导出考核结果PDF */
    @GetMapping("/exportPdf/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("导出考核结果PDF")
    public void exportPdf(@PathVariable Long planId,
                          @RequestParam(required = false) Long departmentId,
                          HttpServletResponse response) throws IOException {
        EvaluationPlan plan = evaluationPlanMapper.selectById(planId);
        List<EvaluationTask> tasks = evaluationTaskMapper.selectList(
                new LambdaQueryWrapper<EvaluationTask>()
                        .eq(EvaluationTask::getPlanId, planId)
                        .eq(EvaluationTask::getStatus, 3)
                        .orderByDesc(EvaluationTask::getFinalScore));

        // 设置响应头
        String fileName = (plan != null ? plan.getName() : "考核结果") + "_" + DateUtil.format(new Date(), "yyyyMMdd") + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

        // 创建PDF文档
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // 使用内置字体支持中文
        PdfFont font;
        try {
            font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
        } catch (Exception e) {
            font = PdfFontFactory.createFont();
        }
        document.setFont(font);

        // 标题
        document.add(new Paragraph((plan != null ? plan.getName() : "考核结果") + " 绩效考核报告")
                .setFontSize(16).setBold().setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("导出时间：" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")).setFontSize(10));
        document.add(new Paragraph(" "));

        // 创建表格
        float[] columnWidths = {40f, 80f, 80f, 60f, 60f, 70f, 60f, 50f};
        Table table = new Table(UnitValue.createPointArray(columnWidths));

        // 表头
        String[] headers = {"序号", "员工姓名", "所属部门", "自评分", "上级评分", "同事互评分", "最终得分", "绩效等级"};
        for (String header : headers) {
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(header).setBold().setFontSize(9)));
        }

        // 数据行
        int rowNum = 1;
        for (EvaluationTask task : tasks) {
            SysUser user = sysUserMapper.selectById(task.getUserId());
            if (departmentId != null && (user == null || !departmentId.equals(user.getDepartmentId()))) {
                continue;
            }
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(rowNum)).setFontSize(9)));

            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(user != null ? user.getRealName() : "").setFontSize(9)));

            SysDepartment dept = (user != null && user.getDepartmentId() != null)
                    ? sysDepartmentMapper.selectById(user.getDepartmentId()) : null;
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(dept != null ? dept.getName() : "").setFontSize(9)));

            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(task.getSelfScore() != null ? task.getSelfScore().toString() : "0").setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(task.getManagerScore() != null ? task.getManagerScore().toString() : "0").setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(task.getPeerScore() != null ? task.getPeerScore().toString() : "0").setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(task.getFinalScore() != null ? task.getFinalScore().toString() : "0").setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(task.getGrade() != null ? task.getGrade() : "").setFontSize(9)));

            rowNum++;
        }

        document.add(table);
        document.close();
    }

    /** 导出薪酬调整Excel */
    @GetMapping("/exportSalaryExcel/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("导出薪酬调整Excel")
    public void exportSalaryExcel(@PathVariable Long planId, HttpServletResponse response) throws IOException {
        EvaluationPlan plan = evaluationPlanMapper.selectById(planId);
        Page<SalaryAdjustment> page = salaryAdjustmentService.pageList(1, 10000, planId, null, null, null);
        List<SalaryAdjustment> list = page.getRecords();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("薪酬调整");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "员工姓名", "所属部门", "绩效等级", "考核得分", "基本薪资", "调整比例(%)", "奖金", "建议", "状态"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        String[] statusMap = {"待审批", "已通过", "已驳回"};
        int rowNum = 1;
        for (SalaryAdjustment sa : list) {
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum);

            SysUser user = sysUserMapper.selectById(sa.getUserId());
            row.createCell(1).setCellValue(user != null ? user.getRealName() : "");

            SysDepartment dept = (user != null && user.getDepartmentId() != null)
                    ? sysDepartmentMapper.selectById(user.getDepartmentId()) : null;
            row.createCell(2).setCellValue(dept != null ? dept.getName() : "");

            row.createCell(3).setCellValue(sa.getGrade() != null ? sa.getGrade() : "");
            row.createCell(4).setCellValue(sa.getFinalScore() != null ? sa.getFinalScore().doubleValue() : 0);
            row.createCell(5).setCellValue(sa.getBaseSalary() != null ? sa.getBaseSalary().doubleValue() : 0);
            row.createCell(6).setCellValue(sa.getAdjustmentRate() != null ? sa.getAdjustmentRate().doubleValue() : 0);
            row.createCell(7).setCellValue(sa.getBonusAmount() != null ? sa.getBonusAmount().doubleValue() : 0);
            row.createCell(8).setCellValue(sa.getSuggestion() != null ? sa.getSuggestion() : "");
            row.createCell(9).setCellValue(sa.getStatus() != null && sa.getStatus() < statusMap.length ? statusMap[sa.getStatus()] : "");

            rowNum++;
        }

        String fileName = (plan != null ? plan.getName() : "薪酬调整") + "_薪酬调整_" + DateUtil.format(new Date(), "yyyyMMdd") + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /** 导出发展计划Excel */
    @GetMapping("/exportIdpExcel/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @OperationLog("导出发展计划Excel")
    public void exportIdpExcel(@PathVariable Long planId, HttpServletResponse response) throws IOException {
        EvaluationPlan plan = evaluationPlanMapper.selectById(planId);
        Page<DevelopmentPlan> page = developmentPlanService.pageList(1, 10000, planId, null, null, null);
        List<DevelopmentPlan> list = page.getRecords();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("发展计划");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "员工姓名", "所属部门", "优势", "不足", "培训建议", "发展目标", "行动计划", "状态"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        String[] statusMap = {"草稿", "已确认", "执行中", "已完成"};
        int rowNum = 1;
        for (DevelopmentPlan dp : list) {
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum);

            SysUser user = sysUserMapper.selectById(dp.getUserId());
            row.createCell(1).setCellValue(user != null ? user.getRealName() : "");

            SysDepartment dept = (user != null && user.getDepartmentId() != null)
                    ? sysDepartmentMapper.selectById(user.getDepartmentId()) : null;
            row.createCell(2).setCellValue(dept != null ? dept.getName() : "");

            row.createCell(3).setCellValue(dp.getStrengths() != null ? dp.getStrengths() : "");
            row.createCell(4).setCellValue(dp.getWeaknesses() != null ? dp.getWeaknesses() : "");
            row.createCell(5).setCellValue(dp.getTrainingSuggestion() != null ? dp.getTrainingSuggestion() : "");
            row.createCell(6).setCellValue(dp.getDevelopmentGoal() != null ? dp.getDevelopmentGoal() : "");
            row.createCell(7).setCellValue(dp.getActionPlan() != null ? dp.getActionPlan() : "");
            row.createCell(8).setCellValue(dp.getStatus() != null && dp.getStatus() < statusMap.length ? statusMap[dp.getStatus()] : "");

            rowNum++;
        }

        String fileName = (plan != null ? plan.getName() : "发展计划") + "_发展计划_" + DateUtil.format(new Date(), "yyyyMMdd") + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
