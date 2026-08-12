-- ========================================
-- 金融服务类企业绩效考核系统 - 数据库初始化脚本
-- ========================================

CREATE DATABASE IF NOT EXISTS `performance_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `performance_db`;

-- ========================================
-- 1. 系统基础模块
-- ========================================

-- 部门表
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '上级部门ID，0表示顶级部门',
    `manager_id` BIGINT DEFAULT NULL COMMENT '部门经理用户ID',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '部门描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(200) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
    `department_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
    `role` VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE' COMMENT '角色：ADMIN-系统管理员 HR-HR专员 MANAGER-部门经理 EMPLOYEE-员工',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 系统公告表
DROP TABLE IF EXISTS `sys_announcement`;
CREATE TABLE `sys_announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-已发布',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- 操作日志表（满足金融合规性要求）
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    `operation` VARCHAR(200) DEFAULT NULL COMMENT '操作描述',
    `method` VARCHAR(300) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `result` TINYINT DEFAULT NULL COMMENT '操作结果：0-失败 1-成功',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ========================================
-- 2. 绩效目标管理模块
-- ========================================

-- KPI指标库（含金融行业特有指标）
DROP TABLE IF EXISTS `kpi_indicator`;
CREATE TABLE `kpi_indicator` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '指标ID',
    `name` VARCHAR(100) NOT NULL COMMENT '指标名称',
    `category` VARCHAR(50) NOT NULL COMMENT '指标分类：FINANCIAL-财务类 COMPLIANCE-合规类 RISK-风控类 CUSTOMER-客户类 OPERATION-运营类',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '指标描述',
    `formula` VARCHAR(500) DEFAULT NULL COMMENT '计算公式',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位（如：%、分、元）',
    `target_value` DECIMAL(12,2) DEFAULT NULL COMMENT '目标值',
    `max_score` DECIMAL(8,2) DEFAULT 100.00 COMMENT '最高分',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='KPI指标库';

-- 绩效目标表（支持公司→部门→个人逐级分解）
DROP TABLE IF EXISTS `performance_goal`;
CREATE TABLE `performance_goal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '目标ID',
    `title` VARCHAR(200) NOT NULL COMMENT '目标标题',
    `description` TEXT DEFAULT NULL COMMENT '目标描述',
    `level` VARCHAR(20) NOT NULL COMMENT '目标层级：COMPANY-公司级 DEPARTMENT-部门级 PERSONAL-个人级',
    `parent_id` BIGINT DEFAULT 0 COMMENT '上级目标ID，0表示顶级',
    `department_id` BIGINT DEFAULT NULL COMMENT '所属部门ID（部门级和个人级）',
    `user_id` BIGINT DEFAULT NULL COMMENT '责任人ID（个人级）',
    `year` INT NOT NULL COMMENT '年度',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已完成',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_department_id` (`department_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绩效目标表';

