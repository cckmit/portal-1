package ru.protei.portal.core.client.youtrack.api.builder;

public interface ProjectCustomFieldsBuilder {
    QueryBuilder builder();

    BuilderValueFields valueFields();

    ProjectCustomFieldsBuilder projectCfId();

    ProjectCustomFieldsBuilder projectCfName();

    ProjectCustomFieldsBuilder emptyFieldText();
}
