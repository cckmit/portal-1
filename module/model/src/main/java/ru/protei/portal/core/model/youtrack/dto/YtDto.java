package ru.protei.portal.core.model.youtrack.dto;

import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;
import ru.protei.portal.core.model.youtrack.annotation.YtEntityName;
import ru.protei.portal.core.model.youtrack.annotation.YtFieldName;

/**
 * Базовый класс для всех YouTrack сущностей.
 * Каждый класс сущности должнен быть унаследован от этого класса.
 *
 * Каждый класс сущности должнен быть назван ровно как YouTrack сущность с префиксом "Yt" или иметь
 * аннотацию {@link YtEntityName}
 * Пример: Сущности "IssueComment" должен соответствовать класс "YtIssueComment" или класс
 * с аннотацией @YtEntityName("IssueComment")
 *
 * Каждое поле сущности должно быть названо ровно как поле YouTrack сущности или иметь аннотацию
 * {@link YtFieldName} или иметь аннотацию
 * {@link com.fasterxml.jackson.annotation.JsonProperty}
 *
 * Поля строятся посредством {@link ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper}
 * Сериализация/десериализация {@link ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider}
 *
 * https://www.jetbrains.com/help/youtrack/standalone/api-entities.html
 */
public abstract class YtDto {

    @YtAlwaysInclude
    public String id;
    @YtAlwaysInclude
    public /* readonly */ String $type;

    @Override
    public String toString() { return "id='" + id + "', $type='" + $type + "'"; }
}
