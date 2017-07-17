package ru.protei.portal.core.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.exception.InsufficientPrivilegesException;
import ru.protei.portal.core.exception.InvalidAuthTokenException;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.PolicyService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Mike on 06.11.2016.
 */
@Aspect
public class ServiceLayerInterceptor {

    private static Logger logger = Logger.getLogger(ServiceLayerInterceptor.class);

    @Pointcut("execution(public ru.protei.portal.api.struct.CoreResponse *(..))")
//    @Pointcut("call(public ru.protei.portal.api.struct.CoreResponse *(..))")
    private void coreResponseMethod() {}


    @Pointcut("within(ru.protei.portal.core.service..*)")
    private void inServiceLayer() {}


    @Around("coreResponseMethod() && inServiceLayer()")
    public Object unhandledException (ProceedingJoinPoint pjp) {

        try {
            checkPrivileges( pjp );
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

            if (e instanceof InvalidAuthTokenException ) {
                return handleReturn(pjp.getSignature(), En_ResultStatus.INVALID_SESSION_ID );
            }

            if ( e instanceof InsufficientPrivilegesException ) {
                return handleReturn(pjp.getSignature(), En_ResultStatus.PERMISSION_DENIED );
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

    private void checkPrivileges( ProceedingJoinPoint pjp ) {
        checkRequireAllPrivileges( pjp );
        checkRequireAnyPrivileges( pjp );
    }

    private void checkRequireAllPrivileges( ProceedingJoinPoint pjp ) {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Privileged privileges = method.getDeclaredAnnotation(Privileged.class);

        if ( privileges == null || privileges.value().length == 0 ) {
            return;
        }

        AuthToken token = findAuthToken( pjp );
        if ( token == null ) {
            return;
        }

        UserSessionDescriptor descriptor = authService.findSession( token );
        if ( !policyService.hasEveryPrivilegeOf( descriptor.getLogin().getRoles(), privileges.value() ) ) {
            throw new InsufficientPrivilegesException();
        }
    }

    private void checkRequireAnyPrivileges( ProceedingJoinPoint pjp ) {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Privileged privileges = method.getDeclaredAnnotation(Privileged.class);

        if ( privileges == null || privileges.requireAny().length == 0 ) {
            return;
        }

        AuthToken token = findAuthToken( pjp );
        if ( token == null ) {
            return;
        }

        UserSessionDescriptor descriptor = authService.findSession( token );
        if ( !policyService.hasAnyPrivilegeOf( descriptor.getLogin().getRoles(), privileges.requireAny() ) ) {
            throw new InsufficientPrivilegesException();
        }
    }

    private AuthToken findAuthToken( ProceedingJoinPoint pjp ) {
        // try to check if method is called from core (in this case, we allow NULL to be passed in authToken)
        Optional<String> firstCoreStackTraceElement = Arrays.asList( Thread.currentThread().getStackTrace() ).stream()
                .map( StackTraceElement::toString )
                .filter( (item)-> item.trim().startsWith( "ru.protei.portal.core.service" ) )
                .findFirst();

        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            if ( !params[ i ].getType().equals( AuthToken.class ) ) {
                continue;
            }

            Object arg = pjp.getArgs()[ i ];
            if ( arg != null ) {
                return (AuthToken) arg;
            }

            if ( firstCoreStackTraceElement.isPresent() ) {
                return null;
            }

            throw new InvalidAuthTokenException();

        }

        return null;
    }

    @Autowired
    AuthService authService;

    @Autowired
    PolicyService policyService;
}
