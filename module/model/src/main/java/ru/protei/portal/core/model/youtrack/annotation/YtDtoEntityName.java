package ru.protei.portal.core.model.youtrack.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для указания имени youtrack-api сущности
 * Если данная аннотация не указана, то используется полное имя класса без приставки "Yt"
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface YtDtoEntityName {
    /**
     * Имя youtrack-api сущности
     */
    String value();
}
