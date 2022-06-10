package ru.protei.portal.core.service.edu;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class BookServiceInterceptor {


    @Pointcut("execution(* ru.protei.portal.core.service.edu.BookService.*(..))")
    private void serviceMethods() {
    }

    @Around(value = "serviceMethods()")
    public Object checkTimes(ProceedingJoinPoint pjp) throws Throwable {
        long before = System.currentTimeMillis();
        Object obj = pjp.proceed();
        long after = System.currentTimeMillis();
        long executionTime = after - before;
        logger.debug("BookServiceInterceptor method: {}, time (ms) : {}", pjp.getSignature().toShortString(),executionTime);
        return obj;
    }

    private static final Logger logger = LoggerFactory.getLogger(BookServiceInterceptor.class);
}