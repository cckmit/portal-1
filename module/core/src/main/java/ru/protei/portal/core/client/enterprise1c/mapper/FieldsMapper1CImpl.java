package ru.protei.portal.core.client.enterprise1c.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.reflect.FieldUtils;
import ru.protei.portal.core.model.dict.lang.En_1CParamType;
import ru.protei.portal.core.model.enterprise1c.annotation.SpecialParam1C;
import ru.protei.portal.core.model.helper.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldsMapper1CImpl implements FieldsMapper1C{

    @Override
    public List<String> getFields(Class<?> clazz) {
        List<String> result = new ArrayList<>();

        List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        for (Field field : fields) {
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null && StringUtils.isNotEmpty(jsonProperty.value())) {
                result.add(jsonProperty.value());
                continue;
            }
            if (jsonProperty != null && StringUtils.isEmpty(jsonProperty.value())) {
                result.add(field.getName());
            }
        }

        return result;
    }

    @Override
    public String getField(Field field) {
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (jsonProperty != null && StringUtils.isNotEmpty(jsonProperty.value())) {
            return jsonProperty.value();
        }
        if (jsonProperty != null && StringUtils.isEmpty(jsonProperty.value())) {
          return field.getName();
        }

        return null;
    }

    @Override
    public En_1CParamType getSpecialParamType(Field field) {
        SpecialParam1C specialParam1C = field.getAnnotation(SpecialParam1C.class);
        if(specialParam1C != null) {
            return specialParam1C.value();
        }

        return null;
    }
}
