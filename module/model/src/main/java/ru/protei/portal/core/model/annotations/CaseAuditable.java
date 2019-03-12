package ru.protei.portal.core.model.annotations;

import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация аудита для указанных {@link En_CaseType}
 * Входит в состав аннотации {@link Auditable}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CaseAuditable {

    /**
     * {@link En_CaseType}, для которого применяется аудит
     */
    En_CaseType caseType();

    /**
     * Тип операции в системе
     */
    En_AuditType auditType();
}
