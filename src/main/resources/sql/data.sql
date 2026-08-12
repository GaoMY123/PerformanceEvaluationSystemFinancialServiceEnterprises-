-- ========================================
-- 金融服务类企业绩效考核系统 - 演示数据脚本
-- 注意：先执行 init.sql 建表，再执行本脚本
-- 所有用户密码统一为：123456
-- 若未执行 data.sql，系统启动时会由 AdminInitializer 自动创建 admin/123456
-- ========================================

USE `performance_db`;

-- 清空所有数据（按依赖顺序，先子表后父表）
DELETE FROM `appeal`;
DELETE FROM `development_plan`;
DELETE FROM `salary_adjustment`;
DELETE FROM `evaluation_score`;
DELETE FROM `evaluation_attachment`;
DELETE FROM `peer_assignment`;
DELETE FROM `evaluation_task`;
DELETE FROM `evaluation_rule`;
DELETE FROM `evaluation_plan`;
DELETE FROM `goal_kpi`;
DELETE FROM `performance_goal`;
DELETE FROM `kpi_indicator`;
DELETE FROM `sys_operation_log`;
DELETE FROM `sys_announcement`;
DELETE FROM `sys_user`;
DELETE FROM `sys_department`;

-- ========================================
-- 1. 部门数据
-- ========================================
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `manager_id`, `description`, `sort_order`, `status`, `create_time`) VALUES
(1, '总经理办公室', 0, NULL, '公司最高管理层，负责战略决策和整体运营', 1, 1, NOW()),
(2, '风险管理部', 0, NULL, '负责公司整体风险评估、风险预警与控制', 2, 1, NOW()),
(3, '合规管理部', 0, NULL, '负责合规监管事务，确保公司运营符合法律法规', 3, 1, NOW()),
(4, '财务管理部', 0, NULL, '负责财务管理、资金运作与财务报告', 4, 1, NOW()),
(5, '客户服务部', 0, NULL, '负责客户关系维护、客户服务和投诉处理', 5, 1, NOW()),
(6, '投资银行部', 0, NULL, '负责投行业务、并购顾问和资本市场运作', 6, 1, NOW()),
(7, '人力资源部', 0, NULL, '负责人事管理、薪酬福利和绩效考核', 7, 1, NOW()),
(8, '信息技术部', 0, NULL, '负责系统开发、运维和信息安全', 8, 1, NOW());

