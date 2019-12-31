package ru.protei.portal.core.client.youtrack.mapper;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.reflections.Reflections;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.api.YtDto;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class YtDtoFieldsMapperImpl implements YtDtoFieldsMapper {

    @PostConstruct
    public void init() {
        setup();
    }

    @Override
    public YtDtoFieldsMapper setup() {
        clean();
        Reflections reflections = makeReflections();
        Set<Class<? extends YtDto>> allClasses = getSubclasses(YtDto.class, new BuildFieldsContext(reflections));
        for (Class<? extends YtDto> clazz : allClasses) {
            String className = ClassUtils.getSimpleName(clazz);
            String entityName = convertClassNameToYtEntityName(className);
            String entityFields = buildFieldsOfClass(clazz, new BuildFieldsContext(reflections));
            dto2fields.put(clazz, entityFields);
            entity2dto.put(entityName, clazz);
        }
        return this;
    }

    @Override
    public <T extends YtDto> String getFields(Class<T> clazz) {
        if (dto2fields == null || dto2fields.isEmpty()) {
            setup();
        }
        return dto2fields.get(clazz);
    }

    @Override
    public Map<String, Class<? extends YtDto>> getEntityNameClassMap() {
        if (entity2dto == null || entity2dto.isEmpty()) {
            setup();
        }
        return entity2dto;
    }

    private <T extends YtDto> String buildFieldsOfClass(Class<T> clazz, BuildFieldsContext context) {
        context.pushClass(clazz);
        Set<String> tokens = new HashSet<>();
        Field[] fields = FieldUtils.getAllFields(clazz);
        Set<Class<? extends T>> subclasses = getSubclasses(clazz, context);
        for (Field field : fields) {
            String token = buildFieldsOfClassField(field, context);
            if (StringUtils.isNotEmpty(token)) {
                tokens.add(token);
            }
        }
        for (Class<? extends T> subclass : subclasses) {
            Field[] subfields = subclass.getFields();
            for (Field field : subfields) {
                String token = buildFieldsOfClassField(field, context);
                if (StringUtils.isNotEmpty(token)) {
                    tokens.add(token);
                }
            }
        }
        return String.join(",", tokens);
    }

    private <T extends YtDto> String buildFieldsOfClassField(Field field, BuildFieldsContext context) {
        String fieldName = getFieldName(field);
        Class<T> ytDtoClass = getClassAssignableFromYtDto(field);
        if (ytDtoClass != null) {
            if (context.hasClass(ytDtoClass)) {
                // no recursions are welcome here
                return "";
            }
            String query = buildFieldsOfClass(ytDtoClass, context);
            return fieldName + "(" + query + ")";
        }
        return fieldName;
    }

    @SuppressWarnings("unchecked")
    private <T extends YtDto> Class<T> getClassAssignableFromYtDto(Field field) {
        try {
            Class<?> fieldType = field.getType();
            if (TypeUtils.isAssignable(fieldType, YtDto.class)) {
                return (Class<T>) fieldType;
            }
            if (TypeUtils.isAssignable(fieldType, Collection.class)) {
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    Type genericType = pType.getActualTypeArguments()[0];
                    if (TypeUtils.isAssignable(genericType, YtDto.class)) {
                        return (Class<T>) genericType;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private <T extends YtDto> Set<Class<? extends T>> getSubclasses(Class<T> clazz, BuildFieldsContext context) {
        return context.reflections.getSubTypesOf(clazz);
    }

    private String getFieldName(Field field) {
        // TODO json annotations
        return field.getName();
    }

    private String convertClassNameToYtEntityName(String className) {
        return className.replaceFirst("^Yt", "");
    }

    private void clean() {
        dto2fields = new HashMap<>();
        entity2dto = new HashMap<>();
    }

    private Reflections makeReflections() {
        return new Reflections(ClassUtils.getPackageCanonicalName(YtDto.class));
    }

    private Map<Class<? extends YtDto>, String> dto2fields;
    private Map<String, Class<? extends YtDto>> entity2dto;

    private static class BuildFieldsContext {
        public final Reflections reflections;
        public final List<Class<?>> classStack;
        public BuildFieldsContext(Reflections reflections) {
            this.reflections = reflections;
            this.classStack = new ArrayList<>();
        }
        public void pushClass(Class<?> clazz) { classStack.add(clazz); }
        public boolean hasClass(Class<?> clazz) { return classStack.contains(clazz); }
    }
}
