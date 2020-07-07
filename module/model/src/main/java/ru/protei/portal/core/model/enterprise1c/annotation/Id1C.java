package ru.protei.portal.core.model.enterprise1c.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для указания параметра-идентификатора (ключа) в сущности 1С
 * Требуется для генерации $filter в URL запроса к 1С, так как перед значениями ключей необходимо добавлять слово "guid"
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id1C {
}