-- ========================================
-- 2. 用户数据
-- 所有用户密码统一为 123456
-- BCrypt("123456") = $2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq
-- ========================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `email`, `phone`, `avatar`, `department_id`, `role`, `status`, `create_time`) VALUES
-- 管理员（密码: 123456）
(1, 'admin', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '系统管理员', 'admin@finance.com', '13800000000', 'https://api.dicebear.com/7.x/initials/svg?seed=Admin', NULL, 'ADMIN', 1, NOW()),
-- HR专员
(2, 'hr_zhang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '张慧', 'zhang.hui@finance.com', '13800000001', 'https://api.dicebear.com/7.x/initials/svg?seed=ZH', 7, 'HR', 1, NOW()),
(3, 'hr_liu', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '刘敏', 'liu.min@finance.com', '13800000002', 'https://api.dicebear.com/7.x/initials/svg?seed=LM', 7, 'HR', 1, NOW()),
-- 部门经理
(4, 'mgr_wang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '王强', 'wang.qiang@finance.com', '13800000003', 'https://api.dicebear.com/7.x/initials/svg?seed=WQ', 2, 'MANAGER', 1, NOW()),
(5, 'mgr_li', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '李娜', 'li.na@finance.com', '13800000004', 'https://api.dicebear.com/7.x/initials/svg?seed=LN', 4, 'MANAGER', 1, NOW()),
(6, 'mgr_chen', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '陈伟', 'chen.wei@finance.com', '13800000005', 'https://api.dicebear.com/7.x/initials/svg?seed=CW', 5, 'MANAGER', 1, NOW()),
(7, 'mgr_zhao', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '赵磊', 'zhao.lei@finance.com', '13800000006', 'https://api.dicebear.com/7.x/initials/svg?seed=ZL', 6, 'MANAGER', 1, NOW()),
(8, 'mgr_sun', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '孙丽', 'sun.li@finance.com', '13800000007', 'https://api.dicebear.com/7.x/initials/svg?seed=SL', 3, 'MANAGER', 1, NOW()),
(9, 'mgr_zhou', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '周明', 'zhou.ming@finance.com', '13800000008', 'https://api.dicebear.com/7.x/initials/svg?seed=ZM', 8, 'MANAGER', 1, NOW()),
-- 普通员工 - 风险管理部
(10, 'emp_yang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '杨帆', 'yang.fan@finance.com', '13800000009', 'https://api.dicebear.com/7.x/initials/svg?seed=YF', 2, 'EMPLOYEE', 1, NOW()),
(11, 'emp_huang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '黄斌', 'huang.bin@finance.com', '13800000010', 'https://api.dicebear.com/7.x/initials/svg?seed=HB', 2, 'EMPLOYEE', 1, NOW()),
-- 普通员工 - 合规管理部
(12, 'emp_wu', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '吴静', 'wu.jing@finance.com', '13800000011', 'https://api.dicebear.com/7.x/initials/svg?seed=WJ', 3, 'EMPLOYEE', 1, NOW()),
(13, 'emp_zheng', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '郑浩', 'zheng.hao@finance.com', '13800000012', 'https://api.dicebear.com/7.x/initials/svg?seed=ZHao', 3, 'EMPLOYEE', 1, NOW()),
-- 普通员工 - 财务管理部
(14, 'emp_xu', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '徐婷', 'xu.ting@finance.com', '13800000013', 'https://api.dicebear.com/7.x/initials/svg?seed=XT', 4, 'EMPLOYEE', 1, NOW()),
(15, 'emp_ma', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '马飞', 'ma.fei@finance.com', '13800000014', 'https://api.dicebear.com/7.x/initials/svg?seed=MF', 4, 'EMPLOYEE', 1, NOW()),
-- 普通员工 - 客户服务部
(16, 'emp_zhu', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '朱琳', 'zhu.lin@finance.com', '13800000015', 'https://api.dicebear.com/7.x/initials/svg?seed=ZhuL', 5, 'EMPLOYEE', 1, NOW()),
(17, 'emp_he', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '何涛', 'he.tao@finance.com', '13800000016', 'https://api.dicebear.com/7.x/initials/svg?seed=HT', 5, 'EMPLOYEE', 1, NOW()),
-- 普通员工 - 投资银行部
(18, 'emp_luo', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '罗峰', 'luo.feng@finance.com', '13800000017', 'https://api.dicebear.com/7.x/initials/svg?seed=LF', 6, 'EMPLOYEE', 1, NOW()),
(19, 'emp_gao', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '高雪', 'gao.xue@finance.com', '13800000018', 'https://api.dicebear.com/7.x/initials/svg?seed=GX', 6, 'EMPLOYEE', 1, NOW()),
-- 普通员工 - 信息技术部
(20, 'emp_tang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '唐杰', 'tang.jie@finance.com', '13800000019', 'https://api.dicebear.com/7.x/initials/svg?seed=TJ', 8, 'EMPLOYEE', 1, NOW()),
(21, 'emp_jiang', '$2a$10$6DdbY6cpPzi6YMgTHxfLRub8.wDRDEZnl3MXUNFdlcbdo9DQ2aMgq', '蒋文', 'jiang.wen@finance.com', '13800000020', 'https://api.dicebear.com/7.x/initials/svg?seed=JW', 8, 'EMPLOYEE', 1, NOW());

-- 更新部门经理
UPDATE `sys_department` SET `manager_id` = 4 WHERE `id` = 2;
UPDATE `sys_department` SET `manager_id` = 8 WHERE `id` = 3;
UPDATE `sys_department` SET `manager_id` = 5 WHERE `id` = 4;
UPDATE `sys_department` SET `manager_id` = 6 WHERE `id` = 5;
UPDATE `sys_department` SET `manager_id` = 7 WHERE `id` = 6;
UPDATE `sys_department` SET `manager_id` = 2 WHERE `id` = 7;
UPDATE `sys_department` SET `manager_id` = 9 WHERE `id` = 8;

-- ========================================
-- 3. KPI指标库（init.sql已有基础数据，此处清空后重新插入）
-- ========================================
INSERT INTO `kpi_indicator` (`id`, `name`, `category`, `description`, `formula`, `unit`, `target_value`, `max_score`, `status`, `create_time`) VALUES
(1, '合规完成率', 'COMPLIANCE', '各项合规任务的完成比例', '已完成合规任务数/总合规任务数×100', '%', 100.00, 100.00, 1, NOW()),
(2, '风险控制得分', 'RISK', '风险事件控制和预防的综合评分', '加权评分', '分', 90.00, 100.00, 1, NOW()),
(3, '客户资产增长率', 'CUSTOMER', '管理客户资产的增长比例', '(期末客户资产-期初客户资产)/期初客户资产×100', '%', 15.00, 100.00, 1, NOW()),
(4, '业务办理差错率', 'OPERATION', '业务办理中的差错比例，越低越好', '差错笔数/总业务笔数×100', '%', 0.50, 100.00, 1, NOW()),
(5, '营业收入完成率', 'FINANCIAL', '实际营业收入与目标的完成比例', '实际收入/目标收入×100', '%', 100.00, 100.00, 1, NOW()),
(6, '净利润增长率', 'FINANCIAL', '净利润同比增长率', '(本期净利润-上期净利润)/上期净利润×100', '%', 10.00, 100.00, 1, NOW()),
(7, '客户满意度', 'CUSTOMER', '客户满意度调查得分', '满意度问卷加权平均分', '分', 85.00, 100.00, 1, NOW()),
(8, '新客户开发数', 'CUSTOMER', '新开发客户数量', '实际新增客户数', '个', 50.00, 100.00, 1, NOW()),
(9, '培训完成率', 'OPERATION', '员工培训计划完成率', '已完成培训数/计划培训数×100', '%', 100.00, 100.00, 1, NOW()),
(10, '反洗钱合规率', 'COMPLIANCE', '反洗钱工作合规完成比例', '合规项数/总检查项数×100', '%', 100.00, 100.00, 1, NOW()),
(11, '内控制度执行率', 'COMPLIANCE', '内控制度各项要求的执行完成率', '已执行项数/应执行项数×100', '%', 95.00, 100.00, 1, NOW()),
(12, '项目回报率', 'FINANCIAL', '投资项目的投资回报率', '投资收益/投资成本×100', '%', 12.00, 100.00, 1, NOW());

-- ========================================
-- 4. 系统公告
-- ========================================
INSERT INTO `sys_announcement` (`id`, `title`, `content`, `publisher_id`, `status`, `create_time`) VALUES
(1, '2026年第一季度绩效考核通知', '各部门、全体员工：\n\n根据公司绩效管理制度，现启动2026年第一季度绩效考核工作。请各位员工在规定时间内完成自评，部门经理完成下属评价。\n\n考核时间：2026年4月1日至4月30日\n自评截止：2026年4月15日\n上级评价截止：2026年4月25日\n\n请大家积极配合，按时完成。\n\n人力资源部\n2026年4月1日', 1, 1, NOW()),
(2, '关于调整绩效考核指标权重的通知', '各部门负责人：\n\n为更好地适应公司战略发展要求，经管理层研究决定，对2026年度部分绩效考核指标权重进行调整：\n\n1. 合规类指标权重由15%提升至20%\n2. 风控类指标权重由10%提升至15%\n3. 运营类指标权重相应调整\n\n具体调整方案请查看考核系统中的最新方案配置。\n\n总经理办公室\n2026年3月15日', 1, 1, NOW()),
(3, '员工培训计划公示', '全体员工：\n\n为提升员工专业能力和综合素质，人力资源部制定了2026年度培训计划，主要包括：\n\n1. 金融合规知识培训（全员必修）\n2. 风险管理专题培训（风控部门及管理层）\n3. 客户服务技巧提升（客服部门）\n4. 领导力发展项目（中层管理者）\n\n培训完成情况将纳入绩效考核，请各位员工合理安排时间，积极参与。\n\n人力资源部\n2026年2月20日', 2, 1, NOW()),
(4, '绩效申诉流程说明', '各位员工：\n\n为保障绩效考核的公平公正，现将绩效申诉流程公示如下：\n\n1. 申诉时限：考核结果公布后5个工作日内\n2. 申诉途径：通过绩效考核系统在线提交\n3. 处理时限：收到申诉后3个工作日内完成初审\n4. 反馈方式：系统内通知\n\n如有疑问，请联系人力资源部。\n\n人力资源部\n2026年1月10日', 2, 1, NOW()),
(5, '系统维护通知', '全体用户：\n\n绩效考核系统将于2026年4月20日（周日）22:00-24:00进行系统升级维护，届时系统将暂时无法访问。\n\n请各位提前做好工作安排，避开维护时段。\n\n信息技术部\n2026年4月18日', 1, 1, NOW());

-- ========================================
-- 5. 绩效目标（公司级 → 部门级 → 个人级）
-- ========================================
-- 公司级目标
INSERT INTO `performance_goal` (`id`, `title`, `description`, `level`, `parent_id`, `department_id`, `user_id`, `year`, `status`, `create_time`) VALUES
(1, '2026年度经营目标：营业收入突破50亿元', '公司整体营收目标，各部门需按分解指标完成', 'COMPANY', 0, NULL, NULL, 2026, 1, NOW()),
(2, '2026年度合规与风控目标：零重大违规事件', '全年不发生重大监管处罚和合规风险事件', 'COMPANY', 0, NULL, NULL, 2026, 1, NOW()),
(3, '2026年度客户发展目标：新增优质客户200+', '着力发展高净值客户，提升客户资产规模', 'COMPANY', 0, NULL, NULL, 2026, 1, NOW());

-- 部门级目标（从公司级分解）
INSERT INTO `performance_goal` (`id`, `title`, `description`, `level`, `parent_id`, `department_id`, `user_id`, `year`, `status`, `create_time`) VALUES
(4, '风险管理部：全年风控评分达90分以上', '建立健全风险预警机制，确保风险控制综合评分达标', 'DEPARTMENT', 2, 2, NULL, 2026, 1, NOW()),
(5, '合规管理部：合规完成率达100%', '完成全部合规检查任务，确保无重大合规违规', 'DEPARTMENT', 2, 3, NULL, 2026, 1, NOW()),
(6, '财务管理部：年度营收完成率达105%', '超额完成公司下达的财务指标', 'DEPARTMENT', 1, 4, NULL, 2026, 1, NOW()),
(7, '客户服务部：客户满意度达88分', '提升客户服务质量，降低投诉率', 'DEPARTMENT', 3, 5, NULL, 2026, 1, NOW()),
(8, '投资银行部：项目回报率达12%以上', '提升项目质量，确保投资回报达标', 'DEPARTMENT', 1, 6, NULL, 2026, 1, NOW()),
(9, '信息技术部：系统可用率达99.9%', '保障生产系统稳定运行，提升技术支持效率', 'DEPARTMENT', 1, 8, NULL, 2026, 1, NOW());

-- 个人级目标（从部门级分解）
INSERT INTO `performance_goal` (`id`, `title`, `description`, `level`, `parent_id`, `department_id`, `user_id`, `year`, `status`, `create_time`) VALUES
(10, '杨帆：完成风险评估报告20份', '按季度完成风险评估，及时发出风险预警', 'PERSONAL', 4, 2, 10, 2026, 1, NOW()),
(11, '黄斌：风控系统优化方案编写', '完成风控系统升级需求分析和优化方案', 'PERSONAL', 4, 2, 11, 2026, 1, NOW()),
(12, '徐婷：季度财务报告准确率100%', '确保季度财务报表零差错', 'PERSONAL', 6, 4, 14, 2026, 1, NOW()),
(13, '朱琳：客户回访覆盖率达95%', '定期完成客户回访，收集反馈建议', 'PERSONAL', 7, 5, 16, 2026, 1, NOW());

-- ========================================
-- 6. 目标与KPI关联
-- ========================================
INSERT INTO `goal_kpi` (`goal_id`, `kpi_id`, `target_value`, `weight`, `create_time`) VALUES
(4, 2, 90.00, 50.00, NOW()),
(4, 10, 100.00, 30.00, NOW()),
(4, 11, 95.00, 20.00, NOW()),
(5, 1, 100.00, 40.00, NOW()),
(5, 10, 100.00, 40.00, NOW()),
(5, 11, 95.00, 20.00, NOW()),
(6, 5, 105.00, 40.00, NOW()),
(6, 6, 10.00, 30.00, NOW()),
(6, 4, 0.50, 30.00, NOW()),
(7, 7, 88.00, 50.00, NOW()),
(7, 8, 60.00, 30.00, NOW()),
(7, 3, 15.00, 20.00, NOW()),
(8, 12, 12.00, 50.00, NOW()),
(8, 5, 100.00, 30.00, NOW()),
(8, 3, 15.00, 20.00, NOW());

-- ========================================
-- 7. 考核方案（一个已完成 + 一个草稿）
-- ========================================
-- 已完成的方案
INSERT INTO `evaluation_plan` (`id`, `name`, `year`, `period_type`, `period_number`, `start_date`, `end_date`, `grade_a_min`, `grade_b_min`, `grade_c_min`, `self_weight`, `manager_weight`, `peer_weight`, `status`, `creator_id`, `create_time`) VALUES
(1, '2026年第一季度绩效考核', 2026, 'QUARTERLY', 1, '2026-01-01', '2026-03-31', 90.00, 75.00, 60.00, 20.00, 60.00, 20.00, 2, 1, NOW());

-- 草稿方案
INSERT INTO `evaluation_plan` (`id`, `name`, `year`, `period_type`, `period_number`, `start_date`, `end_date`, `grade_a_min`, `grade_b_min`, `grade_c_min`, `self_weight`, `manager_weight`, `peer_weight`, `status`, `creator_id`, `create_time`) VALUES
(3, '2026年年度绩效考核', 2026, 'ANNUALLY', NULL, '2026-01-01', '2026-12-31', 90.00, 75.00, 60.00, 15.00, 55.00, 30.00, 0, 1, NOW());

-- ========================================
-- 8. 评分规则
-- ========================================
-- 方案1（Q1已完成）的规则
INSERT INTO `evaluation_rule` (`id`, `plan_id`, `kpi_id`, `weight`, `target_value`, `scoring_formula`, `max_score`, `create_time`) VALUES
(1, 1, 1, 15.00, 100.00, 'WEIGHTED_SUM', 100.00, NOW()),
(2, 1, 2, 15.00, 90.00, 'WEIGHTED_SUM', 100.00, NOW()),
(3, 1, 5, 20.00, 100.00, 'WEIGHTED_SUM', 100.00, NOW()),
(4, 1, 7, 15.00, 85.00, 'WEIGHTED_SUM', 100.00, NOW()),
(5, 1, 4, 10.00, 0.50, 'WEIGHTED_SUM', 100.00, NOW()),
(6, 1, 9, 15.00, 100.00, 'WEIGHTED_SUM', 100.00, NOW()),
(7, 1, 11, 10.00, 95.00, 'WEIGHTED_SUM', 100.00, NOW());

-- ========================================
-- 9. 考核任务（方案1已完成的任务，含最终得分和等级）
-- ========================================
-- 方案1的已完成任务（每个非管理员员工一条）
INSERT INTO `evaluation_task` (`id`, `plan_id`, `user_id`, `department_id`, `self_score`, `manager_score`, `peer_score`, `final_score`, `grade`, `status`, `remark`, `create_time`) VALUES
-- HR专员
(1, 1, 2, 7, 88.00, 92.00, 85.00, 90.00, 'A', 3, '工作认真负责，绩效管理能力突出', NOW()),
(2, 1, 3, 7, 82.00, 85.00, 80.00, 83.40, 'B', 3, '表现良好，继续提升', NOW()),
-- 部门经理
(3, 1, 4, 2, 90.00, 95.00, 88.00, 92.60, 'A', 3, '风险管控能力极强，全年零重大风险事件', NOW()),
(4, 1, 5, 4, 85.00, 88.00, 82.00, 86.00, 'B', 3, '财务管理精细，报表质量高', NOW()),
(5, 1, 6, 5, 92.00, 93.00, 90.00, 92.20, 'A', 3, '客户满意度显著提升', NOW()),
(6, 1, 7, 6, 80.00, 82.00, 78.00, 80.40, 'B', 3, '投行项目推进有成效', NOW()),
(7, 1, 8, 3, 95.00, 96.00, 92.00, 95.00, 'A', 3, '合规工作表现卓越，保持零违规纪录', NOW()),
(8, 1, 9, 8, 78.00, 80.00, 76.00, 78.80, 'B', 3, '系统稳定性维护到位', NOW()),
-- 普通员工
(9, 1, 10, 2, 85.00, 90.00, 83.00, 87.60, 'B', 3, '风险评估报告质量高', NOW()),
(10, 1, 11, 2, 75.00, 72.00, 70.00, 72.00, 'C', 3, '需要加强风控分析能力', NOW()),
(11, 1, 12, 3, 88.00, 91.00, 86.00, 89.40, 'B', 3, '合规检查细致认真', NOW()),
(12, 1, 13, 3, 60.00, 58.00, 62.00, 59.20, 'D', 3, '合规知识掌握不足，需加强培训', NOW()),
(13, 1, 14, 4, 92.00, 94.00, 90.00, 92.80, 'A', 3, '财务报告准确率高，工作细致', NOW()),
(14, 1, 15, 4, 78.00, 75.00, 73.00, 75.00, 'B', 3, '基本完成任务，需提升效率', NOW()),
(15, 1, 16, 5, 90.00, 92.00, 88.00, 90.80, 'A', 3, '客户服务态度优秀，获多次表扬', NOW()),
(16, 1, 17, 5, 72.00, 68.00, 70.00, 69.20, 'C', 3, '客户沟通能力有待提高', NOW()),
(17, 1, 18, 6, 86.00, 88.00, 84.00, 86.80, 'B', 3, '项目分析能力强', NOW()),
(18, 1, 19, 6, 65.00, 62.00, 66.00, 63.40, 'C', 3, '需要提升投行业务专业水平', NOW()),
(19, 1, 20, 8, 91.00, 93.00, 89.00, 91.80, 'A', 3, '技术能力突出，系统优化效果显著', NOW()),
(20, 1, 21, 8, 80.00, 78.00, 76.00, 78.00, 'B', 3, '完成本职工作，技术能力需提升', NOW());

-- ========================================
-- 10. 评分明细（方案1部分评分数据）
-- ========================================
-- 任务1（张慧 HR）自评
INSERT INTO `evaluation_score` (`task_id`, `kpi_id`, `evaluator_id`, `evaluatee_id`, `score_type`, `score`, `comment`, `create_time`) VALUES
(1, 1, 2, 2, 'SELF', 90.00, '合规工作全部按时完成', NOW()),
(1, 2, 2, 2, 'SELF', 85.00, '风控意识较强', NOW()),
(1, 5, 2, 2, 'SELF', 88.00, '营收指标达成良好', NOW()),
(1, 7, 2, 2, 'SELF', 90.00, '内部满意度高', NOW()),
-- 任务1 上级评
(1, 1, 1, 2, 'MANAGER', 93.00, '合规工作表现优秀', NOW()),
(1, 2, 1, 2, 'MANAGER', 90.00, '风控配合到位', NOW()),
(1, 5, 1, 2, 'MANAGER', 92.00, '超额完成营收任务', NOW()),
(1, 7, 1, 2, 'MANAGER', 93.00, '服务意识强', NOW()),
-- 任务1 同事互评
(1, 1, 3, 2, 'PEER', 86.00, '合规培训组织得力', NOW()),
(1, 2, 3, 2, 'PEER', 84.00, '风控配合良好', NOW()),
(1, 5, 3, 2, 'PEER', 85.00, NULL, NOW()),
(1, 7, 3, 2, 'PEER', 86.00, '同事关系融洽', NOW()),
-- 任务3（王强 风险经理）评分
(3, 1, 4, 4, 'SELF', 92.00, '合规任务全部完成', NOW()),
(3, 2, 4, 4, 'SELF', 90.00, '风控体系持续优化', NOW()),
(3, 5, 4, 4, 'SELF', 88.00, '部门营收达标', NOW()),
(3, 1, 1, 4, 'MANAGER', 96.00, '风险管控能力卓越', NOW()),
(3, 2, 1, 4, 'MANAGER', 95.00, '风控零事故', NOW()),
(3, 5, 1, 4, 'MANAGER', 94.00, '超额完成', NOW()),
(3, 1, 10, 4, 'PEER', 88.00, '领导力强', NOW()),
(3, 2, 10, 4, 'PEER', 89.00, '专业能力突出', NOW()),
(3, 5, 10, 4, 'PEER', 87.00, NULL, NOW()),
-- 任务13（徐婷 财务）评分
(13, 1, 14, 14, 'SELF', 93.00, '合规任务完成率100%', NOW()),
(13, 5, 14, 14, 'SELF', 92.00, '财务报告准确率高', NOW()),
(13, 4, 14, 14, 'SELF', 91.00, '零差错', NOW()),
(13, 1, 5, 14, 'MANAGER', 95.00, '财务合规工作扎实', NOW()),
(13, 5, 5, 14, 'MANAGER', 94.00, '超额完成财务指标', NOW()),
(13, 4, 5, 14, 'MANAGER', 93.00, '全年零差错，值得表彰', NOW()),
(13, 1, 15, 14, 'PEER', 90.00, '工作认真细致', NOW()),
(13, 5, 15, 14, 'PEER', 90.00, NULL, NOW()),
(13, 4, 15, 14, 'PEER', 91.00, '业务精通', NOW());

-- ========================================
-- 10.5 互评分配（方案1）
-- ========================================
INSERT INTO `peer_assignment` (`plan_id`, `task_id`, `evaluator_id`, `evaluatee_id`, `status`, `create_time`) VALUES
-- 风险管理部（user 4=王强, 10=杨帆, 11=黄斌）
(1, 3, 10, 4, 1, NOW()), (1, 3, 11, 4, 1, NOW()),
(1, 9, 4, 10, 1, NOW()), (1, 9, 11, 10, 1, NOW()),
(1, 10, 4, 11, 1, NOW()), (1, 10, 10, 11, 1, NOW()),
-- 合规管理部（user 8=孙丽, 12=吴静, 13=郑浩）
(1, 7, 12, 8, 1, NOW()), (1, 7, 13, 8, 1, NOW()),
(1, 11, 8, 12, 1, NOW()), (1, 11, 13, 12, 1, NOW()),
(1, 12, 8, 13, 1, NOW()), (1, 12, 12, 13, 1, NOW()),
-- 财务管理部（user 5=李娜, 14=徐婷, 15=马飞）
(1, 4, 14, 5, 1, NOW()), (1, 4, 15, 5, 1, NOW()),
(1, 13, 5, 14, 1, NOW()), (1, 13, 15, 14, 1, NOW()),
(1, 14, 5, 15, 1, NOW()), (1, 14, 14, 15, 1, NOW()),
-- 客户服务部（user 6=陈伟, 16=朱琳, 17=何涛）
(1, 5, 16, 6, 1, NOW()), (1, 5, 17, 6, 1, NOW()),
(1, 15, 6, 16, 1, NOW()), (1, 15, 17, 16, 1, NOW()),
(1, 16, 6, 17, 1, NOW()), (1, 16, 16, 17, 1, NOW()),
-- 投资银行部（user 7=赵磊, 18=罗峰, 19=高雪）
(1, 6, 18, 7, 1, NOW()), (1, 6, 19, 7, 1, NOW()),
(1, 17, 7, 18, 1, NOW()), (1, 17, 19, 18, 1, NOW()),
(1, 18, 7, 19, 1, NOW()), (1, 18, 18, 19, 1, NOW()),
-- 信息技术部（user 9=周明, 20=唐杰, 21=蒋文）
(1, 8, 20, 9, 1, NOW()), (1, 8, 21, 9, 1, NOW()),
(1, 19, 9, 20, 1, NOW()), (1, 19, 21, 20, 1, NOW()),
(1, 20, 9, 21, 1, NOW()), (1, 20, 20, 21, 1, NOW()),
-- 人力资源部（user 2=张慧, 3=刘敏）
(1, 1, 3, 2, 1, NOW()),
(1, 2, 2, 3, 1, NOW());

-- ========================================
-- 11. 薪酬调整（方案1的结果）
-- ========================================
INSERT INTO `salary_adjustment` (`task_id`, `user_id`, `plan_id`, `grade`, `final_score`, `base_salary`, `adjustment_rate`, `bonus_amount`, `suggestion`, `status`, `create_time`) VALUES
(1, 2, 1, 'A', 90.00, 'ENC:SolOrFe1SNd8N2cX//riqQ==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:ELUMYOenUhTSrjU03YND7g==', '绩效优秀，建议大幅调薪并发放年终奖金', 1, NOW()),
(2, 3, 1, 'B', 83.40, 'ENC:6VRI6ku6rohJ8q4pLZG+Jg==', 'ENC:93eYseSrYqMYnQjPoIeLeQ==', 'ENC:i1V6XEvRWagzjw8PRy/uwg==', '绩效良好，建议适度调薪并发放奖金', 1, NOW()),
(3, 4, 1, 'A', 92.60, 'ENC:kM2PyhVprPXtDoy2h0oWbw==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:qA5ZWippnPBTOt8Tc2Tq7w==', '绩效优秀，建议大幅调薪并发放年终奖金', 1, NOW()),
(5, 6, 1, 'A', 92.20, 'ENC:kM2PyhVprPXtDoy2h0oWbw==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:/XSUTv97lRttS519HmFXcQ==', '绩效优秀，客户满意度提升显著', 1, NOW()),
(7, 8, 1, 'A', 95.00, 'ENC:12y2b3+twVqksudVrkhGrQ==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:FK6XmXK9P5z0/lB5S+Nrhg==', '合规工作卓越，建议晋级', 0, NOW()),
(10, 11, 1, 'C', 72.00, 'ENC:Ju/xClXuYm9AZriwGAwRig==', 'ENC:RuiUNNEDFpAmsnDwrxHYIQ==', 'ENC:Bf//X51HOu6JMjWw6/rfPQ==', '绩效合格，建议小幅调薪', 0, NOW()),
(12, 13, 1, 'D', 59.20, 'ENC:NBepE7Ti85Oa1r6oHLuOIA==', 'ENC:lhIZdwS8cuhjCwjypYgacA==', 'ENC:lhIZdwS8cuhjCwjypYgacA==', '绩效不合格，建议维持薪资并制定改进计划', 2, NOW()),
(13, 14, 1, 'A', 92.80, 'ENC:6WZfn57WoSXfB4PrFb0HbA==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:Bf//X51HOu6JMjWw6/rfPQ==', '财务工作出色，建议大幅调薪', 1, NOW()),
(19, 20, 1, 'A', 91.80, 'ENC:6VRI6ku6rohJ8q4pLZG+Jg==', 'ENC:Q5wwPTMCMwcsWvd3PLJkTQ==', 'ENC:ELUMYOenUhTSrjU03YND7g==', '技术能力突出，建议大幅调薪', 0, NOW());

-- ========================================
-- 12. 个人发展计划（IDP）
-- ========================================
INSERT INTO `development_plan` (`task_id`, `user_id`, `plan_id`, `strengths`, `weaknesses`, `training_suggestion`, `development_goal`, `action_plan`, `status`, `create_time`) VALUES
(1, 2, 1, '绩效管理经验丰富，HR专业能力强', NULL, '建议参加高级人力资源管理师认证培训', '向HR总监方向发展', '1.完成高级HR认证 2.主导绩效体系优化项目', 1, NOW()),
(3, 4, 1, '风险管控能力极强，全年零重大风险事件', NULL, '建议参加高级管理培训、行业峰会，培养领导力', '向管理岗位或专家岗位发展', '1.参加CRO高级研修班 2.编写风控方法论手册', 2, NOW()),
(10, 11, 1, '基本能完成工作任务', '风控分析能力不足，报告质量有待提高', '建议参加风险管理专题培训，加强分析方法学习', '三个月内风控分析能力达到部门平均水平', '1.每周学习一篇风控案例 2.参加FRM考试备考 3.由王强经理一对一指导', 2, NOW()),
(12, 13, 1, NULL, '合规知识掌握不足，工作细致程度不够', '建议安排系统性合规培训，指定导师一对一辅导', '三个月内各项合规考核指标达到合格标准', '1.每周完成合规知识测试 2.每月一次辅导面谈 3.参加反洗钱专题培训', 2, NOW()),
(13, 14, 1, '财务报告准确率高，工作细致认真', NULL, '建议参加高级财务分析培训，提升战略财务能力', '向高级财务分析师方向发展', '1.完成CFA二级备考 2.参与公司财务战略规划项目', 1, NOW()),
(19, 20, 1, '技术能力突出，系统优化效果显著', NULL, '建议参加架构师培训，拓展技术视野', '向技术架构师方向发展', '1.完成微服务架构设计课程 2.主导下一代系统架构设计', 1, NOW());

-- ========================================
-- 13. 绩效申诉
-- ========================================
INSERT INTO `appeal` (`task_id`, `user_id`, `reason`, `status`, `reply`, `handler_id`, `handle_time`, `create_time`) VALUES
(12, 13, '对合规知识考核评分有异议，认为部分扣分项不合理。本人已参加相关培训并取得合格证书，希望重新评估合规完成率得分。', 2, '经核实，该员工确已完成部分合规培训，但日常合规操作仍有多项扣分记录。维持原评分，建议继续加强日常合规操作。', 2, NOW(), NOW()),
(16, 17, '认为客户满意度评分过低，部分低分评价为非责任范围内的投诉，申请剔除相关评分后重新计算。', 1, NULL, NULL, NULL, NOW()),
(18, 19, '对项目回报率评分有异议，所负责项目的回报计算周期应延至下季度方可体现真实收益。', 0, NULL, NULL, NULL, NOW());

-- ========================================
-- 14. 操作日志（示例数据）
-- ========================================
INSERT INTO `sys_operation_log` (`user_id`, `username`, `operation`, `method`, `params`, `ip`, `result`, `create_time`) VALUES
(1, 'admin', '用户登录', 'POST /api/auth/login', '{"username":"admin"}', '192.168.1.100', 1, NOW()),
(1, 'admin', '新增考核方案', 'POST /api/plan', '{"name":"2026年第一季度绩效考核"}', '192.168.1.100', 1, NOW()),
(1, 'admin', '启动考核方案', 'POST /api/plan/start/1', '{}', '192.168.1.100', 1, NOW()),
(2, 'hr_zhang', '用户登录', 'POST /api/auth/login', '{"username":"hr_zhang"}', '192.168.1.101', 1, NOW()),
(2, 'hr_zhang', '发布公告', 'POST /api/announcement', '{"title":"员工培训计划公示"}', '192.168.1.101', 1, NOW()),
(4, 'mgr_wang', '用户登录', 'POST /api/auth/login', '{"username":"mgr_wang"}', '192.168.1.102', 1, NOW()),
(4, 'mgr_wang', '提交考核评分', 'POST /api/task/score', '{"taskId":9,"scoreType":"MANAGER"}', '192.168.1.102', 1, NOW()),
(10, 'emp_yang', '用户登录', 'POST /api/auth/login', '{"username":"emp_yang"}', '192.168.1.103', 1, NOW()),
(10, 'emp_yang', '提交考核评分', 'POST /api/task/score', '{"taskId":9,"scoreType":"SELF"}', '192.168.1.103', 1, NOW()),
(1, 'admin', '完成考核方案', 'POST /api/plan/complete/1', '{}', '192.168.1.100', 1, NOW()),
(1, 'admin', '导出考核结果Excel', 'GET /api/report/exportExcel/1', '{}', '192.168.1.100', 1, NOW()),
(2, 'hr_zhang', '生成薪酬调整建议', 'POST /api/salary/generate/1', '{}', '192.168.1.101', 1, NOW()),
(2, 'hr_zhang', '审批薪酬调整', 'PUT /api/salary/approve/1/1', '{}', '192.168.1.101', 1, NOW()),
(13, 'emp_zheng', '提交绩效申诉', 'POST /api/appeal', '{"taskId":12}', '192.168.1.104', 1, NOW()),
(2, 'hr_zhang', '处理绩效申诉', 'PUT /api/appeal/handle/1', '{"status":2}', '192.168.1.101', 1, NOW());
