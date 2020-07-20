package ru.protei.portal.core.model.enterprise1c.annotation;

import ru.protei.portal.core.model.dict.lang.En_1CParamType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для указания параметров, фильтр запроса которых строится специальным образом
 * Требуется для генерации $filter в URL запроса к 1С
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecialParam1C {

    En_1CParamType value();
}
