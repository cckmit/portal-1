package ru.protei.portal.core.model.helper;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;

public class StringUtilsTest {

    @Test
    public void joinNotNulls(){
        List<Object> nulls = listOf( null, null, null );
        String emptyString = StringUtils.join( nulls, "," );
        assertNotNull("expected empty string", emptyString );
        assertTrue( "expected empty string", emptyString.isEmpty());
    }

    @Test
    public void joinNotAddDelimiterAfterNulls(){
        List<Object> nulls = listOf( null, "A", null, "B", null, "C", null );
        String emptyString = StringUtils.join( nulls, "," );
        assertNotNull("expected string with data", emptyString );
        assertTrue( "expected string with data", !emptyString.isEmpty());
        assertEquals( "expected without nulls", "A,B,C", emptyString);
    }

}