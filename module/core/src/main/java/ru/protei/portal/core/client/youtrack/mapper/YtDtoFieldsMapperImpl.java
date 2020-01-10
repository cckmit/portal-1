package ru.protei.portal.core.client.youtrack.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.reflections.Reflections;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.annotation.YtDtoFieldSubclassesSpecifier;
import ru.protei.portal.core.model.yt.dto.YtDto;

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
    public <T> String getFields(Class<T> clazz) {
        if (dto2fields == null || dto2fields.isEmpty()) {
            setup();
        }
        if (TypeUtils.isAssignable(clazz, YtDto.class)) {
            return dto2fields.get(clazz);
        }
        if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            if (TypeUtils.isAssignable(componentType, YtDto.class)) {
                return dto2fields.get(componentType);
            }
        }
        return null;
    }

    @Override
    public Map<String, Class<? extends YtDto>> getEntityNameClassMap() {
        if (entity2dto == null || entity2dto.isEmpty()) {
            setup();
        }
        return entity2dto;
    }

    private <T extends YtDto> String buildFieldsOfClass(Class<T> clazz, BuildFieldsContext context) {
        Map<String, Object> map = buildFieldMapOfClass(clazz, context);
        return map2string(map);
    }

    private <T extends YtDto> Map<String, Object> buildFieldMapOfClass(Class<T> rootClazz, BuildFieldsContext context) {
        return buildFieldMapOfClass(null, rootClazz, context);
    }

    private <T extends YtDto> Map<String, Object> buildFieldMapOfClass(Field rootField, Class<T> rootClazz, BuildFieldsContext context) {
        // Map<String, Map<String, Map<String, Map<String, ...>>>>
        Map<String, Object> fieldTree = new HashMap<>();

        context.pushClass(rootClazz);

        List<Field> fields = getFieldsOfGivenAndSuperAndSubClasses(rootField, rootClazz, context);

        for (Field field : fields) {
            String fieldName = getFieldName(field);
            Map<String, Object> fieldMapOld;
            if (fieldTree.get(fieldName) instanceof Map) {
                //noinspection unchecked
                fieldMapOld = (Map<String, Object>) fieldTree.get(fieldName);
            } else {
                fieldMapOld = new HashMap<>();
            }
            Class<T> clazz = getClassAssignableFromYtDto(field);
            if (clazz == null) {
                fieldTree.put(fieldName, fieldMapOld);
                continue;
            }
            if (context.hasClass(clazz)) {
                // no recursions are welcome here
                continue;
            }
            Map<String, Object> fieldMap = buildFieldMapOfClass(field, clazz, context);
            Map<String, Object> joinedMap = joinMaps(fieldMapOld, fieldMap);
            fieldTree.put(fieldName, joinedMap);
        }

        context.removeClass(rootClazz);
        return fieldTree;
    }

    private <T extends YtDto> List<Field> getFieldsOfGivenAndSuperAndSubClasses(Field rootField, Class<T> rootClazz, BuildFieldsContext context) {
        List<Field> fields = FieldUtils.getAllFieldsList(rootClazz);
        List<Class<? extends YtDto>> subclasses = new ArrayList<>();
        if (rootClazz == YtDto.class && rootField != null) {
            YtDtoFieldSubclassesSpecifier specifier = rootField.getAnnotation(YtDtoFieldSubclassesSpecifier.class);
            if (specifier == null) {
                throw new IllegalStateException(String.format("Field [%s] has no YtDtoFieldSpecifier annotation", rootField));
            }
            subclasses.addAll(Arrays.asList(specifier.value()));
        } else {
            YtDtoFieldSubclassesSpecifier specifier = rootField != null ? rootField.getAnnotation(YtDtoFieldSubclassesSpecifier.class) : null;
            if (specifier != null) {
                subclasses.addAll(Arrays.asList(specifier.value()));
            } else {
                subclasses.addAll(getSubclasses(rootClazz, context));
            }
        }
        for (Class<? extends YtDto> subclass : subclasses) {
            List<Field> subfields = Arrays.asList(subclass.getFields());
            fields.addAll(subfields);
        }
        return fields;
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
            if (fieldType.isArray()) {
                Class<?> componentType = fieldType.getComponentType();
                if (TypeUtils.isAssignable(componentType, YtDto.class)) {
                    return (Class<T>) componentType;
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
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (jsonProperty != null && StringUtils.isNotEmpty(jsonProperty.value())) {
            return jsonProperty.value();
        }
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

    private Map<String, Object> joinMaps(Map<String, Object> map1, Map<String, Object> map2) {
        Map<String, Object> result = new HashMap<>(map1);
        map2.forEach((key, map) -> {
            Object v = result.get(key);
            if (v instanceof Map) {
                //noinspection unchecked
                result.put(key, joinMaps((Map<String, Object>) v, (Map<String, Object>) map));
            } else {
                result.put(key, map);
            }
        });
        return result;
    }

    private String map2string(Map<String, Object> map) {
        Collection<String> tokens = new ArrayList<>();
        map.forEach((token, subTokens) -> {
            if (subTokens instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) subTokens;
                if (subMap.isEmpty()) {
                    tokens.add(token);
                } else {
                    tokens.add(token + "(" + map2string(subMap) + ")");
                }
            } else {
                tokens.add(token);
            }
        });
        return String.join(",", tokens);
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
        public void removeClass(Class<?> clazz) { classStack.remove(clazz); }
        public boolean hasClass(Class<?> clazz) { return classStack.contains(clazz); }
    }
}
