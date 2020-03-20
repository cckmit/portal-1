package ru.protei.portal.jira.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import ru.protei.portal.core.model.struct.MethodProfile;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

@Aspect
@Order(0)
public class JiraServiceLayerInterceptorLogging {

    @Pointcut("within(ru.protei.portal.jira.service.*)")
    private void inServiceLayer() {}

    @Around("inServiceLayer()")
    public Object serviceMethodLogging(ProceedingJoinPoint pjp) throws Throwable {

        String threadName = Thread.currentThread().getName();
        String methodName = pjp.getSignature().toShortString();
        String threadToken = "T-" + Thread.currentThread().getId() + " M-JIRA " + methodName;

        Thread.currentThread().setName(threadToken);

        log.debug("calling : {} args: {}", methodName, pjp.getArgs());

        long currentTimeMillis = System.currentTimeMillis();
        Object result = null;

        try {

            result = pjp.proceed();

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

    private String makeResultAsString(Object result) {

        if (result == null) {
            return "Result is null.";
        }

        if (result instanceof Collection<?>) {
            log.trace("Result: {}", result);
            return "collection size=" + ((Collection) result).size();
        }

        if (result instanceof Map<?, ?>) {
            log.trace("Result: {}", result);
            return "map size=" + ((Map) result).size();
        }

        return String.valueOf(result);
    }

    private Map<String, MethodProfile> profiling = new ConcurrentHashMap<>();
    public static final String SERVICE_FACADE_LOGGER_NAME = "service";
    private static final Logger log = getLogger(SERVICE_FACADE_LOGGER_NAME);
}
