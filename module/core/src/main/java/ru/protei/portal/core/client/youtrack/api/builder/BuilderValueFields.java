package ru.protei.portal.core.client.youtrack.api.builder;

public interface BuilderValueFields {
    QueryBuilder builder();

    ProjectCustomFieldsBuilder projectCustomFields();

    BuilderValueFields valueId();

    BuilderValueFields valueName();

    BuilderValueFields localizedName();
}
