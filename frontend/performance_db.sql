/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80034
 Source Host           : localhost:3306
 Source Schema         : performance_db

 Target Server Type    : MySQL
 Target Server Version : 80034
 File Encoding         : 65001

 Date: 27/05/2026 19:48:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appeal
-- ----------------------------
DROP TABLE IF EXISTS `appeal`;
CREATE TABLE `appeal`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申诉ID',
  `task_id` bigint NOT NULL COMMENT '考核任务ID',
  `user_id` bigint NOT NULL COMMENT '申诉人ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申诉原因',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待处理 1-处理中 2-已通过 3-已驳回',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '回复内容',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '绩效申诉表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appeal
-- ----------------------------
INSERT INTO `appeal` VALUES (1, 12, 13, '对合规知识考核评分有异议，认为部分扣分项不合理。本人已参加相关培训并取得合格证书，希望重新评估合规完成率得分。', 2, '经核实，该员工确已完成部分合规培训，但日常合规操作仍有多项扣分记录。维持原评分，建议继续加强日常合规操作。', 2, '2026-05-09 19:39:14', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `appeal` VALUES (2, 16, 17, '认为客户满意度评分过低，部分低分评价为非责任范围内的投诉，申请剔除相关评分后重新计算。', 1, NULL, NULL, NULL, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `appeal` VALUES (3, 18, 19, '对项目回报率评分有异议，所负责项目的回报计算周期应延至下季度方可体现真实收益。', 0, NULL, NULL, NULL, '2026-05-09 19:39:14', '2026-05-09 19:39:14');

-- ----------------------------
-- Table structure for development_plan
-- ----------------------------
DROP TABLE IF EXISTS `development_plan`;
CREATE TABLE `development_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `task_id` bigint NOT NULL COMMENT '考核任务ID',
  `user_id` bigint NOT NULL COMMENT '员工ID',
  `plan_id` bigint NOT NULL COMMENT '考核方案ID',
  `strengths` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '优势分析',
  `weaknesses` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '待改进项',
  `training_suggestion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '培训建议',
  `development_goal` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '发展目标',
  `action_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '行动计划',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-草稿 1-已确认 2-执行中 3-已完成',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '个人发展计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of development_plan
-- ----------------------------
INSERT INTO `development_plan` VALUES (1, 1, 2, 1, '绩效管理经验丰富，HR专业能力强', NULL, '建议参加高级人力资源管理师认证培训', '向HR总监方向发展', '1.完成高级HR认证 2.主导绩效体系优化项目', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `development_plan` VALUES (2, 3, 4, 1, '风险管控能力极强，全年零重大风险事件', NULL, '建议参加高级管理培训、行业峰会，培养领导力', '向管理岗位或专家岗位发展', '1.参加CRO高级研修班 2.编写风控方法论手册', 2, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `development_plan` VALUES (3, 10, 11, 1, '基本能完成工作任务', '风控分析能力不足，报告质量有待提高', '建议参加风险管理专题培训，加强分析方法学习', '三个月内风控分析能力达到部门平均水平', '1.每周学习一篇风控案例 2.参加FRM考试备考 3.由王强经理一对一指导', 2, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `development_plan` VALUES (4, 12, 13, 1, NULL, '合规知识掌握不足，工作细致程度不够', '建议安排系统性合规培训，指定导师一对一辅导', '三个月内各项合规考核指标达到合格标准', '1.每周完成合规知识测试 2.每月一次辅导面谈 3.参加反洗钱专题培训', 2, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `development_plan` VALUES (5, 13, 14, 1, '财务报告准确率高，工作细致认真', NULL, '建议参加高级财务分析培训，提升战略财务能力', '向高级财务分析师方向发展', '1.完成CFA二级备考 2.参与公司财务战略规划项目', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `development_plan` VALUES (6, 19, 20, 1, '技术能力突出，系统优化效果显著', NULL, '建议参加架构师培训，拓展技术视野', '向技术架构师方向发展', '1.完成微服务架构设计课程 2.主导下一代系统架构设计', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `development_plan` VALUES (7, 105, 2, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (8, 106, 3, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (9, 107, 4, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (10, 108, 5, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (11, 109, 6, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (12, 110, 7, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (13, 111, 8, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (14, 112, 9, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (15, 113, 10, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (16, 114, 11, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (17, 115, 12, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (18, 116, 13, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (19, 117, 14, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (20, 118, 15, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (21, 119, 16, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (22, 120, 17, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (23, 121, 18, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (24, 122, 19, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (25, 123, 20, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (26, 124, 21, 3, NULL, '综合绩效不达标，多项指标存在明显不足', '建议安排系统性培训，指定导师一对一辅导', '三个月内各项核心指标达到合格标准', '1.制定每周学习计划 2.每月进行一次辅导面谈 3.参加必要的合规和风控培训', 0, '2026-05-25 09:00:24', '2026-05-25 09:00:24');
INSERT INTO `development_plan` VALUES (27, 2, 3, 1, '整体表现良好，大部分指标达到目标', '部分指标仍有提升空间', '建议参加专业技能深化培训，提升薄弱项', '巩固现有能力，提升短板项至优秀水平', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (28, 4, 5, 1, '整体表现良好，大部分指标达到目标', '部分指标仍有提升空间', '建议参加专业技能深化培训，提升薄弱项', '巩固现有能力，提升短板项至优秀水平', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (29, 5, 6, 1, '综合表现优秀，各项KPI指标均达到或超过目标', NULL, '建议参加高级管理培训、行业峰会，培养领导力', '向管理岗位或专家岗位发展', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (30, 6, 7, 1, '整体表现良好，大部分指标达到目标', '部分指标仍有提升空间', '建议参加专业技能深化培训，提升薄弱项', '巩固现有能力，提升短板项至优秀水平', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (31, 7, 8, 1, '综合表现优秀，各项KPI指标均达到或超过目标', NULL, '建议参加高级管理培训、行业峰会，培养领导力', '向管理岗位或专家岗位发展', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (32, 8, 9, 1, '整体表现良好，大部分指标达到目标', '部分指标仍有提升空间', '建议参加专业技能深化培训，提升薄弱项', '巩固现有能力，提升短板项至优秀水平', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (33, 9, 10, 1, '整体表现良好，大部分指标达到目标', '部分指标仍有提升空间', '建议参加专业技能深化培训，提升薄弱项', '巩固现有能力，提升短板项至优秀水平', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (34, 11, 12, 1, '整体表现良好，大部分指标达到目标', '部分指标仍有提升空间', '建议参加专业技能深化培训，提升薄弱项', '巩固现有能力，提升短板项至优秀水平', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (35, 14, 15, 1, '整体表现良好，大部分指标达到目标', '部分指标仍有提升空间', '建议参加专业技能深化培训，提升薄弱项', '巩固现有能力，提升短板项至优秀水平', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (36, 15, 16, 1, '综合表现优秀，各项KPI指标均达到或超过目标', NULL, '建议参加高级管理培训、行业峰会，培养领导力', '向管理岗位或专家岗位发展', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (37, 16, 17, 1, '基本能完成工作任务', '多项指标未达预期，需要重点改进', '建议参加基础技能培训和合规培训，加强指导带教', '尽快提升至合格以上水平，缩小与团队平均的差距', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (38, 17, 18, 1, '整体表现良好，大部分指标达到目标', '部分指标仍有提升空间', '建议参加专业技能深化培训，提升薄弱项', '巩固现有能力，提升短板项至优秀水平', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (39, 18, 19, 1, '基本能完成工作任务', '多项指标未达预期，需要重点改进', '建议参加基础技能培训和合规培训，加强指导带教', '尽快提升至合格以上水平，缩小与团队平均的差距', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');
INSERT INTO `development_plan` VALUES (40, 20, 21, 1, '整体表现良好，大部分指标达到目标', '部分指标仍有提升空间', '建议参加专业技能深化培训，提升薄弱项', '巩固现有能力，提升短板项至优秀水平', NULL, 0, '2026-05-25 09:00:33', '2026-05-25 09:00:33');

-- ----------------------------
-- Table structure for evaluation_attachment
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_attachment`;
CREATE TABLE `evaluation_attachment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `task_id` bigint NOT NULL COMMENT '考核任务ID',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件存储路径',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `uploader_id` bigint NOT NULL COMMENT '上传人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考核附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for evaluation_plan
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_plan`;
CREATE TABLE `evaluation_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '方案ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案名称',
  `year` int NOT NULL COMMENT '年度',
  `period_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考核周期：MONTHLY-月度 QUARTERLY-季度 ANNUALLY-年度',
  `period_number` int NULL DEFAULT NULL COMMENT '周期编号（第几月/第几季度，年度为空）',
  `start_date` date NOT NULL COMMENT '考核开始日期',
  `end_date` date NOT NULL COMMENT '考核结束日期',
  `grade_a_min` decimal(5, 2) NULL DEFAULT 90.00 COMMENT 'A等级最低分',
  `grade_b_min` decimal(5, 2) NULL DEFAULT 75.00 COMMENT 'B等级最低分',
  `grade_c_min` decimal(5, 2) NULL DEFAULT 60.00 COMMENT 'C等级最低分',
  `self_weight` decimal(5, 2) NULL DEFAULT 20.00 COMMENT '自评权重（%）',
  `manager_weight` decimal(5, 2) NULL DEFAULT 60.00 COMMENT '上级评权重（%）',
  `peer_weight` decimal(5, 2) NULL DEFAULT 20.00 COMMENT '同事互评权重（%）',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-草稿 1-进行中 2-已完成',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考核方案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation_plan
-- ----------------------------
INSERT INTO `evaluation_plan` VALUES (1, '2026年第一季度绩效考核', 2026, 'QUARTERLY', 1, '2026-01-01', '2026-03-31', 90.00, 75.00, 60.00, 20.00, 60.00, 20.00, 2, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_plan` VALUES (3, '2026年年度绩效考核', 2026, 'ANNUALLY', 1, '2026-04-30', '2026-06-30', 90.00, 75.00, 60.00, 15.00, 55.00, 30.00, 2, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_plan` VALUES (5, '2026年测试考核（自动计算验证）', 2026, 'QUARTERLY', 3, '2026-07-01', '2026-09-30', 90.00, 75.00, 60.00, 20.00, 60.00, 20.00, 2, 1, '2026-05-09 19:39:20', '2026-05-09 19:39:20');

-- ----------------------------
-- Table structure for evaluation_rule
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_rule`;
CREATE TABLE `evaluation_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `plan_id` bigint NOT NULL COMMENT '考核方案ID',
  `kpi_id` bigint NOT NULL COMMENT 'KPI指标ID',
  `weight` decimal(5, 2) NOT NULL COMMENT '该指标在方案中的权重（%）',
  `target_value` decimal(12, 2) NULL DEFAULT NULL COMMENT '目标值',
  `scoring_formula` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'WEIGHTED_SUM' COMMENT '评分方式：WEIGHTED_SUM-加权求和 PERCENTILE-百分位排名',
  `max_score` decimal(8, 2) NULL DEFAULT 100.00 COMMENT '该指标最高分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考核评分规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation_rule
-- ----------------------------
INSERT INTO `evaluation_rule` VALUES (1, 1, 1, 15.00, 100.00, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_rule` VALUES (2, 1, 2, 15.00, 90.00, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_rule` VALUES (3, 1, 5, 20.00, 100.00, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_rule` VALUES (4, 1, 7, 15.00, 85.00, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_rule` VALUES (5, 1, 4, 10.00, 0.50, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_rule` VALUES (6, 1, 9, 15.00, 100.00, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_rule` VALUES (7, 1, 11, 10.00, 95.00, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_rule` VALUES (20, 5, 1, 40.00, 100.00, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:20', '2026-05-09 19:39:20');
INSERT INTO `evaluation_rule` VALUES (21, 5, 5, 35.00, 100.00, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:20', '2026-05-09 19:39:20');
INSERT INTO `evaluation_rule` VALUES (22, 5, 7, 25.00, 85.00, 'WEIGHTED_SUM', 100.00, '2026-05-09 19:39:20', '2026-05-09 19:39:20');
INSERT INTO `evaluation_rule` VALUES (26, 3, 1, 10.00, 100.00, 'WEIGHTED_SUM', 100.00, '2026-05-24 20:51:17', '2026-05-24 20:51:17');
INSERT INTO `evaluation_rule` VALUES (27, 3, 2, 10.00, 90.00, 'WEIGHTED_SUM', 100.00, '2026-05-24 20:51:19', '2026-05-24 20:51:19');
INSERT INTO `evaluation_rule` VALUES (28, 3, 3, 10.00, 15.00, 'WEIGHTED_SUM', 100.00, '2026-05-24 20:51:21', '2026-05-24 20:51:21');
INSERT INTO `evaluation_rule` VALUES (29, 3, 4, 10.00, 0.50, 'WEIGHTED_SUM', 100.00, '2026-05-24 20:51:23', '2026-05-24 20:51:23');

-- ----------------------------
-- Table structure for evaluation_score
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_score`;
CREATE TABLE `evaluation_score`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评分ID',
  `task_id` bigint NOT NULL COMMENT '考核任务ID',
  `kpi_id` bigint NOT NULL COMMENT 'KPI指标ID',
  `evaluator_id` bigint NOT NULL COMMENT '评分人ID',
  `evaluatee_id` bigint NOT NULL COMMENT '被评分人ID',
  `score_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评分类型：SELF-自评 MANAGER-上级评 PEER-同事互评',
  `score` decimal(8, 2) NOT NULL COMMENT '评分',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评语',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id`) USING BTREE,
  INDEX `idx_evaluator_id`(`evaluator_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考核评分明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation_score
-- ----------------------------
INSERT INTO `evaluation_score` VALUES (1, 1, 1, 2, 2, 'SELF', 90.00, '合规工作全部按时完成', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (2, 1, 2, 2, 2, 'SELF', 85.00, '风控意识较强', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (3, 1, 5, 2, 2, 'SELF', 88.00, '营收指标达成良好', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (4, 1, 7, 2, 2, 'SELF', 90.00, '内部满意度高', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (5, 1, 1, 1, 2, 'MANAGER', 93.00, '合规工作表现优秀', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (6, 1, 2, 1, 2, 'MANAGER', 90.00, '风控配合到位', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (7, 1, 5, 1, 2, 'MANAGER', 92.00, '超额完成营收任务', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (8, 1, 7, 1, 2, 'MANAGER', 93.00, '服务意识强', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (9, 1, 1, 3, 2, 'PEER', 86.00, '合规培训组织得力', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (10, 1, 2, 3, 2, 'PEER', 84.00, '风控配合良好', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (11, 1, 5, 3, 2, 'PEER', 85.00, NULL, '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (12, 1, 7, 3, 2, 'PEER', 86.00, '同事关系融洽', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (13, 3, 1, 4, 4, 'SELF', 92.00, '合规任务全部完成', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (14, 3, 2, 4, 4, 'SELF', 90.00, '风控体系持续优化', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (15, 3, 5, 4, 4, 'SELF', 88.00, '部门营收达标', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (16, 3, 1, 1, 4, 'MANAGER', 96.00, '风险管控能力卓越', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (17, 3, 2, 1, 4, 'MANAGER', 95.00, '风控零事故', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (18, 3, 5, 1, 4, 'MANAGER', 94.00, '超额完成', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (19, 3, 1, 10, 4, 'PEER', 88.00, '领导力强', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (20, 3, 2, 10, 4, 'PEER', 89.00, '专业能力突出', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (21, 3, 5, 10, 4, 'PEER', 87.00, NULL, '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (22, 13, 1, 14, 14, 'SELF', 93.00, '合规任务完成率100%', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (23, 13, 5, 14, 14, 'SELF', 92.00, '财务报告准确率高', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (24, 13, 4, 14, 14, 'SELF', 91.00, '零差错', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (25, 13, 1, 5, 14, 'MANAGER', 95.00, '财务合规工作扎实', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (26, 13, 5, 5, 14, 'MANAGER', 94.00, '超额完成财务指标', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (27, 13, 4, 5, 14, 'MANAGER', 93.00, '全年零差错，值得表彰', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (28, 13, 1, 15, 14, 'PEER', 90.00, '工作认真细致', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (29, 13, 5, 15, 14, 'PEER', 90.00, NULL, '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (30, 13, 4, 15, 14, 'PEER', 91.00, '业务精通', '2026-05-09 19:39:14');
INSERT INTO `evaluation_score` VALUES (31, 101, 1, 2, 2, 'SELF', 95.00, '合规完成', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (32, 101, 5, 2, 2, 'SELF', 90.00, '营收达标', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (33, 101, 7, 2, 2, 'SELF', 90.00, '满意度高', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (34, 101, 1, 1, 2, 'MANAGER', 98.00, '合规优秀', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (35, 101, 5, 1, 2, 'MANAGER', 94.00, '超额完成', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (36, 101, 7, 1, 2, 'MANAGER', 92.00, '服务好', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (37, 101, 1, 3, 2, 'PEER', 92.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (38, 101, 5, 3, 2, 'PEER', 88.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (39, 101, 7, 3, 2, 'PEER', 90.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (40, 102, 1, 10, 10, 'SELF', 82.00, '基本完成', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (41, 102, 5, 10, 10, 'SELF', 80.00, '一般', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (42, 102, 7, 10, 10, 'SELF', 78.00, '尚可', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (43, 102, 1, 4, 10, 'MANAGER', 80.00, '合格', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (44, 102, 5, 4, 10, 'MANAGER', 78.00, '达标', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (45, 102, 7, 4, 10, 'MANAGER', 75.00, '需提升', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (46, 102, 1, 11, 10, 'PEER', 78.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (47, 102, 5, 11, 10, 'PEER', 74.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (48, 102, 7, 11, 10, 'PEER', 76.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (49, 103, 1, 14, 14, 'SELF', 68.00, '有欠缺', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (50, 103, 5, 14, 14, 'SELF', 64.00, '未达标', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (51, 103, 7, 14, 14, 'SELF', 62.00, '低', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (52, 103, 1, 5, 14, 'MANAGER', 65.00, '需改进', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (53, 103, 5, 5, 14, 'MANAGER', 60.00, '未达标', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (54, 103, 7, 5, 14, 'MANAGER', 60.00, '待提升', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (55, 103, 1, 15, 14, 'PEER', 70.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (56, 103, 5, 15, 14, 'PEER', 66.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (57, 103, 7, 15, 14, 'PEER', 68.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (58, 104, 1, 21, 21, 'SELF', 52.00, '差', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (59, 104, 5, 21, 21, 'SELF', 48.00, '差', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (60, 104, 7, 21, 21, 'SELF', 50.00, '差', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (61, 104, 1, 9, 21, 'MANAGER', 50.00, '不合格', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (62, 104, 5, 9, 21, 'MANAGER', 46.00, '差', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (63, 104, 7, 9, 21, 'MANAGER', 48.00, '差', '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (64, 104, 1, 20, 21, 'PEER', 54.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (65, 104, 5, 20, 21, 'PEER', 50.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (66, 104, 7, 20, 21, 'PEER', 52.00, NULL, '2026-05-09 19:39:20');
INSERT INTO `evaluation_score` VALUES (67, 124, 1, 21, 21, 'SELF', 100.00, '', '2026-05-24 20:53:27');
INSERT INTO `evaluation_score` VALUES (68, 124, 2, 21, 21, 'SELF', 90.00, '', '2026-05-24 20:53:27');
INSERT INTO `evaluation_score` VALUES (69, 124, 3, 21, 21, 'SELF', 15.00, '', '2026-05-24 20:53:27');
INSERT INTO `evaluation_score` VALUES (70, 124, 4, 21, 21, 'SELF', 0.50, '', '2026-05-24 20:53:27');
INSERT INTO `evaluation_score` VALUES (71, 123, 1, 20, 20, 'SELF', 100.00, '', '2026-05-24 20:54:23');
INSERT INTO `evaluation_score` VALUES (72, 123, 2, 20, 20, 'SELF', 90.00, '', '2026-05-24 20:54:23');
INSERT INTO `evaluation_score` VALUES (73, 123, 3, 20, 20, 'SELF', 15.00, '', '2026-05-24 20:54:23');
INSERT INTO `evaluation_score` VALUES (74, 123, 4, 20, 20, 'SELF', 0.50, '', '2026-05-24 20:54:23');
INSERT INTO `evaluation_score` VALUES (75, 105, 1, 2, 2, 'SELF', 100.00, '', '2026-05-24 20:55:08');
INSERT INTO `evaluation_score` VALUES (76, 105, 2, 2, 2, 'SELF', 90.00, '', '2026-05-24 20:55:08');
INSERT INTO `evaluation_score` VALUES (77, 105, 3, 2, 2, 'SELF', 15.00, '', '2026-05-24 20:55:08');
INSERT INTO `evaluation_score` VALUES (78, 105, 4, 2, 2, 'SELF', 0.50, '', '2026-05-24 20:55:08');
INSERT INTO `evaluation_score` VALUES (79, 123, 1, 9, 20, 'MANAGER', 100.00, '', '2026-05-24 20:56:30');
INSERT INTO `evaluation_score` VALUES (80, 123, 2, 9, 20, 'MANAGER', 78.00, '', '2026-05-24 20:56:30');
INSERT INTO `evaluation_score` VALUES (81, 123, 3, 9, 20, 'MANAGER', 12.00, '', '2026-05-24 20:56:30');
INSERT INTO `evaluation_score` VALUES (82, 123, 4, 9, 20, 'MANAGER', 1.30, '', '2026-05-24 20:56:30');
INSERT INTO `evaluation_score` VALUES (83, 124, 1, 9, 21, 'MANAGER', 98.00, '', '2026-05-24 20:56:45');
INSERT INTO `evaluation_score` VALUES (84, 124, 2, 9, 21, 'MANAGER', 89.00, '', '2026-05-24 20:56:45');
INSERT INTO `evaluation_score` VALUES (85, 124, 3, 9, 21, 'MANAGER', 13.00, '', '2026-05-24 20:56:45');
INSERT INTO `evaluation_score` VALUES (86, 124, 4, 9, 21, 'MANAGER', 0.30, '', '2026-05-24 20:56:45');
INSERT INTO `evaluation_score` VALUES (87, 124, 1, 20, 21, 'PEER', 100.00, '', '2026-05-24 20:59:13');
INSERT INTO `evaluation_score` VALUES (88, 124, 2, 20, 21, 'PEER', 90.00, '', '2026-05-24 20:59:13');
INSERT INTO `evaluation_score` VALUES (89, 124, 3, 20, 21, 'PEER', 15.00, '', '2026-05-24 20:59:13');
INSERT INTO `evaluation_score` VALUES (90, 124, 4, 20, 21, 'PEER', 0.50, '', '2026-05-24 20:59:13');
INSERT INTO `evaluation_score` VALUES (91, 124, 1, 9, 21, 'PEER', 100.00, '', '2026-05-24 21:00:00');
INSERT INTO `evaluation_score` VALUES (92, 124, 2, 9, 21, 'PEER', 90.00, '', '2026-05-24 21:00:00');
INSERT INTO `evaluation_score` VALUES (93, 124, 3, 9, 21, 'PEER', 15.00, '', '2026-05-24 21:00:00');
INSERT INTO `evaluation_score` VALUES (94, 124, 4, 9, 21, 'PEER', 0.50, '', '2026-05-24 21:00:00');
INSERT INTO `evaluation_score` VALUES (95, 123, 1, 21, 20, 'PEER', 100.00, '', '2026-05-24 21:00:37');
INSERT INTO `evaluation_score` VALUES (96, 123, 2, 21, 20, 'PEER', 90.00, '', '2026-05-24 21:00:37');
INSERT INTO `evaluation_score` VALUES (97, 123, 3, 21, 20, 'PEER', 15.00, '', '2026-05-24 21:00:37');
INSERT INTO `evaluation_score` VALUES (98, 123, 4, 21, 20, 'PEER', 0.20, '', '2026-05-24 21:00:37');

-- ----------------------------
-- Table structure for evaluation_task
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_task`;
CREATE TABLE `evaluation_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `plan_id` bigint NOT NULL COMMENT '考核方案ID',
  `user_id` bigint NOT NULL COMMENT '被考核人ID',
  `department_id` bigint NULL DEFAULT NULL COMMENT '被考核人所属部门ID',
  `self_score` decimal(8, 2) NULL DEFAULT NULL COMMENT '自评总分',
  `manager_score` decimal(8, 2) NULL DEFAULT NULL COMMENT '上级评总分',
  `peer_score` decimal(8, 2) NULL DEFAULT NULL COMMENT '同事互评总分',
  `final_score` decimal(8, 2) NULL DEFAULT NULL COMMENT '最终得分',
  `grade` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '绩效等级：A/B/C/D',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待自评 1-待上级评 2-待互评 3-已完成',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注/评语',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 124 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考核任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation_task
-- ----------------------------
INSERT INTO `evaluation_task` VALUES (1, 1, 2, 7, 88.00, 92.00, 85.00, 90.00, 'A', 3, '工作认真负责，绩效管理能力突出', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (2, 1, 3, 7, 82.00, 85.00, 80.00, 83.40, 'B', 3, '表现良好，继续提升', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (3, 1, 4, 2, 90.00, 95.00, 88.00, 92.60, 'A', 3, '风险管控能力极强，全年零重大风险事件', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (4, 1, 5, 4, 85.00, 88.00, 82.00, 86.00, 'B', 3, '财务管理精细，报表质量高', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (5, 1, 6, 5, 92.00, 93.00, 90.00, 92.20, 'A', 3, '客户满意度显著提升', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (6, 1, 7, 6, 80.00, 82.00, 78.00, 80.40, 'B', 3, '投行项目推进有成效', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (7, 1, 8, 3, 95.00, 96.00, 92.00, 95.00, 'A', 3, '合规工作表现卓越，保持零违规纪录', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (8, 1, 9, 8, 78.00, 80.00, 76.00, 78.80, 'B', 3, '系统稳定性维护到位', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (9, 1, 10, 2, 85.00, 90.00, 83.00, 87.60, 'B', 3, '风险评估报告质量高', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (10, 1, 11, 2, 75.00, 72.00, 70.00, 72.00, 'C', 3, '需要加强风控分析能力', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (11, 1, 12, 3, 88.00, 91.00, 86.00, 89.40, 'B', 3, '合规检查细致认真', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (12, 1, 13, 3, 60.00, 58.00, 62.00, 59.20, 'D', 3, '合规知识掌握不足，需加强培训', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (13, 1, 14, 4, 92.00, 94.00, 90.00, 92.80, 'A', 3, '财务报告准确率高，工作细致', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (14, 1, 15, 4, 78.00, 75.00, 73.00, 75.00, 'B', 3, '基本完成任务，需提升效率', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (15, 1, 16, 5, 90.00, 92.00, 88.00, 90.80, 'A', 3, '客户服务态度优秀，获多次表扬', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (16, 1, 17, 5, 72.00, 68.00, 70.00, 69.20, 'C', 3, '客户沟通能力有待提高', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (17, 1, 18, 6, 86.00, 88.00, 84.00, 86.80, 'B', 3, '项目分析能力强', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (18, 1, 19, 6, 65.00, 62.00, 66.00, 63.40, 'C', 3, '需要提升投行业务专业水平', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (19, 1, 20, 8, 91.00, 93.00, 89.00, 91.80, 'A', 3, '技术能力突出，系统优化效果显著', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (20, 1, 21, 8, 80.00, 78.00, 76.00, 78.00, 'B', 3, '完成本职工作，技术能力需提升', '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `evaluation_task` VALUES (101, 5, 2, 7, 92.00, 95.10, 90.10, 93.48, 'A', 3, NULL, '2026-05-09 19:39:20', '2026-05-09 19:39:20');
INSERT INTO `evaluation_task` VALUES (102, 5, 10, 2, 80.30, 78.05, 76.10, 78.11, 'B', 3, NULL, '2026-05-09 19:39:20', '2026-05-09 19:39:20');
INSERT INTO `evaluation_task` VALUES (103, 5, 14, 4, 65.10, 62.00, 68.10, 63.84, 'C', 3, NULL, '2026-05-09 19:39:20', '2026-05-09 19:39:20');
INSERT INTO `evaluation_task` VALUES (104, 5, 21, 8, 50.10, 48.10, 52.10, 49.30, 'D', 3, NULL, '2026-05-09 19:39:20', '2026-05-09 19:39:20');
INSERT INTO `evaluation_task` VALUES (105, 3, 2, 7, 51.38, 0.00, 0.00, 7.71, 'D', 3, '好', '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (106, 3, 3, 7, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (107, 3, 4, 2, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (108, 3, 5, 4, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (109, 3, 6, 5, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (110, 3, 7, 6, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (111, 3, 8, 3, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (112, 3, 9, 8, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (113, 3, 10, 2, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (114, 3, 11, 2, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (115, 3, 12, 3, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (116, 3, 13, 3, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (117, 3, 14, 4, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (118, 3, 15, 4, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (119, 3, 16, 5, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (120, 3, 17, 5, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (121, 3, 18, 6, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (122, 3, 19, 6, 0.00, 0.00, 0.00, 0.00, 'D', 3, NULL, '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (123, 3, 20, 8, 51.38, 47.83, 51.30, 49.41, 'D', 3, '好', '2026-05-24 20:51:35', '2026-05-24 20:51:35');
INSERT INTO `evaluation_task` VALUES (124, 3, 21, 8, 51.38, 50.08, 51.38, 50.66, 'D', 3, 'very good', '2026-05-24 20:51:35', '2026-05-24 20:51:35');

-- ----------------------------
-- Table structure for goal_kpi
-- ----------------------------
DROP TABLE IF EXISTS `goal_kpi`;
CREATE TABLE `goal_kpi`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `goal_id` bigint NOT NULL COMMENT '目标ID',
  `kpi_id` bigint NOT NULL COMMENT 'KPI指标ID',
  `target_value` decimal(12, 2) NULL DEFAULT NULL COMMENT '该目标下的指标目标值',
  `weight` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '权重（百分比，如30.00表示30%）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_goal_id`(`goal_id`) USING BTREE,
  INDEX `idx_kpi_id`(`kpi_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '目标与KPI指标关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goal_kpi
-- ----------------------------
INSERT INTO `goal_kpi` VALUES (1, 4, 2, 90.00, 50.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (2, 4, 10, 100.00, 30.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (3, 4, 11, 95.00, 20.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (4, 5, 1, 100.00, 40.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (5, 5, 10, 100.00, 40.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (6, 5, 11, 95.00, 20.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (7, 6, 5, 105.00, 40.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (8, 6, 6, 10.00, 30.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (9, 6, 4, 0.50, 30.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (10, 7, 7, 88.00, 50.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (11, 7, 8, 60.00, 30.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (12, 7, 3, 15.00, 20.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (13, 8, 12, 12.00, 50.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (14, 8, 5, 100.00, 30.00, '2026-05-09 19:39:14');
INSERT INTO `goal_kpi` VALUES (15, 8, 3, 15.00, 20.00, '2026-05-09 19:39:14');

-- ----------------------------
-- Table structure for kpi_indicator
-- ----------------------------
DROP TABLE IF EXISTS `kpi_indicator`;
CREATE TABLE `kpi_indicator`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '指标ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指标名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指标分类：FINANCIAL-财务类 COMPLIANCE-合规类 RISK-风控类 CUSTOMER-客户类 OPERATION-运营类',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '指标描述',
  `formula` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '计算公式',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位（如：%、分、元）',
  `target_value` decimal(12, 2) NULL DEFAULT NULL COMMENT '目标值',
  `max_score` decimal(8, 2) NULL DEFAULT 100.00 COMMENT '最高分',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'KPI指标库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kpi_indicator
-- ----------------------------
INSERT INTO `kpi_indicator` VALUES (1, '合规完成率', 'COMPLIANCE', '各项合规任务的完成比例', '已完成合规任务数/总合规任务数×100', '%', 100.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (2, '风险控制得分', 'RISK', '风险事件控制和预防的综合评分', '加权评分', '分', 90.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (3, '客户资产增长率', 'CUSTOMER', '管理客户资产的增长比例', '(期末客户资产-期初客户资产)/期初客户资产×100', '%', 15.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (4, '业务办理差错率', 'OPERATION', '业务办理中的差错比例，越低越好', '差错笔数/总业务笔数×100', '%', 0.50, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (5, '营业收入完成率', 'FINANCIAL', '实际营业收入与目标的完成比例', '实际收入/目标收入×100', '%', 100.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (6, '净利润增长率', 'FINANCIAL', '净利润同比增长率', '(本期净利润-上期净利润)/上期净利润×100', '%', 10.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (7, '客户满意度', 'CUSTOMER', '客户满意度调查得分', '满意度问卷加权平均分', '分', 85.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (8, '新客户开发数', 'CUSTOMER', '新开发客户数量', '实际新增客户数', '个', 50.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (9, '培训完成率', 'OPERATION', '员工培训计划完成率', '已完成培训数/计划培训数×100', '%', 100.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (10, '反洗钱合规率', 'COMPLIANCE', '反洗钱工作合规完成比例', '合规项数/总检查项数×100', '%', 100.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (11, '内控制度执行率', 'COMPLIANCE', '内控制度各项要求的执行完成率', '已执行项数/应执行项数×100', '%', 95.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `kpi_indicator` VALUES (12, '项目回报率', 'FINANCIAL', '投资项目的投资回报率', '投资收益/投资成本×100', '%', 12.00, 100.00, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');

-- ----------------------------
-- Table structure for peer_assignment
-- ----------------------------
DROP TABLE IF EXISTS `peer_assignment`;
CREATE TABLE `peer_assignment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `plan_id` bigint NOT NULL COMMENT '考核方案ID',
  `task_id` bigint NOT NULL COMMENT '被考核人的任务ID',
  `evaluatee_id` bigint NOT NULL COMMENT '被评价人ID',
  `evaluator_id` bigint NOT NULL COMMENT '互评人ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待互评 1-已完成',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id`) USING BTREE,
  INDEX `idx_evaluator_id`(`evaluator_id`) USING BTREE,
  INDEX `idx_task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 80 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '互评分配表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of peer_assignment
-- ----------------------------
INSERT INTO `peer_assignment` VALUES (1, 1, 3, 4, 10, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (2, 1, 3, 4, 11, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (3, 1, 9, 10, 4, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (4, 1, 9, 10, 11, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (5, 1, 10, 11, 4, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (6, 1, 10, 11, 10, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (7, 1, 7, 8, 12, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (8, 1, 7, 8, 13, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (9, 1, 11, 12, 8, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (10, 1, 11, 12, 13, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (11, 1, 12, 13, 8, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (12, 1, 12, 13, 12, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (13, 1, 4, 5, 14, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (14, 1, 4, 5, 15, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (15, 1, 13, 14, 5, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (16, 1, 13, 14, 15, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (17, 1, 14, 15, 5, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (18, 1, 14, 15, 14, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (19, 1, 5, 6, 16, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (20, 1, 5, 6, 17, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (21, 1, 15, 16, 6, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (22, 1, 15, 16, 17, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (23, 1, 16, 17, 6, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (24, 1, 16, 17, 16, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (25, 1, 6, 7, 18, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (26, 1, 6, 7, 19, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (27, 1, 17, 18, 7, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (28, 1, 17, 18, 19, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (29, 1, 18, 19, 7, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (30, 1, 18, 19, 18, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (31, 1, 8, 9, 20, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (32, 1, 8, 9, 21, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (33, 1, 19, 20, 9, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (34, 1, 19, 20, 21, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (35, 1, 20, 21, 9, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (36, 1, 20, 21, 20, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (37, 1, 1, 2, 3, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (38, 1, 2, 3, 2, 1, '2026-05-09 19:39:14');
INSERT INTO `peer_assignment` VALUES (39, 5, 101, 2, 3, 1, '2026-05-09 19:39:20');
INSERT INTO `peer_assignment` VALUES (40, 5, 102, 10, 11, 1, '2026-05-09 19:39:20');
INSERT INTO `peer_assignment` VALUES (41, 5, 103, 14, 15, 1, '2026-05-09 19:39:20');
INSERT INTO `peer_assignment` VALUES (42, 5, 104, 21, 20, 1, '2026-05-09 19:39:20');
INSERT INTO `peer_assignment` VALUES (43, 3, 107, 4, 11, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (44, 3, 107, 4, 10, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (45, 3, 113, 10, 11, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (46, 3, 113, 10, 4, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (47, 3, 114, 11, 4, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (48, 3, 114, 11, 10, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (49, 3, 111, 8, 12, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (50, 3, 111, 8, 13, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (51, 3, 115, 12, 8, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (52, 3, 115, 12, 13, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (53, 3, 116, 13, 8, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (54, 3, 116, 13, 12, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (55, 3, 108, 5, 14, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (56, 3, 108, 5, 15, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (57, 3, 117, 14, 15, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (58, 3, 117, 14, 5, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (59, 3, 118, 15, 5, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (60, 3, 118, 15, 14, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (61, 3, 109, 6, 16, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (62, 3, 109, 6, 17, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (63, 3, 119, 16, 17, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (64, 3, 119, 16, 6, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (65, 3, 120, 17, 6, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (66, 3, 120, 17, 16, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (67, 3, 110, 7, 18, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (68, 3, 110, 7, 19, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (69, 3, 121, 18, 19, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (70, 3, 121, 18, 7, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (71, 3, 122, 19, 7, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (72, 3, 122, 19, 18, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (73, 3, 105, 2, 3, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (74, 3, 106, 3, 2, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (75, 3, 112, 9, 21, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (76, 3, 112, 9, 20, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (77, 3, 123, 20, 21, 1, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (78, 3, 123, 20, 9, 0, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (79, 3, 124, 21, 20, 1, '2026-05-24 20:51:35');
INSERT INTO `peer_assignment` VALUES (80, 3, 124, 21, 9, 1, '2026-05-24 20:51:35');

-- ----------------------------
-- Table structure for performance_goal
-- ----------------------------
DROP TABLE IF EXISTS `performance_goal`;
CREATE TABLE `performance_goal`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '目标ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '目标描述',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标层级：COMPANY-公司级 DEPARTMENT-部门级 PERSONAL-个人级',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '上级目标ID，0表示顶级',
  `department_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID（部门级和个人级）',
  `user_id` bigint NULL DEFAULT NULL COMMENT '责任人ID（个人级）',
  `year` int NOT NULL COMMENT '年度',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已完成',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_department_id`(`department_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '绩效目标表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of performance_goal
-- ----------------------------
INSERT INTO `performance_goal` VALUES (1, '2026年度经营目标：营业收入突破50亿元', '公司整体营收目标，各部门需按分解指标完成', 'COMPANY', 0, NULL, NULL, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (2, '2026年度合规与风控目标：零重大违规事件', '全年不发生重大监管处罚和合规风险事件', 'COMPANY', 0, NULL, NULL, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (3, '2026年度客户发展目标：新增优质客户200+', '着力发展高净值客户，提升客户资产规模', 'COMPANY', 0, NULL, NULL, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (4, '风险管理部：全年风控评分达90分以上', '建立健全风险预警机制，确保风险控制综合评分达标', 'DEPARTMENT', 2, 2, NULL, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (5, '合规管理部：合规完成率达100%', '完成全部合规检查任务，确保无重大合规违规', 'DEPARTMENT', 2, 3, NULL, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (6, '财务管理部：年度营收完成率达105%', '超额完成公司下达的财务指标', 'DEPARTMENT', 1, 4, NULL, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (7, '客户服务部：客户满意度达88分', '提升客户服务质量，降低投诉率', 'DEPARTMENT', 3, 5, NULL, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (8, '投资银行部：项目回报率达12%以上', '提升项目质量，确保投资回报达标', 'DEPARTMENT', 1, 6, NULL, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (9, '信息技术部：系统可用率达99.9%', '保障生产系统稳定运行，提升技术支持效率', 'DEPARTMENT', 1, 8, NULL, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (10, '杨帆：完成风险评估报告20份', '按季度完成风险评估，及时发出风险预警', 'PERSONAL', 4, 2, 10, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (11, '黄斌：风控系统优化方案编写', '完成风控系统升级需求分析和优化方案', 'PERSONAL', 4, 2, 11, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (12, '徐婷：季度财务报告准确率100%', '确保季度财务报表零差错', 'PERSONAL', 6, 4, 14, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `performance_goal` VALUES (13, '朱琳：客户回访覆盖率达95%', '定期完成客户回访，收集反馈建议', 'PERSONAL', 7, 5, 16, 2026, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');

-- ----------------------------
-- Table structure for salary_adjustment
-- ----------------------------
DROP TABLE IF EXISTS `salary_adjustment`;
CREATE TABLE `salary_adjustment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `task_id` bigint NOT NULL COMMENT '考核任务ID',
  `user_id` bigint NOT NULL COMMENT '员工ID',
  `plan_id` bigint NOT NULL COMMENT '考核方案ID',
  `grade` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '绩效等级',
  `final_score` decimal(8, 2) NULL DEFAULT NULL COMMENT '考核得分',
  `base_salary` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '基本薪资（AES加密）',
  `adjustment_rate` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '调薪比例（%）（AES加密）',
  `bonus_amount` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '奖金金额（AES加密）',
  `suggestion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '薪酬调整建议',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待审批 1-已审批 2-已驳回',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '薪酬调整表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of salary_adjustment
-- ----------------------------
INSERT INTO `salary_adjustment` VALUES (1, 1, 2, 1, 'A', 90.00, 'ENC:SolOrFe1SNd8N2cX//riqQ==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:ELUMYOenUhTSrjU03YND7g==', '绩效优秀，建议大幅调薪并发放年终奖金', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `salary_adjustment` VALUES (2, 2, 3, 1, 'B', 83.40, 'ENC:6VRI6ku6rohJ8q4pLZG+Jg==', 'ENC:93eYseSrYqMYnQjPoIeLeQ==', 'ENC:i1V6XEvRWagzjw8PRy/uwg==', '绩效良好，建议适度调薪并发放奖金', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `salary_adjustment` VALUES (3, 3, 4, 1, 'A', 92.60, 'ENC:kM2PyhVprPXtDoy2h0oWbw==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:qA5ZWippnPBTOt8Tc2Tq7w==', '绩效优秀，建议大幅调薪并发放年终奖金', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `salary_adjustment` VALUES (4, 5, 6, 1, 'A', 92.20, 'ENC:kM2PyhVprPXtDoy2h0oWbw==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:/XSUTv97lRttS519HmFXcQ==', '绩效优秀，客户满意度提升显著', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `salary_adjustment` VALUES (5, 7, 8, 1, 'A', 95.00, 'ENC:12y2b3+twVqksudVrkhGrQ==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:FK6XmXK9P5z0/lB5S+Nrhg==', '合规工作卓越，建议晋级', 0, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `salary_adjustment` VALUES (6, 10, 11, 1, 'C', 72.00, 'ENC:Ju/xClXuYm9AZriwGAwRig==', 'ENC:RuiUNNEDFpAmsnDwrxHYIQ==', 'ENC:Bf//X51HOu6JMjWw6/rfPQ==', '绩效合格，建议小幅调薪', 0, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `salary_adjustment` VALUES (7, 12, 13, 1, 'D', 59.20, 'ENC:NBepE7Ti85Oa1r6oHLuOIA==', 'ENC:lhIZdwS8cuhjCwjypYgacA==', 'ENC:lhIZdwS8cuhjCwjypYgacA==', '绩效不合格，建议维持薪资并制定改进计划', 2, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `salary_adjustment` VALUES (8, 13, 14, 1, 'A', 92.80, 'ENC:6WZfn57WoSXfB4PrFb0HbA==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:Bf//X51HOu6JMjWw6/rfPQ==', '财务工作出色，建议大幅调薪', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `salary_adjustment` VALUES (9, 19, 20, 1, 'A', 91.80, 'ENC:6VRI6ku6rohJ8q4pLZG+Jg==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:ELUMYOenUhTSrjU03YND7g==', '技术能力突出，建议大幅调薪', 0, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `salary_adjustment` VALUES (10, 4, 5, 1, 'B', 86.00, NULL, 'ENC:93eYseSrYqMYnQjPoIeLeQ==', NULL, '绩效良好，建议适度调薪并发放奖金', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (11, 6, 7, 1, 'B', 80.40, NULL, 'ENC:93eYseSrYqMYnQjPoIeLeQ==', NULL, '绩效良好，建议适度调薪并发放奖金', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (12, 8, 9, 1, 'B', 78.80, NULL, 'ENC:93eYseSrYqMYnQjPoIeLeQ==', NULL, '绩效良好，建议适度调薪并发放奖金', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (13, 9, 10, 1, 'B', 87.60, NULL, 'ENC:93eYseSrYqMYnQjPoIeLeQ==', NULL, '绩效良好，建议适度调薪并发放奖金', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (14, 11, 12, 1, 'B', 89.40, NULL, 'ENC:93eYseSrYqMYnQjPoIeLeQ==', NULL, '绩效良好，建议适度调薪并发放奖金', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (15, 14, 15, 1, 'B', 75.00, NULL, 'ENC:93eYseSrYqMYnQjPoIeLeQ==', NULL, '绩效良好，建议适度调薪并发放奖金', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (16, 15, 16, 1, 'A', 90.80, NULL, 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', NULL, '绩效优秀，建议大幅调薪并发放年终奖金', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (17, 16, 17, 1, 'C', 69.20, NULL, 'ENC:RuiUNNEDFpAmsnDwrxHYIQ==', NULL, '绩效合格，建议小幅调薪', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (18, 17, 18, 1, 'B', 86.80, NULL, 'ENC:93eYseSrYqMYnQjPoIeLeQ==', NULL, '绩效良好，建议适度调薪并发放奖金', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (19, 18, 19, 1, 'C', 63.40, NULL, 'ENC:RuiUNNEDFpAmsnDwrxHYIQ==', NULL, '绩效合格，建议小幅调薪', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (20, 20, 21, 1, 'B', 78.00, NULL, 'ENC:93eYseSrYqMYnQjPoIeLeQ==', NULL, '绩效良好，建议适度调薪并发放奖金', 0, '2026-05-25 08:58:31', '2026-05-25 08:58:31');
INSERT INTO `salary_adjustment` VALUES (21, 105, 2, 3, 'D', 7.71, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (22, 106, 3, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (23, 107, 4, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (24, 108, 5, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (25, 109, 6, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (26, 110, 7, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (27, 111, 8, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (28, 112, 9, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (29, 113, 10, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (30, 114, 11, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (31, 115, 12, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (32, 116, 13, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (33, 117, 14, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (34, 118, 15, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (35, 119, 16, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (36, 120, 17, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (37, 121, 18, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (38, 122, 19, 3, 'D', 0.00, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (39, 123, 20, 3, 'D', 49.41, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (40, 124, 21, 3, 'D', 50.66, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:31', '2026-05-25 08:59:31');
INSERT INTO `salary_adjustment` VALUES (41, 101, 2, 5, 'A', 93.48, NULL, 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', NULL, '绩效优秀，建议大幅调薪并发放年终奖金', 0, '2026-05-25 08:59:39', '2026-05-25 08:59:39');
INSERT INTO `salary_adjustment` VALUES (42, 102, 10, 5, 'B', 78.11, NULL, 'ENC:93eYseSrYqMYnQjPoIeLeQ==', NULL, '绩效良好，建议适度调薪并发放奖金', 0, '2026-05-25 08:59:39', '2026-05-25 08:59:39');
INSERT INTO `salary_adjustment` VALUES (43, 103, 14, 5, 'C', 63.84, NULL, 'ENC:RuiUNNEDFpAmsnDwrxHYIQ==', NULL, '绩效合格，建议小幅调薪', 0, '2026-05-25 08:59:39', '2026-05-25 08:59:39');
INSERT INTO `salary_adjustment` VALUES (44, 104, 21, 5, 'D', 49.30, NULL, 'ENC:lhIZdwS8cuhjCwjypYgacA==', NULL, '绩效不合格，建议维持薪资并制定改进计划', 0, '2026-05-25 08:59:39', '2026-05-25 08:59:39');

-- ----------------------------
-- Table structure for sys_announcement
-- ----------------------------
DROP TABLE IF EXISTS `sys_announcement`;
CREATE TABLE `sys_announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `publisher_id` bigint NOT NULL COMMENT '发布人ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-草稿 1-已发布',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_announcement
-- ----------------------------
INSERT INTO `sys_announcement` VALUES (1, '2026年第一季度绩效考核通知', '各部门、全体员工：\n\n根据公司绩效管理制度，现启动2026年第一季度绩效考核工作。请各位员工在规定时间内完成自评，部门经理完成下属评价。\n\n考核时间：2026年4月1日至4月30日\n自评截止：2026年4月15日\n上级评价截止：2026年4月25日\n\n请大家积极配合，按时完成。\n\n人力资源部\n2026年4月1日', 1, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_announcement` VALUES (2, '关于调整绩效考核指标权重的通知', '各部门负责人：\n\n为更好地适应公司战略发展要求，经管理层研究决定，对2026年度部分绩效考核指标权重进行调整：\n\n1. 合规类指标权重由15%提升至20%\n2. 风控类指标权重由10%提升至15%\n3. 运营类指标权重相应调整\n\n具体调整方案请查看考核系统中的最新方案配置。\n\n总经理办公室\n2026年3月15日', 1, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_announcement` VALUES (3, '员工培训计划公示', '全体员工：\n\n为提升员工专业能力和综合素质，人力资源部制定了2026年度培训计划，主要包括：\n\n1. 金融合规知识培训（全员必修）\n2. 风险管理专题培训（风控部门及管理层）\n3. 客户服务技巧提升（客服部门）\n4. 领导力发展项目（中层管理者）\n\n培训完成情况将纳入绩效考核，请各位员工合理安排时间，积极参与。\n\n人力资源部\n2026年2月20日', 2, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_announcement` VALUES (4, '绩效申诉流程说明', '各位员工：\n\n为保障绩效考核的公平公正，现将绩效申诉流程公示如下：\n\n1. 申诉时限：考核结果公布后5个工作日内\n2. 申诉途径：通过绩效考核系统在线提交\n3. 处理时限：收到申诉后3个工作日内完成初审\n4. 反馈方式：系统内通知\n\n如有疑问，请联系人力资源部。\n\n人力资源部\n2026年1月10日', 2, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_announcement` VALUES (5, '系统维护通知', '全体用户：\n\n绩效考核系统将于2026年4月20日（周日）22:00-24:00进行系统升级维护，届时系统将暂时无法访问。\n\n请各位提前做好工作安排，避开维护时段。\n\n信息技术部\n2026年4月18日', 1, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');

-- ----------------------------
-- Table structure for sys_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '上级部门ID，0表示顶级部门',
  `manager_id` bigint NULL DEFAULT NULL COMMENT '部门经理用户ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_department
-- ----------------------------
INSERT INTO `sys_department` VALUES (1, '总经理办公室', 0, NULL, '公司最高管理层，负责战略决策和整体运营', 1, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_department` VALUES (2, '风险管理部', 0, 4, '负责公司整体风险评估、风险预警与控制', 2, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_department` VALUES (3, '合规管理部', 0, 8, '负责合规监管事务，确保公司运营符合法律法规', 3, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_department` VALUES (4, '财务管理部', 0, 5, '负责财务管理、资金运作与财务报告', 4, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_department` VALUES (5, '客户服务部', 0, 6, '负责客户关系维护、客户服务和投诉处理', 5, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_department` VALUES (6, '投资银行部', 0, 7, '负责投行业务、并购顾问和资本市场运作', 6, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_department` VALUES (7, '人力资源部', 0, 2, '负责人事管理、薪酬福利和绩效考核', 7, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_department` VALUES (8, '信息技术部', 0, 9, '负责系统开发、运维和信息安全', 8, 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作用户名',
  `operation` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作描述',
  `method` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `result` tinyint NULL DEFAULT NULL COMMENT '操作结果：0-失败 1-成功',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 81 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------
INSERT INTO `sys_operation_log` VALUES (1, 1, 'admin', '用户登录', 'POST /api/auth/login', '{\"username\":\"admin\"}', '192.168.1.100', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (2, 1, 'admin', '新增考核方案', 'POST /api/plan', '{\"name\":\"2026年第一季度绩效考核\"}', '192.168.1.100', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (3, 1, 'admin', '启动考核方案', 'POST /api/plan/start/1', '{}', '192.168.1.100', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (4, 2, 'hr_zhang', '用户登录', 'POST /api/auth/login', '{\"username\":\"hr_zhang\"}', '192.168.1.101', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (5, 2, 'hr_zhang', '发布公告', 'POST /api/announcement', '{\"title\":\"员工培训计划公示\"}', '192.168.1.101', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (6, 4, 'mgr_wang', '用户登录', 'POST /api/auth/login', '{\"username\":\"mgr_wang\"}', '192.168.1.102', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (7, 4, 'mgr_wang', '提交考核评分', 'POST /api/task/score', '{\"taskId\":9,\"scoreType\":\"MANAGER\"}', '192.168.1.102', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (8, 10, 'emp_yang', '用户登录', 'POST /api/auth/login', '{\"username\":\"emp_yang\"}', '192.168.1.103', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (9, 10, 'emp_yang', '提交考核评分', 'POST /api/task/score', '{\"taskId\":9,\"scoreType\":\"SELF\"}', '192.168.1.103', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (10, 1, 'admin', '完成考核方案', 'POST /api/plan/complete/1', '{}', '192.168.1.100', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (11, 1, 'admin', '导出考核结果Excel', 'GET /api/report/exportExcel/1', '{}', '192.168.1.100', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (12, 2, 'hr_zhang', '生成薪酬调整建议', 'POST /api/salary/generate/1', '{}', '192.168.1.101', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (13, 2, 'hr_zhang', '审批薪酬调整', 'PUT /api/salary/approve/1/1', '{}', '192.168.1.101', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (14, 13, 'emp_zheng', '提交绩效申诉', 'POST /api/appeal', '{\"taskId\":12}', '192.168.1.104', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (15, 2, 'hr_zhang', '处理绩效申诉', 'PUT /api/appeal/handle/1', '{\"status\":2}', '192.168.1.101', 1, NULL, '2026-05-09 19:39:14');
INSERT INTO `sys_operation_log` VALUES (16, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"admin123\"}]', '0:0:0:0:0:0:0:1', 0, '用户名或密码错误', '2026-05-09 19:40:14');
INSERT INTO `sys_operation_log` VALUES (17, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-09 19:40:18');
INSERT INTO `sys_operation_log` VALUES (18, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-09 19:41:20');
INSERT INTO `sys_operation_log` VALUES (19, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-10 16:55:55');
INSERT INTO `sys_operation_log` VALUES (20, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-10 17:10:11');
INSERT INTO `sys_operation_log` VALUES (21, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-10 17:12:42');
INSERT INTO `sys_operation_log` VALUES (22, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-12 16:37:39');
INSERT INTO `sys_operation_log` VALUES (23, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-12 17:32:29');
INSERT INTO `sys_operation_log` VALUES (24, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-13 15:54:59');
INSERT INTO `sys_operation_log` VALUES (25, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"hr_zhang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-13 15:55:35');
INSERT INTO `sys_operation_log` VALUES (26, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"emp_tang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-13 15:56:12');
INSERT INTO `sys_operation_log` VALUES (27, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-13 16:01:38');
INSERT INTO `sys_operation_log` VALUES (28, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"emp_tang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-13 16:11:46');
INSERT INTO `sys_operation_log` VALUES (29, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-18 22:38:16');
INSERT INTO `sys_operation_log` VALUES (30, 1, 'admin', '导出考核结果Excel', 'com.performance.controller.ReportController.exportExcel', '[1,{\"response\":{\"response\":{\"response\":{}}}}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-18 22:38:50');
INSERT INTO `sys_operation_log` VALUES (31, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"emp_tang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-18 22:40:05');
INSERT INTO `sys_operation_log` VALUES (32, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"emp_gao\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-18 22:40:31');
INSERT INTO `sys_operation_log` VALUES (33, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:23:29');
INSERT INTO `sys_operation_log` VALUES (34, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"emp_tang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:23:51');
INSERT INTO `sys_operation_log` VALUES (35, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"emp_luo\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:28:31');
INSERT INTO `sys_operation_log` VALUES (36, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:35:49');
INSERT INTO `sys_operation_log` VALUES (37, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"emp_jiang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:37:19');
INSERT INTO `sys_operation_log` VALUES (38, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"emp_tang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:38:25');
INSERT INTO `sys_operation_log` VALUES (39, 1, 'admin', '完成考核方案', 'com.performance.controller.EvaluationPlanController.complete', '[5]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:41:30');
INSERT INTO `sys_operation_log` VALUES (40, 1, 'admin', '更新考核方案', 'com.performance.controller.EvaluationPlanController.update', '[{\"id\":3,\"name\":\"2026年年度绩效考核\",\"year\":2026,\"periodType\":\"ANNUALLY\",\"periodNumber\":1,\"startDate\":1777478400000,\"endDate\":1782748800000,\"gradeAMin\":90,\"gradeBMin\":75,\"gradeCMin\":60,\"selfWeight\":15,\"managerWeight\":55,\"peerWeight\":30,\"status\":0,\"creatorId\":1,\"creatorName\":\"系统管理员\",\"createTime\":1778326754000,\"updateTime\":1778326754000}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:50:05');
INSERT INTO `sys_operation_log` VALUES (41, 1, 'admin', '新增评分规则', 'com.performance.controller.EvaluationPlanController.addRule', '[{\"planId\":3,\"kpiId\":2,\"kpiName\":\"风险控制得分\",\"weight\":10,\"targetValue\":90,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:50:19');
INSERT INTO `sys_operation_log` VALUES (42, 1, 'admin', '新增评分规则', 'com.performance.controller.EvaluationPlanController.addRule', '[{\"planId\":3,\"kpiId\":8,\"kpiName\":\"新客户开发数\",\"weight\":10,\"targetValue\":50,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:50:28');
INSERT INTO `sys_operation_log` VALUES (43, 1, 'admin', '新增评分规则', 'com.performance.controller.EvaluationPlanController.addRule', '[{\"planId\":3,\"kpiId\":1,\"kpiName\":\"合规完成率\",\"weight\":10,\"targetValue\":100,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:02');
INSERT INTO `sys_operation_log` VALUES (44, 1, 'admin', '删除评分规则', 'com.performance.controller.EvaluationPlanController.deleteRule', '[25]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:12');
INSERT INTO `sys_operation_log` VALUES (45, 1, 'admin', '删除评分规则', 'com.performance.controller.EvaluationPlanController.deleteRule', '[24]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:13');
INSERT INTO `sys_operation_log` VALUES (46, 1, 'admin', '删除评分规则', 'com.performance.controller.EvaluationPlanController.deleteRule', '[23]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:14');
INSERT INTO `sys_operation_log` VALUES (47, 1, 'admin', '新增评分规则', 'com.performance.controller.EvaluationPlanController.addRule', '[{\"planId\":3,\"kpiId\":1,\"kpiName\":\"合规完成率\",\"weight\":10,\"targetValue\":100,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:17');
INSERT INTO `sys_operation_log` VALUES (48, 1, 'admin', '新增评分规则', 'com.performance.controller.EvaluationPlanController.addRule', '[{\"planId\":3,\"kpiId\":2,\"kpiName\":\"风险控制得分\",\"weight\":10,\"targetValue\":90,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:19');
INSERT INTO `sys_operation_log` VALUES (49, 1, 'admin', '新增评分规则', 'com.performance.controller.EvaluationPlanController.addRule', '[{\"planId\":3,\"kpiId\":3,\"kpiName\":\"客户资产增长率\",\"weight\":10,\"targetValue\":15,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:21');
INSERT INTO `sys_operation_log` VALUES (50, 1, 'admin', '新增评分规则', 'com.performance.controller.EvaluationPlanController.addRule', '[{\"planId\":3,\"kpiId\":4,\"kpiName\":\"业务办理差错率\",\"weight\":10,\"targetValue\":0.5,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:23');
INSERT INTO `sys_operation_log` VALUES (51, 1, 'admin', '更新评分规则', 'com.performance.controller.EvaluationPlanController.updateRule', '[{\"id\":26,\"planId\":3,\"kpiId\":1,\"kpiName\":\"合规完成率\",\"weight\":10,\"targetValue\":100,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100,\"createTime\":1779627077000,\"updateTime\":1779627077000}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:24');
INSERT INTO `sys_operation_log` VALUES (52, 1, 'admin', '更新评分规则', 'com.performance.controller.EvaluationPlanController.updateRule', '[{\"id\":27,\"planId\":3,\"kpiId\":2,\"kpiName\":\"风险控制得分\",\"weight\":10,\"targetValue\":90,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100,\"createTime\":1779627079000,\"updateTime\":1779627079000}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:24');
INSERT INTO `sys_operation_log` VALUES (53, 1, 'admin', '更新评分规则', 'com.performance.controller.EvaluationPlanController.updateRule', '[{\"id\":28,\"planId\":3,\"kpiId\":3,\"kpiName\":\"客户资产增长率\",\"weight\":10,\"targetValue\":15,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100,\"createTime\":1779627081000,\"updateTime\":1779627081000}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:24');
INSERT INTO `sys_operation_log` VALUES (54, 1, 'admin', '更新评分规则', 'com.performance.controller.EvaluationPlanController.updateRule', '[{\"id\":29,\"planId\":3,\"kpiId\":4,\"kpiName\":\"业务办理差错率\",\"weight\":10,\"targetValue\":0.5,\"scoringFormula\":\"WEIGHTED_SUM\",\"maxScore\":100,\"createTime\":1779627083000,\"updateTime\":1779627083000}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:24');
INSERT INTO `sys_operation_log` VALUES (55, 1, 'admin', '启动考核方案', 'com.performance.controller.EvaluationPlanController.start', '[3]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:51:35');
INSERT INTO `sys_operation_log` VALUES (56, 21, 'emp_jiang', '提交考核评分', 'com.performance.controller.EvaluationTaskController.submitScore', '[{\"taskId\":124,\"scoreType\":\"SELF\",\"remark\":\"very good\",\"scores\":[{\"kpiId\":1,\"score\":100,\"comment\":\"\"},{\"kpiId\":2,\"score\":90,\"comment\":\"\"},{\"kpiId\":3,\"score\":15,\"comment\":\"\"},{\"kpiId\":4,\"score\":0.5,\"comment\":\"\"}]}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:53:27');
INSERT INTO `sys_operation_log` VALUES (57, 20, 'emp_tang', '提交考核评分', 'com.performance.controller.EvaluationTaskController.submitScore', '[{\"taskId\":123,\"scoreType\":\"SELF\",\"remark\":\"好\",\"scores\":[{\"kpiId\":1,\"score\":100,\"comment\":\"\"},{\"kpiId\":2,\"score\":90,\"comment\":\"\"},{\"kpiId\":3,\"score\":15,\"comment\":\"\"},{\"kpiId\":4,\"score\":0.5,\"comment\":\"\"}]}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:54:23');
INSERT INTO `sys_operation_log` VALUES (58, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"hr_zhang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:54:39');
INSERT INTO `sys_operation_log` VALUES (59, 2, 'hr_zhang', '提交考核评分', 'com.performance.controller.EvaluationTaskController.submitScore', '[{\"taskId\":105,\"scoreType\":\"SELF\",\"remark\":\"好\",\"scores\":[{\"kpiId\":1,\"score\":100,\"comment\":\"\"},{\"kpiId\":2,\"score\":90,\"comment\":\"\"},{\"kpiId\":3,\"score\":15,\"comment\":\"\"},{\"kpiId\":4,\"score\":0.5,\"comment\":\"\"}]}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:55:08');
INSERT INTO `sys_operation_log` VALUES (60, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"mgr_zhou\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:56:02');
INSERT INTO `sys_operation_log` VALUES (61, 9, 'mgr_zhou', '提交考核评分', 'com.performance.controller.EvaluationTaskController.submitScore', '[{\"taskId\":123,\"scoreType\":\"MANAGER\",\"scores\":[{\"kpiId\":1,\"score\":100,\"comment\":\"\"},{\"kpiId\":2,\"score\":78,\"comment\":\"\"},{\"kpiId\":3,\"score\":12,\"comment\":\"\"},{\"kpiId\":4,\"score\":1.3,\"comment\":\"\"}]}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:56:30');
INSERT INTO `sys_operation_log` VALUES (62, 9, 'mgr_zhou', '提交考核评分', 'com.performance.controller.EvaluationTaskController.submitScore', '[{\"taskId\":124,\"scoreType\":\"MANAGER\",\"scores\":[{\"kpiId\":1,\"score\":98,\"comment\":\"\"},{\"kpiId\":2,\"score\":89,\"comment\":\"\"},{\"kpiId\":3,\"score\":13,\"comment\":\"\"},{\"kpiId\":4,\"score\":0.3,\"comment\":\"\"}]}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:56:45');
INSERT INTO `sys_operation_log` VALUES (63, 20, 'emp_tang', '提交考核评分', 'com.performance.controller.EvaluationTaskController.submitScore', '[{\"taskId\":124,\"scoreType\":\"PEER\",\"scores\":[{\"kpiId\":1,\"score\":100,\"comment\":\"\"},{\"kpiId\":2,\"score\":90,\"comment\":\"\"},{\"kpiId\":3,\"score\":15,\"comment\":\"\"},{\"kpiId\":4,\"score\":0.5,\"comment\":\"\"}]}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 20:59:13');
INSERT INTO `sys_operation_log` VALUES (64, 9, 'mgr_zhou', '提交考核评分', 'com.performance.controller.EvaluationTaskController.submitScore', '[{\"taskId\":124,\"scoreType\":\"PEER\",\"scores\":[{\"kpiId\":1,\"score\":100,\"comment\":\"\"},{\"kpiId\":2,\"score\":90,\"comment\":\"\"},{\"kpiId\":3,\"score\":15,\"comment\":\"\"},{\"kpiId\":4,\"score\":0.5,\"comment\":\"\"}]}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 21:00:00');
INSERT INTO `sys_operation_log` VALUES (65, 21, 'emp_jiang', '提交考核评分', 'com.performance.controller.EvaluationTaskController.submitScore', '[{\"taskId\":123,\"scoreType\":\"PEER\",\"scores\":[{\"kpiId\":1,\"score\":100,\"comment\":\"\"},{\"kpiId\":2,\"score\":90,\"comment\":\"\"},{\"kpiId\":3,\"score\":15,\"comment\":\"\"},{\"kpiId\":4,\"score\":0.2,\"comment\":\"\"}]}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 21:00:37');
INSERT INTO `sys_operation_log` VALUES (66, 1, 'admin', '完成考核方案', 'com.performance.controller.EvaluationPlanController.complete', '[3]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 21:01:26');
INSERT INTO `sys_operation_log` VALUES (67, 1, 'admin', '导出考核结果PDF', 'com.performance.controller.ReportController.exportPdf', '[1,{\"response\":{\"response\":{\"response\":{}}}}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 23:15:49');
INSERT INTO `sys_operation_log` VALUES (68, 1, 'admin', '导出考核结果Excel', 'com.performance.controller.ReportController.exportExcel', '[1,{\"response\":{\"response\":{\"response\":{}}}}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-24 23:16:08');
INSERT INTO `sys_operation_log` VALUES (69, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 08:57:22');
INSERT INTO `sys_operation_log` VALUES (70, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"hr_zhang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 08:58:01');
INSERT INTO `sys_operation_log` VALUES (71, 2, 'hr_zhang', '生成薪酬调整建议', 'com.performance.controller.SalaryAdjustmentController.generate', '[1]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 08:58:31');
INSERT INTO `sys_operation_log` VALUES (72, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"emp_tang\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 08:59:03');
INSERT INTO `sys_operation_log` VALUES (73, 2, 'hr_zhang', '生成薪酬调整建议', 'com.performance.controller.SalaryAdjustmentController.generate', '[3]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 08:59:31');
INSERT INTO `sys_operation_log` VALUES (74, 2, 'hr_zhang', '生成薪酬调整建议', 'com.performance.controller.SalaryAdjustmentController.generate', '[5]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 08:59:39');
INSERT INTO `sys_operation_log` VALUES (75, 1, 'admin', '生成个人发展计划', 'com.performance.controller.DevelopmentPlanController.generate', '[3]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 09:00:24');
INSERT INTO `sys_operation_log` VALUES (76, 1, 'admin', '生成个人发展计划', 'com.performance.controller.DevelopmentPlanController.generate', '[1]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 09:00:33');
INSERT INTO `sys_operation_log` VALUES (77, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 14:25:39');
INSERT INTO `sys_operation_log` VALUES (78, 1, 'admin', '生成薪酬调整建议', 'com.performance.controller.SalaryAdjustmentController.generate', '[1]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 14:28:47');
INSERT INTO `sys_operation_log` VALUES (79, 1, 'admin', '导出考核结果PDF', 'com.performance.controller.ReportController.exportPdf', '[1,{\"response\":{\"response\":{\"response\":{}}}}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 14:31:15');
INSERT INTO `sys_operation_log` VALUES (80, 1, 'admin', '导出考核结果Excel', 'com.performance.controller.ReportController.exportExcel', '[1,{\"response\":{\"response\":{\"response\":{}}}}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 14:35:40');
INSERT INTO `sys_operation_log` VALUES (81, NULL, NULL, '用户登录', 'com.performance.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1, NULL, '2026-05-25 15:08:57');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像路径',
  `department_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'EMPLOYEE' COMMENT '角色：ADMIN-系统管理员 HR-HR专员 MANAGER-部门经理 EMPLOYEE-员工',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '系统管理员', 'admin@finance.com', '13800000000', 'https://api.dicebear.com/7.x/initials/svg?seed=Admin', NULL, 'ADMIN', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (2, 'hr_zhang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '张慧', 'zhang.hui@finance.com', '13800000001', 'https://api.dicebear.com/7.x/initials/svg?seed=ZH', 7, 'HR', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (3, 'hr_liu', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '刘敏', 'liu.min@finance.com', '13800000002', 'https://api.dicebear.com/7.x/initials/svg?seed=LM', 7, 'HR', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (4, 'mgr_wang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '王强', 'wang.qiang@finance.com', '13800000003', 'https://api.dicebear.com/7.x/initials/svg?seed=WQ', 2, 'MANAGER', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (5, 'mgr_li', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '李娜', 'li.na@finance.com', '13800000004', 'https://api.dicebear.com/7.x/initials/svg?seed=LN', 4, 'MANAGER', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (6, 'mgr_chen', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '陈伟', 'chen.wei@finance.com', '13800000005', 'https://api.dicebear.com/7.x/initials/svg?seed=CW', 5, 'MANAGER', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (7, 'mgr_zhao', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '赵磊', 'zhao.lei@finance.com', '13800000006', 'https://api.dicebear.com/7.x/initials/svg?seed=ZL', 6, 'MANAGER', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (8, 'mgr_sun', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '孙丽', 'sun.li@finance.com', '13800000007', 'https://api.dicebear.com/7.x/initials/svg?seed=SL', 3, 'MANAGER', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (9, 'mgr_zhou', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '周明', 'zhou.ming@finance.com', '13800000008', 'https://api.dicebear.com/7.x/initials/svg?seed=ZM', 8, 'MANAGER', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (10, 'emp_yang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '杨帆', 'yang.fan@finance.com', '13800000009', 'https://api.dicebear.com/7.x/initials/svg?seed=YF', 2, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (11, 'emp_huang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '黄斌', 'huang.bin@finance.com', '13800000010', 'https://api.dicebear.com/7.x/initials/svg?seed=HB', 2, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (12, 'emp_wu', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '吴静', 'wu.jing@finance.com', '13800000011', 'https://api.dicebear.com/7.x/initials/svg?seed=WJ', 3, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (13, 'emp_zheng', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '郑浩', 'zheng.hao@finance.com', '13800000012', 'https://api.dicebear.com/7.x/initials/svg?seed=ZHao', 3, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (14, 'emp_xu', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '徐婷', 'xu.ting@finance.com', '13800000013', 'https://api.dicebear.com/7.x/initials/svg?seed=XT', 4, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (15, 'emp_ma', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '马飞', 'ma.fei@finance.com', '13800000014', 'https://api.dicebear.com/7.x/initials/svg?seed=MF', 4, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (16, 'emp_zhu', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '朱琳', 'zhu.lin@finance.com', '13800000015', 'https://api.dicebear.com/7.x/initials/svg?seed=ZhuL', 5, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (17, 'emp_he', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '何涛', 'he.tao@finance.com', '13800000016', 'https://api.dicebear.com/7.x/initials/svg?seed=HT', 5, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (18, 'emp_luo', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '罗峰', 'luo.feng@finance.com', '13800000017', 'https://api.dicebear.com/7.x/initials/svg?seed=LF', 6, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (19, 'emp_gao', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '高雪', 'gao.xue@finance.com', '13800000018', 'https://api.dicebear.com/7.x/initials/svg?seed=GX', 6, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (20, 'emp_tang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '唐杰', 'tang.jie@finance.com', '13800000019', 'https://api.dicebear.com/7.x/initials/svg?seed=TJ', 8, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');
INSERT INTO `sys_user` VALUES (21, 'emp_jiang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '蒋文', 'jiang.wen@finance.com', '13800000020', 'https://api.dicebear.com/7.x/initials/svg?seed=JW', 8, 'EMPLOYEE', 1, '2026-05-09 19:39:14', '2026-05-09 19:39:14');

SET FOREIGN_KEY_CHECKS = 1;
