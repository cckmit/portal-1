package ru.protei.portal.test.youtrack;

import org.junit.Test;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapperImpl;
import ru.protei.portal.core.model.youtrack.dto.*;

import static org.junit.Assert.assertEquals;

public class YtFieldMapperTest {

    @Test
    public void testSimpleEmpty() {
        String fields = getMapper().getFields(YtDtoSimple.class, false);
        String expected = "$type,id";
        assertEquals(expected, fields);
    }

    @Test
    public void testSimpleFull() {
        String fields = getMapper().getFields(YtDtoSimple.class, true);
        String expected = "$type,a,b,c,id";
        assertEquals(expected, fields);
    }

    @Test
    public void testComplexEmpty() {
        String fields = getMapper().getFields(YtDtoComplex.class, false);
        String expected = "$type,id";
        assertEquals(expected, fields);
    }

    @Test
    public void testComplexPartialPlain() {
        String fields = getMapper().getFields(YtDtoComplex.class, true);
        String expected = "$type,aa,bb,cc,ee,id";
        assertEquals(expected, fields);
    }

    @Test
    public void testComplexPartialDto() {
        String fields = getMapper().getFields(YtDtoComplex.class, false, YtDtoSimple.class);
        String expected = "$type,dd($type,id),ff($type,id),id";
        assertEquals(expected, fields);
    }

    @Test
    public void testComplexFull() {
        String fields = getMapper().getFields(YtDtoComplex.class, true, YtDtoSimple.class);
        String expected = "$type,aa,bb,cc,dd($type,a,b,c,id),ee,ff($type,a,b,c,id),id";
        assertEquals(expected, fields);
    }

    @Test
    public void testFirstLayer() {
        String fields = getMapper().getFields(YtDtoFirstLayer.class, true, YtDtoSimple.class);
        String expected = "$type,a,b,c,d,e,f($type,a,b,c,id),g,h,id";
        assertEquals(expected, fields);
    }

    @Test
    public void testSecondLayer() {
        String fields = getMapper().getFields(YtDtoSecondLayer.class, true, YtDtoSimple.class);
        String expected = "$type,a,b,c,d,e,f($type,a,b,c,id),g,h,id";
        assertEquals(expected, fields);
    }

    @Test
    public void testThirdLayer() {
        String fields = getMapper().getFields(YtDtoThirdLayer.class, true, YtDtoSimple.class);
        String expected = "$type,a,b,c,d,e,f($type,a,b,c,id),g,h,id";
        assertEquals(expected, fields);
    }

    @Test
    public void testMerge() {
        String fields = getMapper().getFields(YtDtoMerge.class, true, YtDtoMerge1.class, YtDtoMerge1.YtDtoMerge1Content.class, YtDtoMerge2.class, YtDtoMerge2.YtDtoMerge2Content.class);
        String expected = "$type,a,b,c($type,a,b,id),id";
        assertEquals(expected, fields);
    }

    @Test
    public void testFieldNameAnnotation() {
        String fields = getMapper().getFields(YtDtoWithFieldNameAnnotation.class, true);
        String expected = "$type,a,b,c,id";
        assertEquals(expected, fields);
    }

    @Test
    public void testAlwaysIncludeAnnotation() {
        String fields = getMapper().getFields(YtDtoWithAlwaysIncludeAnnotation.class, false);
        String expected = "$type,c,id";
        assertEquals(expected, fields);
    }

    @Test
    public void testCustomSubclassesAnnotation() {
        String fields = getMapper().getFields(YtDtoWithCustomSubclassesAnnotation.class, true, YtDtoWithCustomSubclassesAnnotation.YtInner1.class, YtDtoWithCustomSubclassesAnnotation.YtInner2.class);
        String expected = "$type,a,b,c($type,aa,bb,cc,dd,ee,id),id";
        assertEquals(expected, fields);
    }

    private YtDtoFieldsMapper getMapper() {
        return new YtDtoFieldsMapperImpl();
    }
}
