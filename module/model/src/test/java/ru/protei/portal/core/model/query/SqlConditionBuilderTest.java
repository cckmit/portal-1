package ru.protei.portal.core.model.query;

import org.junit.Test;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.dict.En_SortDir.DESC;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;
import static ru.protei.portal.core.model.util.sqlcondition.SqlConditionBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlConditionBuilder.query;

public class SqlConditionBuilderTest {

    @Test
    public void constructorWithoutArgs() throws Exception {
        Condition condition = condition();
        assertEquals( "TRUE", condition.getSqlCondition() );
        assertNotNull( condition.getSqlParameters() );
        assertEquals( 0, condition.getSqlParameters().size() );
    }

    @Test
    public void subQuery() throws Exception {
        String sql = "Person.name = ? OR Person.id IN (SELECT id FROM Person WHERE Person.age != ?) OR Person.id IN (SELECT id FROM Person WHERE TRUE)";
        Condition condition = condition();

        condition.and( "Person.name" ).equal( "Vasya" )
                .or( "Person.id" ).in( query()
                    .select( "SELECT id" ).from( "FROM Person WHERE").and( "Person.age" ).not().equal( 7 ) )
                .or( condition().and( "Person.city").equal( null ) ) // ignore
                .or( "Person.id" ).in( query()
                        .select( "id" ).from( "Person").and( "Person.city").equal( null ) ) 
        ;

        Object[] args = new Object[]{"Vasya", 7};

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }


    @Test
    public void equalNotEqual() throws Exception {
        String sql = "Person.name = ? AND Person.age != ?";
        Condition condition = condition();
        condition.and( "Person.name" ).equal( "Vasya" );
        condition.and( "Person.age" ).not().equal( 2 );

        Object[] args = new Object[]{"Vasya", 2};
        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void firstAndTest() throws Exception {
        String sql = "Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        Condition condition = condition();
        condition.and( "Person.name" ).equal( firstArg );

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void firstOrTest() throws Exception {
        String sql = "Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        Condition condition = condition();
        condition.or( "Person.name" ).equal( firstArg );

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void secondAndTest() throws Exception {
        String sql = "Person.name = ? AND Person.age != ?";
        String arg1 = "Vasya";
        int arg2 = 2;

        Condition condition = condition()
                .and( "Person.name" ).equal( arg1 )
                .and( "Person.age" ).not().equal( arg2 );

        Object[] args = new Object[]{arg1, arg2};

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void andSqlConditionTest() throws Exception {
        String expectedSql = "Person.name = ? AND (Person.age < ? OR Person.age > ?)";
        String arg1 = "Vasya";
        int arg2 = 2;
        int arg3 = 3;

        Condition condition = condition()
                .and( "Person.name" ).equal( arg1 )
                .and( condition().and( "Person.age" ).lt( arg2 ).or( "Person.age" ).gt( arg3 ) );

        Object[] args = new Object[]{arg1, arg2, arg3};

        assertEquals( expectedSql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void equalSqlConditionTest() throws Exception {
        String expectedSql = "Person.name = ? AND (Person.age != ? OR Person.age = ?)";
        String arg1 = "Vasya";
        int arg2 = 2;
        int arg3 = 3;

        Object[] args = new Object[]{arg1, arg2, arg3};

        Condition condition = condition()
                .and( "Person.name" ).equal( arg1 )
                .and( condition().and( "Person.age" ).not().equal( arg2 ).or( "Person.age" ).equal( arg3 ) );

        assertEquals( expectedSql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void isNull_ConditionTest() throws Exception {
        Boolean isNameNull = true;
        Boolean isAgeNull = true;
        Boolean isCityNull = null;

        String expectedSql = "Person.isnamenull IS NULL OR Person.age IS NULL";

        Condition condition = condition()
                .and( "Person.isnamenull" ).isNull( isNameNull )
                .or( "Person.age" ).isNull( isAgeNull )
                .or( "Person.city" ).isNull( isCityNull )
                .and( "Person.city" ).isNull( isCityNull );

        assertEquals( expectedSql, condition.getSqlCondition() );

    }

    @Test
    public void orSqlConditionTest() throws Exception {
        String arg1 = "Vasya";
        int arg2 = 2;
        int arg3 = 3;

        String expectedSql = "Person.name = ? OR (Person.age < ? AND Person.age > ?)";

        Condition condition = condition()
                .or( "Person.name" ).equal( arg1 )
                .or( condition().and( "Person.age" ).lt( arg2 ).and( "Person.age" ).gt( arg3 ) );

        Object[] args = new Object[]{arg1, arg2, arg3};

        assertEquals( expectedSql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void inNumbersTest() throws Exception {
        String expectedSql = "Person.age IN (2,3,4,5)";
        List<Integer> args = Arrays.asList( 2, 3, null, 4, 5 );

        Condition condition = condition();
        condition.and( "Person.age" ).in( args );

        assertEquals( expectedSql, condition.getSqlCondition() );
        assertNotNull( condition.getSqlParameters() );
        assertEquals( 0, condition.getSqlParameters().size() );
    }

    @Test
    public void inStringsTest() throws Exception {
        String expectedSql = "Person.age IN ('2','','4','5')";
        List<String> args = Arrays.asList( "2", "", null, "4", "5" );

        Condition condition = condition();
        condition.and( "Person.age" ).in( args );

        assertEquals( expectedSql, condition.getSqlCondition() );
        assertNotNull( condition.getSqlParameters() );
        assertEquals( 0, condition.getSqlParameters().size() );
    }

    @Test
    public void likeTest() throws Exception {
        String expectedSql = "Person.age LIKE ?";
        String expectedArg = "%Vasya%";

        Condition condition = condition();
        condition.and( "Person.age" ).like( "Vasya" );

        Object[] args = new Object[]{expectedArg};
        assertEquals( expectedSql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void sortOrderTest() throws Exception {
        String sql = "Person.name = ?";
        String arg1 = "Vasya";
        String field = "name";

        JdbcSort expectedSort = new JdbcSort( JdbcSort.Direction.DESC, field );

        Condition condition = condition();
        condition.and( "Person.name" ).equal( arg1 )
                .sort( DESC, field );

        Object[] args = new Object[]{arg1};
        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
        assertTrue( compareJdbcSort( expectedSort, condition.getSort() ) );
    }

    private boolean compareJdbcSort( JdbcSort expectedSort, JdbcSort sortOrder ) {
        if (expectedSort == sortOrder) return true;
        if (expectedSort.getParams() == sortOrder.getParams()) return true;
        assertEquals( "expected equals size", size( expectedSort.getParams() ), size( sortOrder.getParams() ));
        for (int i = 0; i < size( expectedSort.getParams() ); i++) {
            JdbcSort.DescriptionParam expectedParam = expectedSort.getDescriptionParams().get( i );
            JdbcSort.DescriptionParam orderParam = sortOrder.getDescriptionParams().get( i );

            assertEquals( expectedParam.paramName, orderParam.paramName );
            assertEquals( expectedParam.direction, orderParam.direction );
            assertEquals( expectedParam.mode, orderParam.mode );
        }
        return true;
    }

}
