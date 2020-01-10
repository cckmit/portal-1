package ru.protei.portal.core.client.youtrack.mapper;

import ru.protei.portal.core.model.yt.dto.YtDto;

import java.util.Map;

public interface YtDtoFieldsMapper {

    YtDtoFieldsMapper setup();

    <T> String getFields(Class<T> clazz);

    Map<String, Class<? extends YtDto>> getEntityNameClassMap();
}
