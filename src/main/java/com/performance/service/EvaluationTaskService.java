package com.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.performance.dto.EvaluationScoreDTO;
import com.performance.entity.EvaluationAttachment;
import com.performance.entity.EvaluationScore;
import com.performance.entity.EvaluationTask;

import java.util.List;

/**
 * 考核任务服务接口
 */
public interface EvaluationTaskService extends IService<EvaluationTask> {

    /** 分页查询考核任务（管理视角） */
    Page<EvaluationTask> pageList(Integer pageNum, Integer pageSize, Long planId, Long departmentId, Integer status, String keyword);

    /** 查询当前用户的考核任务 */
    Page<EvaluationTask> myTasks(Integer pageNum, Integer pageSize, Long planId);

    /** 查询需要当前经理评价的下属任务 */
    Page<EvaluationTask> managerTasks(Integer pageNum, Integer pageSize, Long planId);

    /** 查询需要当前用户互评的同事任务 */
    Page<EvaluationTask> peerTasks(Integer pageNum, Integer pageSize, Long planId);

    /** 提交评分 */
    void submitScore(EvaluationScoreDTO dto);

    /** 获取某任务的评分明细 */
    List<EvaluationScore> getScores(Long taskId, String scoreType);

    /** 上传考核附件 */
    void uploadAttachment(EvaluationAttachment attachment);

    /** 获取任务的附件列表 */
    List<EvaluationAttachment> getAttachments(Long taskId);

    /** 删除附件 */
    void deleteAttachment(Long id);
}
