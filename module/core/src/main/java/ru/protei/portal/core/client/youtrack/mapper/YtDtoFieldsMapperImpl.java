package ru.protei.portal.core.client.youtrack.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.youtrack.annotation.YtEntityName;
import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;
import ru.protei.portal.core.model.youtrack.annotation.YtCustomSubclasses;
import ru.protei.portal.core.model.youtrack.annotation.YtFieldName;
import ru.protei.portal.core.model.youtrack.dto.YtDto;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class YtDtoFieldsMapperImpl implements YtDtoFieldsMapper {

    @Override
    public String getFields(Class<?> clazz, boolean includeNotYtDtoFields, Class<?>...includeYtDtoFields) {
        Class<?> type = null;
        if (TypeUtils.isAssignable(clazz, YtDto.class)) {
            type = clazz;
        }
        if (clazz.isArray() && TypeUtils.isAssignable(clazz.getComponentType(), YtDto.class)) {
            type = clazz.getComponentType();
        }
        if (type == null) {
            return null;
        }
        BuildFieldsContext context = new BuildFieldsContext(
                getReflections(),
                includeNotYtDtoFields,
                includeYtDtoFields == null ? Collections.emptyList() : Arrays.asList(includeYtDtoFields)
        );
        return buildFieldsOfClass(type, context);
    }

    @Override
    public Map<String, Class<?>> getEntityNameClassMap() {
        Map<String, Class<?>> entity2dto = new HashMap<>();
        Set<Class<? extends YtDto>> allClasses = getSubclasses(YtDto.class, getReflections());
        for (Class<?> clazz : allClasses) {
            String entityName = getEntityName(clazz);
            entity2dto.put(entityName, clazz);
        }
        return entity2dto;
    }

    private String buildFieldsOfClass(Class<?> clazz, BuildFieldsContext context) {
        Map<String, Object> map = buildFieldMapOfClass(null, clazz, context);
        return map2string(map);
    }

    private Map<String, Object> buildFieldMapOfClass(Field rootField, Class<?> rootClazz, BuildFieldsContext context) {
        // Map<String, Map<String, Map<String, Map<String, ...>>>>
        Map<String, Object> fieldTree = newFieldMap();

        context.pushClass(rootClazz);

        List<Field> fields = getFieldsOfGivenAndSuperAndSubClasses(rootField, rootClazz, context);

        for (Field field : fields) {
            String fieldName = getFieldName(field);
            Map<String, Object> fieldMapOld;
            if (fieldTree.get(fieldName) instanceof Map) {
                //noinspection unchecked
                fieldMapOld = (Map<String, Object>) fieldTree.get(fieldName);
            } else {
                fieldMapOld = newFieldMap();
            }
            Class<?> clazz = getClassAssignableFromYtDto(field);
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

    private List<Field> getFieldsOfGivenAndSuperAndSubClasses(Field rootField, Class<?> rootClazz, BuildFieldsContext context) {
        List<Field> fields = FieldUtils.getAllFieldsList(rootClazz);
        List<Class<?>> subclasses = new ArrayList<>();
        YtCustomSubclasses specifier = rootField != null
                ? rootField.getAnnotation(YtCustomSubclasses.class)
                : null;
        if (specifier != null) {
            for (Class<?> clazz : specifier.value()) {
                subclasses.add(clazz);
                subclasses.addAll(getSubclasses(clazz, context.reflections));
            }
        } else {
            if (rootClazz == YtDto.class && rootField != null) {
                log.error("Field [{}] has no YtDtoFieldCustomSubclasses annotation, field's subclasses are skipped", rootField);
            } else {
                subclasses.addAll(getSubclasses(rootClazz, context.reflections));
            }
        }
        for (Class<?> subclass : subclasses) {
            List<Field> subfields = FieldUtils.getAllFieldsList(subclass);
            fields.addAll(subfields);
        }
        return fields.stream()
            .filter(field -> {
                boolean isAlwaysInclude = field.getAnnotation(YtAlwaysInclude.class) != null;
                if (isAlwaysInclude) return true;
                boolean isYtDtoField = getClassAssignableFromYtDto(field) != null;
                if (isYtDtoField) {
                    List<Class<?>> classes = new ArrayList<>();
                    classes.add(getFieldClass(field));
                    YtCustomSubclasses custom = field.getAnnotation(YtCustomSubclasses.class);
                    if (custom != null) {
                        classes.addAll(Arrays.asList(custom.value()));
                    }
                    for (Class<?> clazz : context.includeYtDtoFields) {
                        for (Class<?> other : classes) {
                            if (TypeUtils.isAssignable(clazz, other)) {
                                return true;
                            }
                        }
                    }
                    return false;
                } else {
                    return context.includeNotYtDtoFields;
                }
            })
            .distinct()
            .collect(Collectors.toList());
    }

    private Class<?> getFieldClass(Field field) {
        Class<?> fieldType = field.getType();
        if (TypeUtils.isAssignable(fieldType, Collection.class)) {
            Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                return (Class<?>) pType.getActualTypeArguments()[0];
            }
        }
        if (fieldType.isArray()) {
            return fieldType.getComponentType();
        }
        return fieldType;
    }

    private Class<?> getClassAssignableFromYtDto(Field field) {
        try {
            Class<?> fieldType = getFieldClass(field);
            if (TypeUtils.isAssignable(fieldType, YtDto.class)) {
                return fieldType;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private <T> Set<Class<? extends T>> getSubclasses(Class<T> clazz, Reflections reflections) {
        return reflections.getSubTypesOf(clazz);
    }

    private String getFieldName(Field field) {
        YtFieldName ytFieldName = field.getAnnotation(YtFieldName.class);
        if (ytFieldName != null && StringUtils.isNotEmpty(ytFieldName.value())) {
            return ytFieldName.value();
        }
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (jsonProperty != null && StringUtils.isNotEmpty(jsonProperty.value())) {
            return jsonProperty.value();
        }
        return field.getName();
    }

    private String getEntityName(Class<?> clazz) {
        YtEntityName ytEntityName = clazz.getAnnotation(YtEntityName.class);
        if (ytEntityName != null && StringUtils.isNotEmpty(ytEntityName.value())) {
            return ytEntityName.value();
        }
        String className = ClassUtils.getSimpleName(clazz);
        return className.replaceFirst("^Yt", "");
    }

    private Reflections getReflections() {
        if (reflections != null) {
            return reflections;
        }
        String packageName = ClassUtils.getPackageCanonicalName(YtDto.class);
        return reflections = new Reflections(packageName);
    }

    private Map<String, Object> joinMaps(Map<String, Object> map1, Map<String, Object> map2) {
        Map<String, Object> result = newFieldMap();
        result.putAll(map1);
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

    private Map<String, Object> newFieldMap() {
        return new TreeMap<>(Comparator.naturalOrder());
    }

    private Reflections reflections;
    private static final Logger log = LoggerFactory.getLogger(YtDtoFieldsMapperImpl.class);

    private static class BuildFieldsContext {
        final Reflections reflections;
        final boolean includeNotYtDtoFields;
        final List<Class<?>> includeYtDtoFields;
        final List<Class<?>> classStack;
        BuildFieldsContext(Reflections reflections, boolean includeNotYtDtoFields, List<Class<?>> includeYtDtoFields) {
            this.reflections = reflections;
            this.includeNotYtDtoFields = includeNotYtDtoFields;
            this.includeYtDtoFields = includeYtDtoFields;
            this.classStack = new ArrayList<>();
        }
        void pushClass(Class<?> clazz) { classStack.add(clazz); }
        void removeClass(Class<?> clazz) { classStack.remove(clazz); }
        boolean hasClass(Class<?> clazz) { return classStack.contains(clazz); }
    }
}
