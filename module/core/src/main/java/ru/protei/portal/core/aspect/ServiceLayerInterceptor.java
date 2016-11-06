package ru.protei.portal.core.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;

/**
 * Created by Mike on 06.11.2016.
 */
@Aspect
public class ServiceLayerInterceptor {

    private static Logger logger = Logger.getLogger(ServiceLayerInterceptor.class);

    @Pointcut("execution(public ru.protei.portal.api.struct.CoreResponse *(..))")
    private void coreResponseMethod() {}


    @Pointcut("within(ru.protei.portal.core.service..*)")
    private void inServiceLayer() {}


    @Around("coreResponseMethod() && inServiceLayer()")
    public Object unhandledException (ProceedingJoinPoint pjp) {

        try {
            return pjp.proceed();
        }
        catch (Throwable e) {
            logger.debug("service layer unhandled exception", e);
        }

        return new CoreResponse<>().error(En_ResultStatus.INTERNAL_ERROR);
    }

}
