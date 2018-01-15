package ru.protei.portal.core.model.annotations;

import ru.protei.portal.core.model.dict.En_CaseState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CaseHandler {
    En_CaseState external();

    En_CaseState oldCase();

    En_CaseState newCase();
}
