package ru.api.moviepark.actuator;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class RpsCalculatorAspect {

    @Pointcut("execution(* ru.api.moviepark.controller.MovieParkController.*(..))")
    public void controllerMethod(){ }

    @Pointcut("@annotation(ru.api.moviepark.actuator.IncrRps)")
    public void incrRpsMethod() { }

    @Around("controllerMethod() || incrRpsMethod()")
    public Object incrRps(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        log.debug("incr RPS before execute {}", thisJoinPoint.getSignature().getName());
        RpsCalculatorUtil.incrRps();
        return thisJoinPoint.proceed();
    }
}
