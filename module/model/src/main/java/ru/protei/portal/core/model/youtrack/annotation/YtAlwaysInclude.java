package ru.protei.portal.core.model.youtrack.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация над полем youtrack-api сущности, указывающая на то, что данное поле необходимо всегда включать в запрос.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface YtAlwaysInclude {
}
