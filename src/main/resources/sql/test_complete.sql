-- 测试数据：已完成考核方案（验证自动计算最终得分和等级）
-- 权重：自评20% 上级评60% 互评20%，等级：A≥90 B≥75 C≥60
-- 预期：张慧→A(93.4) 杨帆→B(78.0) 徐婷→C(63.8) 蒋文→D(49.2)
USE `performance_db`;

DELETE FROM `evaluation_score` WHERE `task_id` BETWEEN 101 AND 104;
DELETE FROM `peer_assignment` WHERE `plan_id` = 5;
DELETE FROM `evaluation_task` WHERE `plan_id` = 5;
DELETE FROM `evaluation_rule` WHERE `plan_id` = 5;
DELETE FROM `evaluation_plan` WHERE `id` = 5;

INSERT INTO `evaluation_plan` (`id`,`name`,`year`,`period_type`,`period_number`,`start_date`,`end_date`,`grade_a_min`,`grade_b_min`,`grade_c_min`,`self_weight`,`manager_weight`,`peer_weight`,`status`,`creator_id`,`create_time`) VALUES
(5,'2026年测试考核（自动计算验证）',2026,'QUARTERLY',3,'2026-07-01','2026-09-30',90.00,75.00,60.00,20.00,60.00,20.00,1,1,NOW());

INSERT INTO `evaluation_rule` (`id`,`plan_id`,`kpi_id`,`weight`,`target_value`,`scoring_formula`,`max_score`,`create_time`) VALUES
(20,5,1,40.00,100.00,'WEIGHTED_SUM',100.00,NOW()),
(21,5,5,35.00,100.00,'WEIGHTED_SUM',100.00,NOW()),
(22,5,7,25.00,85.00,'WEIGHTED_SUM',100.00,NOW());

INSERT INTO `evaluation_task` (`id`,`plan_id`,`user_id`,`department_id`,`status`,`create_time`) VALUES
(101,5,2,7,3,NOW()),(102,5,10,2,3,NOW()),(103,5,14,4,3,NOW()),(104,5,21,8,3,NOW());

-- 张慧(task101) 自评→92 上级评→95.1 互评→90.2 → 0.2*92+0.6*95.1+0.2*90.2≈93.4→A
INSERT INTO `evaluation_score` (`task_id`,`kpi_id`,`evaluator_id`,`evaluatee_id`,`score_type`,`score`,`comment`,`create_time`) VALUES
(101,1,2,2,'SELF',95.00,'合规完成',NOW()),(101,5,2,2,'SELF',90.00,'营收达标',NOW()),(101,7,2,2,'SELF',90.00,'满意度高',NOW()),
(101,1,1,2,'MANAGER',98.00,'合规优秀',NOW()),(101,5,1,2,'MANAGER',94.00,'超额完成',NOW()),(101,7,1,2,'MANAGER',92.00,'服务好',NOW()),
(101,1,3,2,'PEER',92.00,NULL,NOW()),(101,5,3,2,'PEER',88.00,NULL,NOW()),(101,7,3,2,'PEER',90.00,NULL,NOW());

-- 杨帆(task102) 自评→80.3 上级评→78.05 互评→76.3 → 0.2*80.3+0.6*78.05+0.2*76.3≈78.2→B
INSERT INTO `evaluation_score` (`task_id`,`kpi_id`,`evaluator_id`,`evaluatee_id`,`score_type`,`score`,`comment`,`create_time`) VALUES
(102,1,10,10,'SELF',82.00,'基本完成',NOW()),(102,5,10,10,'SELF',80.00,'一般',NOW()),(102,7,10,10,'SELF',78.00,'尚可',NOW()),
(102,1,4,10,'MANAGER',80.00,'合格',NOW()),(102,5,4,10,'MANAGER',78.00,'达标',NOW()),(102,7,4,10,'MANAGER',75.00,'需提升',NOW()),
(102,1,11,10,'PEER',78.00,NULL,NOW()),(102,5,11,10,'PEER',74.00,NULL,NOW()),(102,7,11,10,'PEER',76.00,NULL,NOW());

-- 徐婷(task103) 自评→65.1 上级评→62.0 互评→68.3 → 0.2*65.1+0.6*62.0+0.2*68.3≈63.8→C
INSERT INTO `evaluation_score` (`task_id`,`kpi_id`,`evaluator_id`,`evaluatee_id`,`score_type`,`score`,`comment`,`create_time`) VALUES
(103,1,14,14,'SELF',68.00,'有欠缺',NOW()),(103,5,14,14,'SELF',64.00,'未达标',NOW()),(103,7,14,14,'SELF',62.00,'低',NOW()),
(103,1,5,14,'MANAGER',65.00,'需改进',NOW()),(103,5,5,14,'MANAGER',60.00,'未达标',NOW()),(103,7,5,14,'MANAGER',60.00,'待提升',NOW()),
(103,1,15,14,'PEER',70.00,NULL,NOW()),(103,5,15,14,'PEER',66.00,NULL,NOW()),(103,7,15,14,'PEER',68.00,NULL,NOW());

-- 蒋文(task104) 自评→50.1 上级评→48.1 互评→52.1 → 0.2*50.1+0.6*48.1+0.2*52.1≈49.3→D
INSERT INTO `evaluation_score` (`task_id`,`kpi_id`,`evaluator_id`,`evaluatee_id`,`score_type`,`score`,`comment`,`create_time`) VALUES
(104,1,21,21,'SELF',52.00,'差',NOW()),(104,5,21,21,'SELF',48.00,'差',NOW()),(104,7,21,21,'SELF',50.00,'差',NOW()),
(104,1,9,21,'MANAGER',50.00,'不合格',NOW()),(104,5,9,21,'MANAGER',46.00,'差',NOW()),(104,7,9,21,'MANAGER',48.00,'差',NOW()),
(104,1,20,21,'PEER',54.00,NULL,NOW()),(104,5,20,21,'PEER',50.00,NULL,NOW()),(104,7,20,21,'PEER',52.00,NULL,NOW());

-- 互评分配（已完成）
INSERT INTO `peer_assignment` (`plan_id`,`task_id`,`evaluatee_id`,`evaluator_id`,`status`,`create_time`) VALUES
(5,101,2,3,1,NOW()),(5,102,10,11,1,NOW()),(5,103,14,15,1,NOW()),(5,104,21,20,1,NOW());
