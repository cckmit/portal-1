package ru.protei.portal.core.model.annotations;

import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.struct.AuditableObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, сообщающая аспекту, что результатом работы данного метода должна стать запись в таблице Аудита.
 *
 * Среди аргументов метода должены присутствовать {@link AuthToken} и дочерний объект {@link AuditableObject}.
 * Если хотя бы один из аргументов отсутствует, аудит не будет создан.
 *
 * При использовании {@link #value()} содержимое {@link #forCases()} игнорируется.
 */
@Retention( RetentionPolicy.RUNTIME)
@Target( ElementType.METHOD)
public @interface Auditable {

    /**
     * Тип операции в системе
     */
    En_AuditType[] value() default {};

    /**
     * Аудит для указанных {@link En_CaseType}
     */
    CaseAuditable[] forCases() default {};
}
