package ru.protei.portal.core.model.yt.annotation;

import ru.protei.portal.core.model.yt.dto.YtDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для использования над полем с типом YtDto.
 * В некоторых случаях youtrack сущности могут содержать поля, которые могут быть любым классом youtrack сущности.
 * В таких случаях данное поле имеет корневой тип YtDto, который позволяет полю быть любой сущностью.
 * Однако, при построении запроса, такое поле имеет слишком большой размер, так как включает в себя все
 * существующие youtrack сущности.
 * Данная аннотация указывает список сущностей, которые будут приниматься и обрабатываться порталом
 * (заместо всех существующих сущностей)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface YtDtoFieldSubclassesSpecifier {
    /** Список классов для получения полей (вместо всех полей YtDto) */
    Class<? extends YtDto>[] value();
}
