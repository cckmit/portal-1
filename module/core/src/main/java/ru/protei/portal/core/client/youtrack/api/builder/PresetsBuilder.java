package ru.protei.portal.core.client.youtrack.api.builder;

public interface PresetsBuilder {

    QueryBuilder fieldsDefaults();

    QueryBuilder customFieldsDefaults();

    QueryBuilder idAndCustomFieldsDefaults();
}
