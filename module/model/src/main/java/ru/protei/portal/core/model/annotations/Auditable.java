package ru.protei.portal.core.model.annotations;

import ru.protei.portal.core.model.dict.En_AuditType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, сообщающая аспекту, что результатом работы данного метода должна стать запись в таблице Аудита
 */
@Retention( RetentionPolicy.RUNTIME)
@Target( ElementType.METHOD)
public @interface Auditable {
    En_AuditType value();
}
