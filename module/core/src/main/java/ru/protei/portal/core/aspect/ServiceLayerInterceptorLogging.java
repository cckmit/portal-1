package ru.protei.portal.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import ru.protei.portal.core.model.struct.MethodProfile;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Журналирование
 */
@Aspect
@Order(0)//Оборачивает прочие - первым вызывается, последним отдает результат.
public class ServiceLayerInterceptorLogging {

    @Pointcut("execution(public ru.protei.portal.api.struct.Result *(..))")
    private void methodWithResult() {}

    @Pointcut("within(ru.protei.portal.core.service.*)")
    private void inServiceFacade() {}

    @Pointcut("within(ru.protei.portal.core.service.auth.*)")
    public void authServiceMethod() {
    }

    @Around("methodWithResult() && authServiceMethod()")
    public Object authorizeServiceLogging(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        log.info("calling : {} : <secure>", methodName);

        return invokeMethod( methodName, pjp);
    }

    @Around("inServiceFacade()")
    public Object serviceFacadeLogging(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        log.info("calling : {} args: {}", methodName, pjp.getArgs());

        return invokeMethod( methodName, pjp);
    }

    private Object invokeMethod( final String methodName, ProceedingJoinPoint pjp )throws Throwable {
        String threadName = Thread.currentThread().getName();
        Thread.currentThread().setName("T-" + Thread.currentThread().getId() + " " + methodName);

        Object result = null;
        long currentTimeMillis = System.currentTimeMillis();

        try {

            result = pjp.proceed();

        } finally {
            Long executionTime = System.currentTimeMillis() - currentTimeMillis;

            MethodProfile profile = profiling.get(methodName);
            if (profile == null) {
                profile = new MethodProfile();
                profiling.put(methodName, profile);
            }
            profile.updateTime(executionTime);
            log.info("result : {} : count={} : time={} : averageT={} : maxT={} : minT={} : Result [{}] ",
                    methodName, profile.invokeCount, executionTime, profile.average, profile.maxTime, profile.minTime,
                    makeResultAsString(result));

            Thread.currentThread().setName(threadName);
        }

        return result;
    }

    private String makeResultAsString( Object result ) {
        if ( result == null ) {
            return "Result is null.";
        }

        if (result instanceof Result) {
            Result resultObject = ((Result) result);
            return resultObject.getStatus() + " "
                    + (resultObject.getMessage() == null ? "" : "- " + resultObject.getMessage() + ". ")
                    + makeStringFromObject( resultObject.getData() );
        }

        return makeStringFromObject( result );
    }

    private String makeStringFromObject( Object resultObject ) {
        if ( resultObject == null ) {
            return "ResultObject is null.";
        }

        if ( resultObject instanceof Long) {
            log.trace( "ResultObject: {}", resultObject );
            return String.valueOf( resultObject );
        }

        if ( resultObject instanceof Integer) {
            log.trace( "ResultObject: {}", resultObject );
            return String.valueOf( resultObject );
        }

        if ( resultObject instanceof Boolean) {
            log.trace( "ResultObject: {}", resultObject );
            return String.valueOf( resultObject );
        }

        if ( resultObject instanceof Collection<?>) {
            log.trace( "ResultObject: {}", resultObject );
            return "collection size=" + ((Collection) resultObject).size();
        }

        if ( resultObject instanceof Set<?>) {
            log.trace( "ResultObject: {}", resultObject );
            return "set size=" + ((Set) resultObject).size();
        }

        if ( resultObject instanceof Map<?, ?>) {
            log.trace( "ResultObject: {}", resultObject );
            return "map size=" + ((Map) resultObject).size();
        }

        if ( resultObject instanceof SearchResult) {
            log.trace( "ResultObject: {}", ((SearchResult) resultObject).getResults() );
            return "SearchResult total=" + ((SearchResult) resultObject).getTotalCount();
        }

        if ( resultObject instanceof AuditableObject) {
            log.trace( "ResultObject: {}", resultObject );
            return resultObject.getClass().getSimpleName()+ " id=" + ((AuditableObject) resultObject).getId();
        }

        log.trace( "ResultObject: {}", resultObject );
        return resultObject.getClass().getSimpleName();
    }


    Map<String, MethodProfile> profiling = new ConcurrentHashMap<>();
    public static final String SERVICE_FACADE_LOGGER_NAME = "service";
    private static final Logger log = getLogger( SERVICE_FACADE_LOGGER_NAME );

}

