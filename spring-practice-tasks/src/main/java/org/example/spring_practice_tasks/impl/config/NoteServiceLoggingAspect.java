package org.example.spring_practice_tasks.impl.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.spring_practice_tasks.impl.config.request_id.RequestIdHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class NoteServiceLoggingAspect {

    private final RequestIdHolder requestIdHolder;

    @Around("""
            execution(* org.example.spring_practice_tasks.api.service.NoteService.create(..)) || 
            execution(* org.example.spring_practice_tasks.api.service.NoteService.createBatch(..)) ||
            execution(* org.example.spring_practice_tasks.api.service.NoteService.update(..))""")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            String methodName = joinPoint.getSignature().getName();
            String requestId = requestIdHolder.getRequestId();
            log.info("NoteService.{} took {} ms, requestId={}", methodName, duration, requestId != null ? requestId : "N/A");
        }
    }
}
