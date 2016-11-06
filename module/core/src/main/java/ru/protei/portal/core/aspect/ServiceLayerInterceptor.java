package ru.protei.portal.core.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.winter.jdbc.JdbcHelper;

import java.sql.SQLDataException;
import java.sql.SQLException;

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

            if (JdbcHelper.isTemporaryDatabaseError (e)) {
                return handleReturn(pjp.getSignature(), En_ResultStatus.DB_TEMP_ERROR);
            }

            if (e instanceof SQLException) {
                return handleReturn(pjp.getSignature(), En_ResultStatus.DB_COMMON_ERROR);
            }
        }

        return handleReturn(pjp.getSignature(), En_ResultStatus.INTERNAL_ERROR);
    }

    private Object handleReturn (Signature signature, En_ResultStatus status) {
        if (!(signature instanceof MethodSignature))
            return null;

        if (CoreResponse.class.isAssignableFrom(((MethodSignature)signature).getReturnType())) {
            return new CoreResponse<>().error(status);
        }

        return null;
    }
}
