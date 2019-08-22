package ru.protei.portal.core.client.youtrack.api.builder;

public interface QueryBuilder {
    FieldsBuilder fields();

    CustomFieldsBuilder customFields();

    PresetsBuilder preset();

    String build();
}
