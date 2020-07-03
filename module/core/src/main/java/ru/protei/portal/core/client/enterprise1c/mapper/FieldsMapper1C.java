package ru.protei.portal.core.client.enterprise1c.mapper;

import java.lang.reflect.Field;
import java.util.List;

public interface FieldsMapper1C {

    List<String> getFields(Class<?> clazz);

    String getField(Field field);

}
