package ru.protei.portal.core.model.annotations;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, сообщающая аспекту, что данный метод, требует наличия обязательных привилегий.
 *
 * Среди аргументов метода должен присутствовать {@link AuthToken). Если аргумент
 * отсутствует, проверка привилегии не будет производиться.
 *
 * При использовании {@link #forCases()} среди аргументов метода должен присутствовать {@link En_CaseType). Если
 * аргумент отсутствует, проверка привилегий из {@link #forCases()} не будет производиться.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Privileged {

    /**
     * Набор привилегий, каждая из которых обязательна
     */
    En_Privilege[] value() default {};

    /**
     * Набор привилегий, из которых обязательна хотя бы одна
     */
    En_Privilege[] requireAny() default {};

    /**
     * Набор привилегий для указанных {@link En_CaseType}
     */
    CasePrivileged[] forCases() default {};
}
