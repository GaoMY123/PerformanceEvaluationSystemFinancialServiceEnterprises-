package com.performance.aspect;

import cn.hutool.json.JSONUtil;
import com.performance.annotation.OperationLog;
import com.performance.entity.SysOperationLog;
import com.performance.mapper.SysOperationLogMapper;
import com.performance.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志切面 - 自动记录标注了@OperationLog注解的方法调用
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private SysOperationLogMapper operationLogMapper;

    @Around("@annotation(com.performance.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        SysOperationLog operationLog = new SysOperationLog();
        try {
            // 获取注解信息
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            OperationLog annotation = method.getAnnotation(OperationLog.class);
            operationLog.setOperation(annotation.value());
            operationLog.setMethod(point.getTarget().getClass().getName() + "." + method.getName());

            // 获取请求参数（截取前2000字符防止过长）
            String params = JSONUtil.toJsonStr(point.getArgs());
            operationLog.setParams(params.length() > 2000 ? params.substring(0, 2000) : params);

            // 获取当前用户信息
            Long userId = SecurityUtils.getCurrentUserId();
            String username = SecurityUtils.getCurrentUsername();
            operationLog.setUserId(userId);
            operationLog.setUsername(username);

            // 获取IP地址
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setIp(request.getRemoteAddr());
            }

            // 执行目标方法
            Object result = point.proceed();
            operationLog.setResult(1);
            return result;
        } catch (Exception e) {
            operationLog.setResult(0);
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 异步保存日志
            try {
                operationLogMapper.insert(operationLog);
            } catch (Exception e) {
                log.error("保存操作日志失败：{}", e.getMessage());
            }
        }
    }
}
