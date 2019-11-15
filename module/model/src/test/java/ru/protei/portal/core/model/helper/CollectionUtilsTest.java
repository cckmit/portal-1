package ru.protei.portal.core.model.helper;

import org.junit.Test;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.List;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;

public class CollectionUtilsTest {


    @Test
    public void diffCollection() {
        String same1 =  "same1";
        String same2 =  "same2";
        String added1 =  "added1";
        String added2 =  "added2";
        String removed1 =  "removed1";
        String removed2 =  "removed2";

        List<String> initial = listOf( same1,removed1, removed2, same2  );
        List<String> changed = listOf( same1, same2, added1, added2 );

        DiffCollectionResult<String> diff = CollectionUtils.diffCollection( initial, changed );

        assertNotNull( "Expected differences", diff );
        assertEquals(  "Expected same added", listOf(added1, added2), diff.getAddedEntries() );
        assertEquals(  "Expected same removed", listOf(removed1, removed2), diff.getRemovedEntries() );
        assertEquals(  "Expected same same", listOf(same1, same2), diff.getSameEntries() );

    }
}