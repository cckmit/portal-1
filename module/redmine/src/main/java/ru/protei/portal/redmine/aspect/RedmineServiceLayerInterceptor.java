package ru.protei.portal.redmine.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.struct.MethodProfile;
import ru.protei.winter.jdbc.JdbcHelper;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.protei.portal.api.struct.Result.error;

@Aspect
public class RedmineServiceLayerInterceptor {

    @Pointcut("within(ru.protei.portal.redmine.service.*)")
    private void inServiceLayer() {}

    @Around("inServiceLayer()")
    public Object serviceMethodLogging(ProceedingJoinPoint pjp) throws Throwable {

        String threadName = Thread.currentThread().getName();
        String methodName = pjp.getSignature().toShortString();
        String threadToken = "T-" + Thread.currentThread().getId() + " Redmine " + methodName;

        Thread.currentThread().setName(threadToken);

        log.debug("calling : {} args: {}", methodName, pjp.getArgs());

        long currentTimeMillis = System.currentTimeMillis();
        Object result = null;

        try {

            result = pjp.proceed();

        } catch (Throwable e) {
                log.error("service layer unhandled exception", e);

                if (JdbcHelper.isTemporaryDatabaseError (e)) {
                    return error( En_ResultStatus.DB_TEMP_ERROR);
                }

                if (e instanceof SQLException) {
                    return error( En_ResultStatus.DB_COMMON_ERROR);
                }

                if ( e instanceof ResultStatusException) {
                    return error( ((ResultStatusException) e).getResultStatus());
                }

                return error( En_ResultStatus.INTERNAL_ERROR );

        } finally {

            long executionTime = System.currentTimeMillis() - currentTimeMillis;
            MethodProfile profile = getMethodProfile(methodName);

            profile.updateTime(executionTime);

            log.debug("result  : {} : count={} : time={} : averageT={} : maxT={} : minT={} : Result [{}] ",
                    methodName, profile.invokeCount, executionTime, profile.average, profile.maxTime, profile.minTime,
                    makeResultAsString(result));

            Thread.currentThread().setName(threadName);
        }

        return result;
    }

    private MethodProfile getMethodProfile(String methodName) {
        if (!profiling.containsKey(methodName)) {
            profiling.put(methodName, new MethodProfile());
        }
        return profiling.get(methodName);
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

    private String makeStringFromObject(Object result) {

        if (result == null) {
            return "Result is null.";
        }

        if (result instanceof Collection<?>) {
            log.trace("ResultObject: {}", result);
            return "collection size=" + ((Collection) result).size();
        }

        if (result instanceof Map<?, ?>) {
            log.trace("ResultObject: {}", result);
            return "map size=" + ((Map) result).size();
        }

        return String.valueOf(result);
    }

    private Map<String, MethodProfile> profiling = new ConcurrentHashMap<>();
    public static final String SERVICE_FACADE_LOGGER_NAME = "service";
    private static final Logger log = getLogger(SERVICE_FACADE_LOGGER_NAME);
}
