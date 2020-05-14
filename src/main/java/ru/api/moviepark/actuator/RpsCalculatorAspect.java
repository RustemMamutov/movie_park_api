package ru.api.moviepark.actuator;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class RpsCalculatorAspect {

    @Pointcut("execution(* ru.api.moviepark.web.controller.ApiRestController.*(..))")
    public void controllerMethod() {
    }

    @Before("controllerMethod() || @annotation(IncrRps)")
    public void incrRps(JoinPoint thisJoinPoint) {
        log.debug("incr RPS before execute {}", thisJoinPoint.getSignature().getName());
        RpsCalculatorUtil.incrRps();
    }
}
