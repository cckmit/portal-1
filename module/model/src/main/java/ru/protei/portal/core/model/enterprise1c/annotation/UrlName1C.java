package ru.protei.portal.core.model.enterprise1c.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для указания адреса url данной сущности
 * Требуется для подстановки в адрес запроса
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UrlName1C {
    /**
     * Адрес в запросе на 1С
     */
    String value();
}
