package com.e104.realtime.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class ControllerLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ControllerLogAspect.class);

    @Pointcut("within(com.e104.realtime.**.*Controller)")
    void controllers() {}

    @Before("controllers")
    public void loggingControllers(JoinPoint joinPoint) {
        log.info("ENTER :: {}", joinPoint.getSignature().getName());
        log.info("PARAMS :: {}", Arrays.toString(joinPoint.getArgs()));
    }

}
