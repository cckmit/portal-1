package ru.protei.portal.core.model.annotations;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_PrivilegeAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, сообщающая аспекту, что данный метод, требует наличия обязательных привилегий
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Privileged {
    En_Privilege[] value() default {};
    En_Privilege[] requireAny() default {};
}
