package ru.protei.portal.core.model.helper;

import org.junit.Test;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.DiffResult;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    @Test
    public void diffCollectionWithElements() {
        Holder<String> same1 = new Holder( 1, "same1" );
        Holder<String> same2 = new Holder( 2, "same2" );
        Holder<String> added1 = new Holder( 3, "added1" );
        Holder<String> added2 = new Holder( 4, "added2" );
        Holder<String> removed1 = new Holder( 5, "removed1" );
        Holder<String> removed2 = new Holder( 6, "removed2" );
        Holder<String> changed1init = new Holder( 7, "changed1init" );
        Holder<String> changed1new = new Holder( 7, "changed1new" );
        Holder<String> changed2init = new Holder( 8, "changed2init" );
        Holder<String> changed2new = new Holder( 8, "changed2new" );

        List<Holder<String>> initial = listOf( same1, removed1, removed2, same2, changed1init, changed2init );
        List<Holder<String>> changed = listOf( same1, same2, added1, added2, changed1new, changed2new );

        DiffCollectionResult<Holder<String>> diff = CollectionUtils.diffCollection( initial, changed,
                ( o1, o2 ) -> Objects.equals( o1.data, o2.data ) ? 0 : -1 );

        assertNotNull( "Expected differences", diff );
        assertEquals( "Expected same added", listOf( added1, added2 ), diff.getAddedEntries() );
        assertEquals( "Expected same removed", listOf( removed1, removed2 ), diff.getRemovedEntries() );
        assertEquals( "Expected same same", listOf( same1, same2 ), diff.getSameEntries() );

        DiffResult<Holder<String>> df1 = new DiffResult<Holder<String>>();
        df1.setInitialState( changed1init );
        df1.setNewState( changed1new );

        DiffResult<Holder<String>> df2 = new DiffResult<Holder<String>>();
        df2.setInitialState( changed2init );
        df2.setNewState( changed2new );

        assertEquals( "Expected same changed", listOf( df1, df2 ), diff.getChangedEntries() );
    }

    class Holder<T>{
        public Holder( int id, T data ) {
            this.id = id;
            this.data = data;
        }

        T data;
        int id;

        @Override
        public boolean equals( Object o ) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Holder<?> holder = (Holder<?>) o;
            return id == holder.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash( id );
        }

        @Override
        public String toString() {
            return String.valueOf( data );
        }
    }
}