package ru.protei.portal.core.model.yt.api;

/**
 * Базовый класс для всех YouTrack сущностей.
 * Каждый класс сущности должнен быть унаследован от этого класса.
 * Каждый класс сущности должнен быть назван ровно как YouTrack сущность с префиксом "Yt".
 * Пример: Сущности "IssueComment" должен соответствовать класс "YtIssueComment".
 */
public abstract class YtDto {

    public String id;
    public /* readonly */ String $type;

    @Override
    public String toString() { return "id='" + id + "', $type='" + $type + "'"; }
}
