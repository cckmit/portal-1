package ru.protei.portal.core.client.youtrack.api.builder;

public interface FieldsBuilder {
    QueryBuilder builder();

    CustomFieldsBuilder customFields();

    FieldsBuilder id();

    FieldsBuilder idReadable();

    FieldsBuilder name();

    FieldsBuilder summary();
}
