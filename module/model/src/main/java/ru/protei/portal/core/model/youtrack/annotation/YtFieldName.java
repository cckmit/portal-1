package ru.protei.portal.core.model.youtrack.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для указания имени поля youtrack-api сущности
 * Если данная аннотация не указана, то используется значение аннотации
 * {@link com.fasterxml.jackson.annotation.JsonProperty} или полное имя поля, если аннотаций не указано
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface YtFieldName {
    /**
     * Имя поля youtrack-api сущности
     */
    String value();
}
