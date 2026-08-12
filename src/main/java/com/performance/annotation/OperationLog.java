package com.performance.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解 - 标注在需要记录操作日志的方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /** 操作描述 */
    String value() default "";
}
