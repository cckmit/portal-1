package ru.protei.portal.core.client.youtrack.api.builder;

public interface CustomFieldsBuilder {
    QueryBuilder builder();

    BuilderValueFields valueFields();

    ProjectCustomFieldsBuilder projectCustomFields();

    CustomFieldsBuilder customId();

    CustomFieldsBuilder customName();
}
