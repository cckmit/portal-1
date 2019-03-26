package ru.protei.portal.core.model.annotations;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, служащая для указания привилегий для работы с case-сущностью
 * Входит в состав аннотации {@link Privileged}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CasePrivileged {

    /**
     * {@link En_CaseType}, для которого применяются указанные правила привилегий
     */
    En_CaseType caseType();

    /**
     * Набор привилегий, каждая из которых обязательна для текущего {@link En_CaseType}
     */
    En_Privilege[] requireAll() default {};

    /**
     * Набор привилегий, из которых обязательна хотя бы одна для текущего {@link En_CaseType}
     */
    En_Privilege[] requireAny() default {};
}
