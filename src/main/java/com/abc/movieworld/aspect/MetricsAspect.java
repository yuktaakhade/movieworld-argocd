package com.abc.movieworld.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Aspect for collecting custom metrics on service methods.
 * This demonstrates how to add custom metrics to specific business operations.
 */
@Aspect
@Component
@Slf4j
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    public MetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Measures execution time for all service methods and records it as a timer metric.
     * Also increments a counter for each method invocation.
     *
     * @param joinPoint the join point for the intercepted method
     * @return the result of the method execution
     * @throws Throwable if the method execution throws an exception
     */
    @Around("execution(* com.abc.movieworld.service.*.*(..))")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String metricName = "service." + className + "." + methodName;
        
        // Increment counter for method invocation
        meterRegistry.counter(metricName + ".invocations").increment();
        
        // Record method execution time
        long startTime = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long duration = System.nanoTime() - startTime;
            
            // Record successful execution time
            Timer.builder(metricName + ".duration")
                .description("Execution time of " + className + "." + methodName)
                .tag("outcome", "success")
                .register(meterRegistry)
                .record(duration, TimeUnit.NANOSECONDS);
                
            log.debug("Method {} executed in {} ns", metricName, duration);
            return result;
        } catch (Throwable throwable) {
            long duration = System.nanoTime() - startTime;
            
            // Record failed execution time
            Timer.builder(metricName + ".duration")
                .description("Execution time of " + className + "." + methodName)
                .tag("outcome", "error")
                .tag("exception", throwable.getClass().getSimpleName())
                .register(meterRegistry)
                .record(duration, TimeUnit.NANOSECONDS);
                
            // Increment error counter
            meterRegistry.counter(metricName + ".errors", 
                "exception", throwable.getClass().getSimpleName())
                .increment();
                
            log.error("Method {} failed after {} ns with exception: {}", 
                metricName, duration, throwable.getMessage());
            throw throwable;
        }
    }
}
