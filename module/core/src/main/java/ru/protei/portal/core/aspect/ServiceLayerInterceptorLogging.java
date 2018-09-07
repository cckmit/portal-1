package ru.protei.portal.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Журналирование
 */
@Aspect
@Order(1)
public class ServiceLayerInterceptorLogging {

    private static final Logger log = getLogger("service");

    HashMap<String, MethodProfile> profiling = new HashMap<>();

    @Pointcut("execution(public ru.protei.portal.api.struct.CoreResponse *(..))")
    private void coreResponseMethod() {}

    @Pointcut("within(ru.protei.portal.core.service..*)")
    private void inServiceLayer() {}

    @Around("coreResponseMethod() && inServiceLayer()")
    public Object serviceMethodLogging(ProceedingJoinPoint pjp) throws Throwable {
        String threadName = Thread.currentThread().getName();
        String methodName = pjp.getSignature().toShortString();
        Thread.currentThread().setName("T-" + Thread.currentThread().getId() + " " + methodName);

        log.debug("calling : {} args: {}", methodName, pjp.getArgs());
        long currentTimeMillis = System.currentTimeMillis();

        CoreResponse result = ERROR_RESPONSE;
        try {

            result = (CoreResponse) pjp.proceed();

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

    private String makeResultAsString( CoreResponse result ) {
        if ( result == null ) {
            return "Result is null.";
        }
        Object resultObject = result.getData();
        if ( resultObject == null ) {
            return makeStatusString( result ) + " ResultObject is null.";
        }

        if ( resultObject instanceof Collection<?>) {
            log.trace( "ResultObject: {}", resultObject );
            return makeStatusString( result ) + " collection size=" + ((Collection) resultObject).size();
        }

        if ( resultObject instanceof Map<?, ?>) {
            log.trace( "ResultObject: {}", resultObject );
            return makeStatusString( result ) + " map size=" + ((Map) resultObject).size();
        }
        return String.valueOf( resultObject );
    }

    private Serializable makeStatusString(CoreResponse result ) {
        return result == null ? "Result is null." : result.getStatus();
    }

      private static final CoreResponse<Object> ERROR_RESPONSE = new CoreResponse<>().error(En_ResultStatus.INTERNAL_ERROR);

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
