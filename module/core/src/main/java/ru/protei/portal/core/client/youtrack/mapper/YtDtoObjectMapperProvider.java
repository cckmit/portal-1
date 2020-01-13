package ru.protei.portal.core.client.youtrack.mapper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import ru.protei.portal.core.model.youtrack.dto.YtDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class YtDtoObjectMapperProvider {

    private static final String FILTER_NAME = "exclude-nulls-except-exclusions";
    private static final String TYPE_PROPERTY = "$type";

    public static ObjectMapper getMapper(YtDtoFieldsMapper fieldsMapper) {

        List<NamedType> subtypes = makeSubtypes(fieldsMapper);

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.setAnnotationIntrospectors(new JacksonAnnotationIntrospector() {
            public Object findFilterId(Annotated a) { return FILTER_NAME; }
        }, new JacksonAnnotationIntrospector());
        mapper.registerSubtypes(subtypes.toArray(new NamedType[0]));
        mapper.setDefaultTyping(makeTypeResolverBuilder(subtypes));
        return mapper;
    }

    public static FilterProvider getFilterProvider(List<String> forceIncludeFields) {
        SimpleFilterProvider filters = new SimpleFilterProvider();
        filters.addFilter(YtDtoObjectMapperProvider.FILTER_NAME, new SimpleBeanPropertyFilter() {
            @Override
            public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
                if (!include(writer)) {
                    return;
                }
                String field = writer.getName();
                if (forceIncludeFields.contains(field)) {
                    writer.serializeAsField(pojo, jgen, provider);
                    return;
                }
                if (pojo == null) {
                    return;
                }
                Object value = FieldUtils.readField(pojo, field);
                if (value == null) {
                    return;
                }
                writer.serializeAsField(pojo, jgen, provider);
            }
        });
        return filters;
    }

    private static List<NamedType> makeSubtypes(YtDtoFieldsMapper fieldsMapper) {
        List<NamedType> subtypes = new ArrayList<>();
        Map<String, Class<? extends YtDto>> entityNameClassMap = fieldsMapper.getEntityNameClassMap();
        for (Map.Entry<String, Class<? extends YtDto>> entry : entityNameClassMap.entrySet()) {
            subtypes.add(new NamedType(entry.getValue(), entry.getKey()));
        }
        return subtypes;
    }

    private static TypeResolverBuilder<?> makeTypeResolverBuilder(List<NamedType> subtypes) {
        TypeResolverBuilder<?> typer = new CustomTypeResolverBuilder(subtypes);
        typer = typer.init(JsonTypeInfo.Id.NAME, null);
        typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
        typer = typer.typeProperty(TYPE_PROPERTY);
        typer = typer.typeIdVisibility(true);
        typer = typer.defaultImpl(Void.class);
        return typer;
    }

    private static class CustomTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

        private final Collection<NamedType> subtypes;

        public CustomTypeResolverBuilder(Collection<NamedType> subtypes) {
            super(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
            this.subtypes = subtypes;
        }

        @Override
        public boolean useForType(JavaType t) {
            return TypeUtils.isAssignable(t.getRawClass(), YtDto.class);
        }

        @Override
        protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
            return super.idResolver(
                    config,
                    baseType,
                    CollectionUtils.union(
                        CollectionUtils.emptyIfNull(subtypes),
                        CollectionUtils.emptyIfNull(this.subtypes)
                    ),
                    forSer,
                    forDeser
            );
        }
    }
}
