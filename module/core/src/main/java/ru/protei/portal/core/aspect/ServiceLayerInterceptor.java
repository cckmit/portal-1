package ru.protei.portal.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.event.CreateAuditObjectEvent;
import ru.protei.portal.core.exception.InsufficientPrivilegesException;
import ru.protei.portal.core.exception.InvalidAuditableObjectException;
import ru.protei.portal.core.exception.InvalidAuthTokenException;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.CasePrivileged;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.service.EventPublisherService;
import ru.protei.portal.core.service.PolicyService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Mike on 06.11.2016.
 */
@Aspect
@Order(0)
public class ServiceLayerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(ServiceLayerInterceptor.class);

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
            tryDoAudit( pjp, result );
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

            if ( e instanceof ResultStatusException ) {
                En_ResultStatus resultStatus = ((ResultStatusException) e).getResultStatus();
                return handleReturn(pjp.getSignature(), resultStatus);
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


    private void tryDoAudit( ProceedingJoinPoint pjp, Object result ) {

        if ( result instanceof CoreResponse && !((CoreResponse)result).getStatus().equals( En_ResultStatus.OK ) ){
            return;
        }

        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Auditable auditable = method.getDeclaredAnnotation(Auditable.class);

        if ( auditable == null || auditable.value() == null ) {
            return;
        }

        AuthToken token = findArgument( pjp, AuthToken.class );
        if ( token == null ) {
            return;
        }

        AuditableObject auditableObject = findAuditableObject( pjp );
        if ( auditableObject == null ) {
            makeSimpleAudit(token, auditable.value());
            return;
        }

        if (auditable.forCases().length == 0) {
            makeAudit(token, auditable.value(), auditableObject);
            return;
        }

        En_CaseType caseType = findArgument(pjp, En_CaseType.class);
        if (caseType == null) {
            return;
        }

        boolean isCaseTypePresentAtAnnotation = Arrays.asList(auditable.forCases()).contains(caseType);
        if (isCaseTypePresentAtAnnotation) {
            makeAudit(token, auditable.value(), auditableObject);
            return;
        }
    }

    private void makeAudit(AuthToken token, En_AuditType auditType, AuditableObject auditableObject) {
        UserSessionDescriptor descriptor = authService.findSession(token);
        AuditObject auditObject = new AuditObject(auditType.getId(), descriptor, auditableObject);
        publisherService.publishEvent(new CreateAuditObjectEvent(this, auditObject));
    }

    private void makeSimpleAudit(AuthToken token, En_AuditType auditType) {
        UserSessionDescriptor descriptor = authService.findSession(token);

        SimpleAuditableObject auditableObject = new SimpleAuditableObject();
        notAuditableContainer.put(AUDITABLE_TYPE, auditType.name());
        auditableObject.setContainer(notAuditableContainer);

        AuditObject auditObject = new AuditObject(auditType.getId(), descriptor, auditableObject);
        publisherService.publishEvent(new CreateAuditObjectEvent(this, auditObject));
        notAuditableContainer.clear();
    }

    private void checkPrivileges( ProceedingJoinPoint pjp ) {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Privileged privileges = method.getDeclaredAnnotation(Privileged.class);

        if ( privileges == null ) {
            return;
        }

        checkRequireAllPrivileges( pjp, privileges );
        checkRequireAnyPrivileges( pjp, privileges );
        checkRequireCasePrivileges( pjp, privileges );
    }

    private void checkRequireAllPrivileges( ProceedingJoinPoint pjp, Privileged privileges ) {

        if ( privileges.value().length == 0 ) {
            return;
        }

        AuthToken token = findArgument( pjp, AuthToken.class );
        if ( token == null ) {
            return;
        }

        UserSessionDescriptor descriptor = authService.findSession( token );
        if ( !policyService.hasEveryPrivilegeOf( descriptor.getLogin().getRoles(), privileges.value() ) ) {
            throw new InsufficientPrivilegesException();
        }
    }

    private void checkRequireAnyPrivileges( ProceedingJoinPoint pjp, Privileged privileges ) {

        if ( privileges.requireAny().length == 0 ) {
            return;
        }

        AuthToken token = findArgument( pjp, AuthToken.class );
        if ( token == null ) {
            return;
        }

        UserSessionDescriptor descriptor = authService.findSession( token );
        if ( !policyService.hasAnyPrivilegeOf( descriptor.getLogin().getRoles(), privileges.requireAny() ) ) {
            throw new InsufficientPrivilegesException();
        }
    }

    private void checkRequireCasePrivileges( ProceedingJoinPoint pjp, Privileged privileges ) {

        if ( privileges.forCases().length == 0) {
            return;
        }

        AuthToken token = findArgument(pjp, AuthToken.class);
        if (token == null) {
            return;
        }

        En_CaseType caseType = findArgument(pjp, En_CaseType.class);
        if (caseType == null) {
            return;
        }

        CasePrivileged casePrivileged = Arrays.stream(privileges.forCases())
                .filter(cp -> caseType.equals(cp.caseType()))
                .findFirst()
                .orElse(null);

        if (casePrivileged == null) {
            throw new InsufficientPrivilegesException("Provided En_CaseType='" + caseType + "' not matched supported types");
        }

        if (casePrivileged.requireAll().length == 0 && casePrivileged.requireAny().length == 0) {
            return;
        }

        UserSessionDescriptor descriptor = authService.findSession(token);
        Set<UserRole> roles = descriptor.getLogin().getRoles();

        if (casePrivileged.requireAll().length > 0 && !policyService.hasEveryPrivilegeOf(roles, casePrivileged.requireAll())) {
            throw new InsufficientPrivilegesException();
        }

        if (casePrivileged.requireAny().length > 0 && !policyService.hasAnyPrivilegeOf(roles, casePrivileged.requireAny())) {
            throw new InsufficientPrivilegesException();
        }
    }


    private <T> T findArgument(ProceedingJoinPoint pjp, Class<T> clazz) {
        // try to check if method is called from core (in this case, we allow NULL to be passed in authToken)
        Optional<String> firstCoreStackTraceElement = Arrays.stream( Thread.currentThread().getStackTrace() )
                .map( StackTraceElement::toString )
                .filter( (item)-> item.trim().startsWith( "ru.protei.portal.core.service" ) || item.trim().startsWith( "ru.protei.portal.test" ))
                .findFirst();

        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            if ( !params[ i ].getType().equals( clazz ) ) {
                continue;
            }

            Object arg = pjp.getArgs()[ i ];
            if ( arg != null ) {
                return (T) arg;
            }

            if ( firstCoreStackTraceElement.isPresent() ) {
                return null;
            }

            throw new InvalidAuthTokenException();

        }

        return null;
    }

    private AuditableObject findAuditableObject( ProceedingJoinPoint pjp ) {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Parameter[] params = method.getParameters();

        for (int i = 0; i < params.length; i++) {

            Object arg = pjp.getArgs()[ i ];
            if ( !( arg instanceof AuditableObject ) ) {
                if (!params[i].getType().equals(AuthToken.class)) {
                    notAuditableContainer.put(params[i].getName(), arg.toString());
                }

                continue;
            }

            notAuditableContainer.clear();

            if ( arg != null ) {
                if ( arg instanceof Long ){
                    LongAuditableObject longAuditableObject = new LongAuditableObject();
                    longAuditableObject.setId( (Long)arg );
                    return longAuditableObject;
                }
                return (AuditableObject) arg;
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
    EventPublisherService publisherService;

    private static final String AUDITABLE_TYPE = "AuditableType";
    private Map<String, String> notAuditableContainer = new LinkedHashMap<>();
}