-- 目标与KPI指标关联表
DROP TABLE IF EXISTS `goal_kpi`;
CREATE TABLE `goal_kpi` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `goal_id` BIGINT NOT NULL COMMENT '目标ID',
    `kpi_id` BIGINT NOT NULL COMMENT 'KPI指标ID',
    `target_value` DECIMAL(12,2) DEFAULT NULL COMMENT '该目标下的指标目标值',
    `weight` DECIMAL(5,2) DEFAULT 0.00 COMMENT '权重（百分比，如30.00表示30%）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_goal_id` (`goal_id`),
    KEY `idx_kpi_id` (`kpi_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目标与KPI指标关联表';

-- ========================================
-- 3. 绩效考核执行模块
-- ========================================

-- 考核方案表
DROP TABLE IF EXISTS `evaluation_plan`;
CREATE TABLE `evaluation_plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '方案ID',
    `name` VARCHAR(200) NOT NULL COMMENT '方案名称',
    `year` INT NOT NULL COMMENT '年度',
    `period_type` VARCHAR(20) NOT NULL COMMENT '考核周期：MONTHLY-月度 QUARTERLY-季度 ANNUALLY-年度',
    `period_number` INT DEFAULT NULL COMMENT '周期编号（第几月/第几季度，年度为空）',
    `start_date` DATE NOT NULL COMMENT '考核开始日期',
    `end_date` DATE NOT NULL COMMENT '考核结束日期',
    `grade_a_min` DECIMAL(5,2) DEFAULT 90.00 COMMENT 'A等级最低分',
    `grade_b_min` DECIMAL(5,2) DEFAULT 75.00 COMMENT 'B等级最低分',
    `grade_c_min` DECIMAL(5,2) DEFAULT 60.00 COMMENT 'C等级最低分',
    `self_weight` DECIMAL(5,2) DEFAULT 20.00 COMMENT '自评权重（%）',
    `manager_weight` DECIMAL(5,2) DEFAULT 60.00 COMMENT '上级评权重（%）',
    `peer_weight` DECIMAL(5,2) DEFAULT 20.00 COMMENT '同事互评权重（%）',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-草稿 1-进行中 2-已完成',
    `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核方案表';

-- 考核评分规则表（方案与KPI的权重配置）
DROP TABLE IF EXISTS `evaluation_rule`;
CREATE TABLE `evaluation_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `plan_id` BIGINT NOT NULL COMMENT '考核方案ID',
    `kpi_id` BIGINT NOT NULL COMMENT 'KPI指标ID',
    `weight` DECIMAL(5,2) NOT NULL COMMENT '该指标在方案中的权重（%）',
    `target_value` DECIMAL(12,2) DEFAULT NULL COMMENT '目标值',
    `scoring_formula` VARCHAR(50) DEFAULT 'WEIGHTED_SUM' COMMENT '评分方式：WEIGHTED_SUM-加权求和 PERCENTILE-百分位排名',
    `max_score` DECIMAL(8,2) DEFAULT 100.00 COMMENT '该指标最高分',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核评分规则表';

USE performance_db;

CREATE TABLE `peer_assignment` (
   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
   `plan_id` BIGINT NOT NULL COMMENT '考核方案ID',
   `task_id` BIGINT NOT NULL COMMENT '被考核人的任务ID',
   `evaluatee_id` BIGINT NOT NULL COMMENT '被评价人ID',
   `evaluator_id` BIGINT NOT NULL COMMENT '互评人ID',
   `status` TINYINT DEFAULT 0 COMMENT '状态：0-待互评 1-已完成',
   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`),
   KEY `idx_plan_id` (`plan_id`),
   KEY `idx_evaluator_id` (`evaluator_id`),
   KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='互评分配表';

-- 考核任务表（每个被考核人一条记录）
DROP TABLE IF EXISTS `evaluation_task`;
CREATE TABLE `evaluation_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `plan_id` BIGINT NOT NULL COMMENT '考核方案ID',
    `user_id` BIGINT NOT NULL COMMENT '被考核人ID',
    `department_id` BIGINT DEFAULT NULL COMMENT '被考核人所属部门ID',
    `self_score` DECIMAL(8,2) DEFAULT NULL COMMENT '自评总分',
    `manager_score` DECIMAL(8,2) DEFAULT NULL COMMENT '上级评总分',
    `peer_score` DECIMAL(8,2) DEFAULT NULL COMMENT '同事互评总分',
    `final_score` DECIMAL(8,2) DEFAULT NULL COMMENT '最终得分',
    `grade` VARCHAR(5) DEFAULT NULL COMMENT '绩效等级：A/B/C/D',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待自评 1-待上级评 2-待互评 3-已完成',
    `remark` TEXT DEFAULT NULL COMMENT '备注/评语',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_plan_id` (`plan_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核任务表';

-- 考核评分明细表
DROP TABLE IF EXISTS `evaluation_score`;
CREATE TABLE `evaluation_score` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评分ID',
    `task_id` BIGINT NOT NULL COMMENT '考核任务ID',
    `kpi_id` BIGINT NOT NULL COMMENT 'KPI指标ID',
    `evaluator_id` BIGINT NOT NULL COMMENT '评分人ID',
    `evaluatee_id` BIGINT NOT NULL COMMENT '被评分人ID',
    `score_type` VARCHAR(20) NOT NULL COMMENT '评分类型：SELF-自评 MANAGER-上级评 PEER-同事互评',
    `score` DECIMAL(8,2) NOT NULL COMMENT '评分',
    `comment` VARCHAR(500) DEFAULT NULL COMMENT '评语',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_evaluator_id` (`evaluator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核评分明细表';

-- 考核附件表（证明材料）
DROP TABLE IF EXISTS `evaluation_attachment`;
CREATE TABLE `evaluation_attachment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '附件ID',
    `task_id` BIGINT NOT NULL COMMENT '考核任务ID',
    `file_name` VARCHAR(200) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
    `uploader_id` BIGINT NOT NULL COMMENT '上传人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核附件表';

-- ========================================
-- 4. 绩效结果应用模块
-- ========================================

-- 薪酬调整表
DROP TABLE IF EXISTS `salary_adjustment`;
CREATE TABLE `salary_adjustment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `task_id` BIGINT NOT NULL COMMENT '考核任务ID',
    `user_id` BIGINT NOT NULL COMMENT '员工ID',
    `plan_id` BIGINT NOT NULL COMMENT '考核方案ID',
    `grade` VARCHAR(5) DEFAULT NULL COMMENT '绩效等级',
    `final_score` DECIMAL(8,2) DEFAULT NULL COMMENT '考核得分',
    `base_salary` VARCHAR(200) DEFAULT NULL COMMENT '基本薪资（AES加密）',
    `adjustment_rate` VARCHAR(200) DEFAULT NULL COMMENT '调薪比例（%）（AES加密）',
    `bonus_amount` VARCHAR(200) DEFAULT NULL COMMENT '奖金金额（AES加密）',
    `suggestion` TEXT DEFAULT NULL COMMENT '薪酬调整建议',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审批 1-已审批 2-已驳回',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪酬调整表';

-- 个人发展计划表（IDP）
DROP TABLE IF EXISTS `development_plan`;
CREATE TABLE `development_plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `task_id` BIGINT NOT NULL COMMENT '考核任务ID',
    `user_id` BIGINT NOT NULL COMMENT '员工ID',
    `plan_id` BIGINT NOT NULL COMMENT '考核方案ID',
    `strengths` TEXT DEFAULT NULL COMMENT '优势分析',
    `weaknesses` TEXT DEFAULT NULL COMMENT '待改进项',
    `training_suggestion` TEXT DEFAULT NULL COMMENT '培训建议',
    `development_goal` TEXT DEFAULT NULL COMMENT '发展目标',
    `action_plan` TEXT DEFAULT NULL COMMENT '行动计划',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-草稿 1-已确认 2-执行中 3-已完成',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人发展计划表';

-- 绩效申诉表
DROP TABLE IF EXISTS `appeal`;
CREATE TABLE `appeal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申诉ID',
    `task_id` BIGINT NOT NULL COMMENT '考核任务ID',
    `user_id` BIGINT NOT NULL COMMENT '申诉人ID',
    `reason` TEXT NOT NULL COMMENT '申诉原因',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待处理 1-处理中 2-已通过 3-已驳回',
    `reply` TEXT DEFAULT NULL COMMENT '回复内容',
    `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绩效申诉表';

-- ========================================
-- 初始数据
-- ========================================

-- 插入默认管理员（密码: 123456，BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `email`, `phone`, `role`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@company.com', '13800000000', 'ADMIN', 1);

-- 插入默认部门
INSERT INTO `sys_department` (`name`, `parent_id`, `description`, `sort_order`) VALUES
('总经理办公室', 0, '公司最高管理层', 1),
('风险管理部', 0, '负责公司风险管控', 2),
('合规管理部', 0, '负责合规监管事务', 3),
('财务管理部', 0, '负责财务管理和资金运作', 4),
('客户服务部', 0, '负责客户关系维护和服务', 5),
('投资银行部', 0, '负责投行业务', 6),
('人力资源部', 0, '负责人事管理和绩效考核', 7);

-- 插入金融行业特有KPI指标
INSERT INTO `kpi_indicator` (`name`, `category`, `description`, `formula`, `unit`, `target_value`, `max_score`) VALUES
('合规完成率', 'COMPLIANCE', '各项合规任务的完成比例', '已完成合规任务数/总合规任务数×100', '%', 100.00, 100.00),
('风险控制得分', 'RISK', '风险事件控制和预防的综合评分', '加权评分', '分', 90.00, 100.00),
('客户资产增长率', 'CUSTOMER', '管理客户资产的增长比例', '(期末客户资产-期初客户资产)/期初客户资产×100', '%', 15.00, 100.00),
('业务办理差错率', 'OPERATION', '业务办理中的差错比例', '差错笔数/总业务笔数×100', '%', 0.50, 100.00),
('营业收入完成率', 'FINANCIAL', '实际营业收入与目标的完成比例', '实际收入/目标收入×100', '%', 100.00, 100.00),
('净利润增长率', 'FINANCIAL', '净利润同比增长率', '(本期净利润-上期净利润)/上期净利润×100', '%', 10.00, 100.00),
('客户满意度', 'CUSTOMER', '客户满意度调查得分', '满意度问卷加权平均分', '分', 85.00, 100.00),
('新客户开发数', 'CUSTOMER', '新开发客户数量', '实际新增客户数', '个', 50.00, 100.00),
('培训完成率', 'OPERATION', '员工培训计划完成率', '已完成培训数/计划培训数×100', '%', 100.00, 100.00),
('反洗钱合规率', 'COMPLIANCE', '反洗钱工作合规完成比例', '合规项数/总检查项数×100', '%', 100.00, 100.00);
