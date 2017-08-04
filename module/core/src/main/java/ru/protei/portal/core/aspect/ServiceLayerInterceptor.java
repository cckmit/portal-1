package ru.protei.portal.core.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.exception.InsufficientPrivilegesException;
import ru.protei.portal.core.exception.InvalidAuditableObjectException;
import ru.protei.portal.core.exception.InvalidAuthTokenException;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.annotations.Stored;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_PrivilegeEntity;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuditObject;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.AuditService;
import ru.protei.portal.core.service.PolicyService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcHelper;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

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
            Object result = pjp.proceed();
            createAudit( pjp, result );
            return result;
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

    /**
     * Если требуется аудит, оповещаем подписчика
     */
    private void createAudit( ProceedingJoinPoint pjp, Object result ) {

        if ( result instanceof CoreResponse && !((CoreResponse)result).getStatus().equals( En_ResultStatus.OK ) ){
            return;
        }

        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Auditable auditable = method.getDeclaredAnnotation(Auditable.class);

        if ( auditable == null || auditable.value() == null ) {
            return;
        }

        //TODO CRM-16: оповестить создателя Аудита
//        publisherService.publishEvent(new CaseObjectEvent(this, newState, initiator));

        fillAuditParams(pjp, auditable);
    }

    private void fillAuditParams( ProceedingJoinPoint pjp, Auditable auditable ) {

        AuthToken token = findAuthToken( pjp );
        if ( token == null ) {
            return;
        }
        UserSessionDescriptor descriptor = authService.findSession( token );

        Serializable object = findAuditableObject( pjp );
        if ( object == null ) {
            return;
        }

        AuditObject auditObject = new AuditObject();
        auditObject.setCreated( new Date() );

        En_AuditType auditType = auditable.value();
        auditObject.setTypeId( auditType.getId() );

        auditObject.setCreatorId( descriptor.getPerson().getId() );
        auditObject.setCreatorIp( descriptor.getPerson().getIpAddress() );
        auditObject.setCreatorShortName( descriptor.getPerson().getDisplayShortName() );

        auditObject.setEntryInfo( object );

        logger.debug("--------->>> CRM-16 Interceptor fill Audit params:");
        logger.debug("--------->>> CRM-16 Date: " + auditObject.getCreated());
        logger.debug("--------->>> CRM-16 Type: " + auditObject.getTypeId());
        logger.debug("--------->>> CRM-16 PersonId: " + auditObject.getCreatorId());
        logger.debug("--------->>> CRM-16 Person short name: " + auditObject.getCreatorShortName());
        logger.debug("--------->>> CRM-16 Person Ip: " + auditObject.getCreatorIp());
        logger.debug("--------->>> CRM-16 Object: " + auditObject.getEntryInfo());

        auditService.saveAuditObject( token, auditObject );
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
                .filter( (item)-> item.trim().startsWith( "ru.protei.portal.core.service" ) || item.trim().startsWith( "ru.protei.portal.test" ))
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

    private Serializable findAuditableObject( ProceedingJoinPoint pjp ) {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Parameter[] params = method.getParameters();

        for (int i = 0; i < params.length; i++) {

            if ( !params[ i ].isAnnotationPresent( Stored.class ) ) {
                continue;
            }

            Object arg = pjp.getArgs()[ i ];
            if ( arg != null ) {
                return (Serializable) arg;
            }

            throw new InvalidAuditableObjectException();
        }
        return null;
    }

    @Autowired
    AuthService authService;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuditService auditService;
}
