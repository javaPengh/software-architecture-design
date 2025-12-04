package org.zzu.utils;// LoggingAspect.java
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    // 移除了未使用的ObjectMapper

    // 定义切点：拦截所有Controller包下的公共方法
    @Pointcut("execution(public * org.zzu.controller..*Controller.*(..))")
    public void controllerMethods() {}

    // 环绕通知
    @Around("controllerMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }

        // 记录入参
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (log.isInfoEnabled()) {
            try {
                log.info("REQUEST  => Method: {}, URI: {}, Params: {}",
                        methodName,
                        request != null ? request.getRequestURI() : "N/A",
                        Arrays.toString(args));
            } catch (Exception e) {
                log.warn("Failed to log request params: ", e);
            }
        }

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            // 执行目标方法
            result = joinPoint.proceed();

            // 记录出参
            long endTime = System.currentTimeMillis();
            if (log.isInfoEnabled()) {
                try {
                    // 直接使用result，因为proceed()方法一定会返回对象（即使为void也会包装）
                    log.info("RESPONSE => Method: {}, Execution Time: {} ms, Result: {}",
                            methodName,
                            (endTime - startTime),
                            result);
                } catch (Exception e) {
                    log.warn("Failed to log response result: ", e);
                }
            }

            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("EXCEPTION => Method: {}, Execution Time: {} ms, Exception: {}",
                    methodName,
                    (endTime - startTime),
                    e.getMessage());
            throw e;
        }
    }
}
