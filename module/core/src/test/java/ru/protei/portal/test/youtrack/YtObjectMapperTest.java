package ru.protei.portal.test.youtrack;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapperImpl;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider;
import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;
import ru.protei.portal.core.model.youtrack.dto.YtDtoComplex;
import ru.protei.portal.core.model.youtrack.dto.YtDtoSimple;
import ru.protei.portal.core.model.youtrack.dto.YtDtoWithEntityNameAnnotation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class YtObjectMapperTest {

    @Test
    public void testEmpty() throws JsonProcessingException {
        YtDtoSimple simple = new YtDtoSimple();
        String actual = serialize(simple);
        String expected = "{\"$type\":\"DtoSimple\"}";
        assertEquals(expected, actual);
    }

    @Test
    public void testPartial() throws JsonProcessingException {
        YtDtoSimple simple = new YtDtoSimple();
        simple.a = "FA";
        simple.c = "FC";
        String actual = serialize(simple);
        String expected = "{\"$type\":\"DtoSimple\",\"a\":\"FA\",\"c\":\"FC\"}";
        assertEquals(expected, actual);
    }

    @Test
    public void testFull() throws JsonProcessingException {
        YtDtoSimple simple = new YtDtoSimple();
        simple.a = "FA";
        simple.b = "FB";
        simple.c = "FC";
        String actual = serialize(simple);
        String expected = "{\"$type\":\"DtoSimple\",\"a\":\"FA\",\"b\":\"FB\",\"c\":\"FC\"}";
        assertEquals(expected, actual);
    }

    @Test
    public void testNull() throws JsonProcessingException {
        YtDtoSimple simple = new YtDtoSimple();
        simple.a = "FA";
        simple.b = null;
        String actual = serialize(simple, new YtFieldDescriptor(YtDtoSimple.class, "b"));
        String expected = "{\"$type\":\"DtoSimple\",\"a\":\"FA\",\"b\":null}";
        assertEquals(expected, actual);
    }

    @Test
    public void testDeserializeEmpty() throws IOException {
        YtDtoSimple simple = deserialize("{\"$type\":\"DtoSimple\"}", YtDtoSimple.class);
        assertNotNull(simple);
    }

    @Test
    public void testDeserializePartial() throws IOException {
        YtDtoSimple simple = deserialize("{\"$type\":\"DtoSimple\",\"a\":\"FA\",\"c\":\"FC\"}", YtDtoSimple.class);
        assertNotNull(simple);
        assertEquals("FA", simple.a);
        assertNull(simple.b);
        assertEquals("FC", simple.c);
    }

    @Test
    public void testDeserializeFull() throws IOException {
        YtDtoSimple simple = deserialize("{\"$type\":\"DtoSimple\",\"a\":\"FA\",\"b\":\"FB\",\"c\":\"FC\"}", YtDtoSimple.class);
        assertNotNull(simple);
        assertEquals("FA", simple.a);
        assertEquals("FB", simple.b);
        assertEquals("FC", simple.c);
    }

    @Test
    public void testDeserializeWrong() throws IOException {
        YtDtoSimple simple = deserialize("{\"$type\":\"DtoNotExists\",\"a\":\"FA\",\"b\":\"FB\",\"c\":\"FC\"}", YtDtoSimple.class);
        assertNull(simple);
    }

    @Test
    public void testDeserializeComplex() throws IOException {
        YtDtoComplex complex = deserialize("{" +
                "\"$type\":\"DtoComplex\"," +
                "\"aa\":\"FAA\"," +
                "\"cc\":\"FCC\"," +
                "\"dd\":{\"$type\":\"DtoSimple\",\"a\":\"FA0\",\"b\":\"FB0\",\"c\":\"FC0\"}," +
                "\"ee\":\"FEE\"," +
                "\"ff\":[" +
                    "{\"$type\":\"DtoSimple\",\"a\":\"FA1\",\"b\":\"FB1\",\"c\":\"FC1\"}," +
                    "{\"$type\":\"DtoSimple\",\"a\":\"FA2\",\"b\":\"FB2\",\"c\":\"FC2\"}" +
                "]}",
                YtDtoComplex.class
        );
        assertNotNull(complex);
        assertEquals("FAA", complex.aa);
        assertNull(complex.bb);
        assertEquals("FCC", complex.cc);
        assertNotNull(complex.dd);
        assertEquals("FA0", complex.dd.a);
        assertEquals("FB0", complex.dd.b);
        assertEquals("FC0", complex.dd.c);
        assertEquals("FEE", complex.ee);
        assertNotNull(complex.ff);
        assertEquals(2, complex.ff.size());
        assertEquals("FA1", complex.ff.get(0).a);
        assertEquals("FB1", complex.ff.get(0).b);
        assertEquals("FC1", complex.ff.get(0).c);
        assertEquals("FA2", complex.ff.get(1).a);
        assertEquals("FB2", complex.ff.get(1).b);
        assertEquals("FC2", complex.ff.get(1).c);
    }

    @Test
    public void testEntityNameAnnotationSerialization() throws IOException {
        YtDtoWithEntityNameAnnotation dto = new YtDtoWithEntityNameAnnotation();
        dto.a = "FA";
        dto.b = "FB";
        String actual = serialize(dto);
        String expected = "{\"$type\":\"OverrideName\",\"a\":\"FA\",\"b\":\"FB\"}";
        assertEquals(expected, actual);
    }

    @Test
    public void testEntityNameAnnotationDeserialization() throws IOException {
        YtDtoWithEntityNameAnnotation dto = deserialize("{\"$type\":\"OverrideName\",\"a\":\"FA\",\"b\":\"FB\"}", YtDtoWithEntityNameAnnotation.class);
        assertNotNull(dto);
        assertEquals("FA", dto.a);
        assertEquals("FB", dto.b);
    }

    private <T> String serialize(T dto, YtFieldDescriptor...forceIncludeFields) throws JsonProcessingException {
        List<YtFieldDescriptor> includeFields = forceIncludeFields == null
                ? Collections.emptyList()
                : Arrays.asList(forceIncludeFields);
        return YtDtoObjectMapperProvider.getMapper(getFieldMapper())
                .writer(YtDtoObjectMapperProvider.getFilterProvider(includeFields))
                .writeValueAsString(dto);
    }

    private <T> T deserialize(String json, Class<T> clazz) throws IOException {
        return YtDtoObjectMapperProvider.getMapper(getFieldMapper())
                .readValue(json, clazz);
    }

    private YtDtoFieldsMapper getFieldMapper() {
        return new YtDtoFieldsMapperImpl();
    }
}
