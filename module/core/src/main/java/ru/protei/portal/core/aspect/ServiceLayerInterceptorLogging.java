package ru.protei.portal.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import java.io.Serializable;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.protei.portal.api.struct.Result.error;

/**
 * Журналирование
 */
@Aspect
@Order(0)
public class ServiceLayerInterceptorLogging {

    private static final Logger log = getLogger("service");

    HashMap<String, MethodProfile> profiling = new HashMap<>();

    @Pointcut("within(ru.protei.portal.core.service.*)")
    private void inServiceLayer() {}

    @Pointcut("within(ru.protei.portal.core.service.user.*)")
    public void secureServiceMethod() {
    }

    @Around("inServiceLayer()")
    public Object serviceMethodLogging(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        log.debug("calling : {} args: {}", methodName, pjp.getArgs());

        return invokeMethod( methodName, pjp);
    }

    @Around("secureServiceMethod()")
    public Object authorizeSecureProcess(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        log.debug("calling : {} : <secure>", methodName);

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
            log.debug("result  : {} : count={} : time={} : averageT={} : maxT={} : minT={} : Result [{}] ",
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
            return resultObject.getStatus() + " " + makeStringFromObject( resultObject.getData() );
        }

        return makeStringFromObject( result );
    }

    private String makeStringFromObject( Object resultObject ) {
        if ( resultObject == null ) {
            return "ResultObject is null.";
        }

        if ( resultObject instanceof Collection<?>) {
            log.trace( "ResultObject: {}", resultObject );
            return "collection size=" + ((Collection) resultObject).size();
        }

        if ( resultObject instanceof Map<?, ?>) {
            log.trace( "ResultObject: {}", resultObject );
            return "map size=" + ((Map) resultObject).size();
        }
        return null;
    }

}

class MethodProfile {
    long invokeCount = 0L;
    long minTime = 0L;
    long maxTime = 0L;
    long average = 0L;

    public void updateTime(long executionTime) {
        invokeCount++;
        if (minTime > executionTime || minTime == 0) minTime = executionTime;
        if (maxTime < executionTime || maxTime == 0) maxTime = executionTime;
        average = average + (executionTime - average) / invokeCount;
    }
}
