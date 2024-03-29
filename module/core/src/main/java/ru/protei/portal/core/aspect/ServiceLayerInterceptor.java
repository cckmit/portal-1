package ru.protei.portal.core.aspect;

import com.mysql.jdbc.MysqlDataTruncation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.CreateAuditObjectEvent;
import ru.protei.portal.core.exception.InsufficientPrivilegesException;
import ru.protei.portal.core.exception.InvalidAuthTokenException;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.CasePrivileged;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.SimpleAuditableObject;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.jdbc.JdbcHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.*;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.core.aspect.ServiceLayerInterceptorLogging.SERVICE_FACADE_LOGGER_NAME;

/**
 * Created by Mike on 06.11.2016.
 */
@Aspect
@Order(1)
public class ServiceLayerInterceptor {

    /**
     *  Сервис методы "Фасада", строго в пакете service, прочие сервисы в подпакетах
     */
    @Pointcut("within(ru.protei.portal.core.service.*)")
    private void inServiceFacade() {
    }

    @Pointcut("execution(public ru.protei.portal.api.struct.Result *(..))")
    private void methodWithResult() {}

    @Pointcut("within(ru.protei.portal.core.service.auth.*)")
    public void authServiceMethod() {
    }

    @Around("methodWithResult() && authServiceMethod()")
    public Object unhandledExceptionAuthMethods( ProceedingJoinPoint pjp ) {
        try {
            Result result = (Result) pjp.proceed();
            publishEvents( result == null ? null : result.getEvents() );
            return result;
        } catch (Throwable t) {
            logger.warn( "Unhandled exception from auth methods: {}", pjp.getSignature(), t );
            return null;
        }
    }

    /**
     * Все сервис методы "Фасада" обязаны возвращать объект результата выполения "Result",
     * принимать признак безопасности "AuthToken", могут быть аннотированы как @Privileged.
     * Если метод не возврщает "Result", не требует авторизации или не попадает в журнал аудита,
     * то наверное такой сервис метод не относится к "Фасаду"
     * и должен быть размещен в отдельном пакете и обрабатываться иначе.
     */
    @Around("inServiceFacade()")
    public Object serviceFacadeProcessing (ProceedingJoinPoint pjp) {

        try {
            checkPrivileges( pjp );
            logger.trace( "serviceFacadeProcessing(): begin" );
            // Все сервис методы "Фасада" обязаны возвращать объект результата выполения "Result"...
            Result<?> result = (Result) pjp.proceed(); // Нужно падать если не приводтся к Result!
            logger.trace( "serviceFacadeProcessing(): succsess" );
            tryDoAudit( pjp, result );
            publishEvents( result == null ? null : result.getEvents() );
            return result;
        }
        catch (Throwable e) {
            logger.error("service layer unhandled exception", e);

            if (JdbcHelper.isTemporaryDatabaseError (e)) {
                return error( En_ResultStatus.DB_TEMP_ERROR);
            }

            if (e instanceof DataIntegrityViolationException &&
                    e.getCause() instanceof MysqlDataTruncation) {
                return error( En_ResultStatus.MYSQL_DATA_TRUNCATION);
            }

            if (e instanceof DuplicateKeyException) {
                return error(En_ResultStatus.ALREADY_EXIST);
            }

            if (e instanceof SQLException) {
                return error( En_ResultStatus.DB_COMMON_ERROR);
            }

            if (e instanceof InvalidAuthTokenException ) {
                return error( En_ResultStatus.INVALID_SESSION_ID );
            }

            if ( e instanceof InsufficientPrivilegesException ) {
                return error( En_ResultStatus.PERMISSION_DENIED );
            }

            if ( e instanceof RollbackTransactionException) {
                return error( ((RollbackTransactionException) e).getResultStatus());
            }

            return error( En_ResultStatus.INTERNAL_ERROR );
        }
    }

    private void publishEvents( List<ApplicationEvent> events ) {
        if (events == null) return;
        for (ApplicationEvent event : events) {
            publisherService.publishEvent( event );
        }
    }

    private void tryDoAudit( ProceedingJoinPoint pjp, Object result ) {

        if ( result instanceof Result && !((Result)result).getStatus().equals( En_ResultStatus.OK ) ){
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

        Map<String, Object> notAuditableContainer = new LinkedHashMap<>();

        AuditableObject auditableObject = findAuditableObject( pjp, notAuditableContainer );
        if ( auditableObject == null ) {
            makeSimpleAudit(token, auditable.value(), notAuditableContainer);
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
        AuditObject auditObject = new AuditObject(
                auditType,
                auditableObject,
                token.getPersonId(),
                token.getIp(),
                token.getPersonDisplayShortName()
        );
        publisherService.publishEvent(new CreateAuditObjectEvent(this, auditObject));
    }

    private void makeSimpleAudit(AuthToken token, En_AuditType auditType, Map<String, Object> notAuditableContainer) {
        SimpleAuditableObject auditableObject = new SimpleAuditableObject();
        notAuditableContainer.put(AUDITABLE_TYPE, auditType.name());
        auditableObject.setContainer(notAuditableContainer);

        makeAudit(token, auditType, auditableObject);
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

        if ( !policyService.hasEveryPrivilegeOf( token.getRoles(), privileges.value() ) ) {
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

        if ( !policyService.hasAnyPrivilegeOf( token.getRoles(), privileges.requireAny() ) ) {
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

        if (casePrivileged.requireAll().length > 0 && !policyService.hasEveryPrivilegeOf(token.getRoles(), casePrivileged.requireAll())) {
            throw new InsufficientPrivilegesException();
        }

        if (casePrivileged.requireAny().length > 0 && !policyService.hasAnyPrivilegeOf(token.getRoles(), casePrivileged.requireAny())) {
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

    private AuditableObject findAuditableObject(ProceedingJoinPoint pjp, Map<String, Object> notAuditableContainer) {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Parameter[] params = method.getParameters();
        notAuditableContainer.clear();

        for (int i = 0; i < params.length; i++) {
            Object arg = pjp.getArgs()[i];

            if (!(arg instanceof AuditableObject)) {
                if (!params[i].getType().equals(AuthToken.class)) {
                    notAuditableContainer.put(params[i].getName(), arg);
                }

                continue;
            }

            return (AuditableObject) arg;
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
    private static Logger logger = LoggerFactory.getLogger(SERVICE_FACADE_LOGGER_NAME);
}
