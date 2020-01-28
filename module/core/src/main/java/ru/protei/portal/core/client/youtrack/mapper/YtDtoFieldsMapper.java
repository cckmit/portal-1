package ru.protei.portal.core.client.youtrack.mapper;

import java.util.Map;

public interface YtDtoFieldsMapper {

    String getFields(Class<?> clazz, boolean includeNotYtDtoFields, Class<?>...includeYtDtoFields);

    Map<String, Class<?>> getEntityNameClassMap();
}
