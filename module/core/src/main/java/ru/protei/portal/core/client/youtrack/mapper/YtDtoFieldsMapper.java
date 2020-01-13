package ru.protei.portal.core.client.youtrack.mapper;

import java.util.Map;

public interface YtDtoFieldsMapper {

    String getFields(Class<?> clazz, Class<?>...includeClasses);

    Map<String, Class<?>> getEntityNameClassMap();
}
