package ru.protei.portal.core.model.yt.dto;

/**
 * Базовый класс для всех YouTrack сущностей.
 * Каждый класс сущности должнен быть унаследован от этого класса.
 * Каждый класс сущности должнен быть назван ровно как YouTrack сущность с префиксом "Yt".
 * Пример: Сущности "IssueComment" должен соответствовать класс "YtIssueComment".
 *
 * Поля строятся посредством {@link ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper}
 * Сериализация/десериализация {@link ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider}
 */
public abstract class YtDto {

    public String id;
    public /* readonly */ String $type;

    @Override
    public String toString() { return "id='" + id + "', $type='" + $type + "'"; }
}
