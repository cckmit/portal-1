package ru.protei.portal.core.service.edu;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static ru.protei.portal.core.aspect.ServiceLayerInterceptorLogging.SERVICE_FACADE_LOGGER_NAME;

@Component
@Aspect
public class BookServiceInterceptor {


    @Pointcut("execution(* ru.protei.portal.core.service.edu.BookService.*(..))")
    private void serviceMethods() {
    }

    @Around(value = "ru.protei.portal.core.service.edu.BookServiceInterceptor.serviceMethods()")
    public Object checkTimes(ProceedingJoinPoint pjp) throws Throwable {
        long before = System.currentTimeMillis();
        Object obj = pjp.proceed();
        long after = System.currentTimeMillis();
        logger.debug(after - before + " ms");
        return obj;
    }

    private static Logger logger = LoggerFactory.getLogger(SERVICE_FACADE_LOGGER_NAME);
}