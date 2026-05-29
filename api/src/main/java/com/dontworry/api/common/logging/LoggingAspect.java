package com.dontworry.api.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;

import java.lang.annotation.Annotation;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    // Controller: Start/End 로그
    @Around("execution(* com.dontworry.api.domain..controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "_" + methodName;

        Object[] maskedArgs = maskCookieArgs(joinPoint);

        log.info("[{}] Start, Arguments = {}", fullMethodName, maskedArgs);
        long startTime = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("[{}] Error: {}", fullMethodName, e.getMessage());
            throw e;
        }

        long endTime = System.currentTimeMillis();
        log.info("[{}] End, result = {}, Execution Time: {}ms", fullMethodName, result,
                endTime - startTime);

        return result;
    }

    // Service: 성공/실패 로그
    @Around("execution(* com.dontworry.api.domain..service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "_" + methodName;

        long startTime = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("[{}] Error: {}", fullMethodName, e.getMessage());

            throw e;
        }

        long endTime = System.currentTimeMillis();
        log.info("[{}] Success, result = {} ,Execution Time: {}ms", fullMethodName, result,
                endTime - startTime);

        return result;
    }

    private Object[] maskCookieArgs(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        Object[] masked = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            boolean isCookie = false;

            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof CookieValue) {
                    isCookie = true;
                    break;
                }
            }

            masked[i] = isCookie ? "[COOKIE_MASKED]" : args[i];
        }

        return masked;
    }

}
