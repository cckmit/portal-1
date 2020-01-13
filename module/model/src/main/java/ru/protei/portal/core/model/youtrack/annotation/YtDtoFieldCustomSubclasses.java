package ru.protei.portal.core.model.youtrack.annotation;

import ru.protei.portal.core.model.youtrack.dto.YtDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для использования над полем с типом или подтипом YtDto.
 * Для построения полей будут учитываться:
 * - Все поля класса
 * - Все поля всех родительских классов
 * - Если аннотация не указана, тогда все дочерние классы
 * - Если аннотация указана, тогда только те классы, которые указаны в аннотации
 *
 * Данная аннотация обязательна для полей с типом YtDto.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface YtDtoFieldCustomSubclasses {
    /**
     * Список классов
     */
    Class<? extends YtDto>[] value();
}
