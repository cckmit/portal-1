package ru.protei.portal.core.model.query;

import org.junit.Test;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.dict.En_SortDir.DESC;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class SqlConditionBuilderTest {

    @Test
    public void constructorWithoutArgs() throws Exception {
        SqlConditionBuilder condition = new SqlConditionBuilder();
        assertEquals( "TRUE", condition.getSqlCondition() );
        assertNotNull( condition.getSqlParameters() );
        assertEquals( 0, condition.getSqlParameters().size() );
    }

    @Test
    public void equalNotEqual() throws Exception {
        String sql = "Person.name = ? AND Person.age != ?";
        Object[] args = new Object[]{"Vasya", 2};
        SqlConditionBuilder condition = new SqlConditionBuilder();
        condition.and( "Person.name" ).equal( "Vasya" );
        condition.and( "Person.age" ).not().equal( 2 );

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void firstAndTest() throws Exception {
        String sql = "Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        SqlConditionBuilder condition = new SqlConditionBuilder();
        condition.and( "Person.name" ).equal( firstArg );

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void firstOrTest() throws Exception {
        String sql = "Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        SqlConditionBuilder condition = new SqlConditionBuilder();
        condition.or( "Person.name" ).equal( firstArg );

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void secondAndTest() throws Exception {
        String sql = "Person.name = ? AND Person.age != ?";
        String arg1 = "Vasya";
        int arg2 = 2;

        SqlConditionBuilder condition = new SqlConditionBuilder()
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

        SqlConditionBuilder condition = new SqlConditionBuilder()
                .and( "Person.name" ).equal( arg1 )
                .and( new SqlConditionBuilder().and( "Person.age" ).lt( arg2 ).or( "Person.age" ).gt( arg3 ) );

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

        SqlConditionBuilder condition = new SqlConditionBuilder()
                .and( "Person.name" ).equal( arg1 )
                .and( new SqlConditionBuilder().and( "Person.age" ).not().equal( arg2 ).or( "Person.age" ).equal( arg3 ) );

        assertEquals( expectedSql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void isNull_ConditionTest() throws Exception {
        Boolean isNameNull = true;
        Boolean isAgeNull = true;
        Boolean isCityNull = null;

        String expectedSql = "Person.isnamenull IS NULL OR Person.age IS NULL";

        SqlConditionBuilder condition = new SqlConditionBuilder()
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

        SqlConditionBuilder condition = new SqlConditionBuilder()
                .or( "Person.name" ).equal( arg1 )
                .or( new SqlConditionBuilder().and( "Person.age" ).lt( arg2 ).and( "Person.age" ).gt( arg3 ) );

        Object[] args = new Object[]{arg1, arg2, arg3};

        assertEquals( expectedSql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void inNumbersTest() throws Exception {
        String expectedSql = "Person.age IN (2,3,4,5)";
        List<Integer> args = Arrays.asList( 2, 3, null, 4, 5 );

        SqlConditionBuilder condition = new SqlConditionBuilder();
        condition.and( "Person.age" ).in( args );

        assertEquals( expectedSql, condition.getSqlCondition() );
        assertNotNull( condition.getSqlParameters() );
        assertEquals( 0, condition.getSqlParameters().size() );
    }

    @Test
    public void inStringsTest() throws Exception {
        String expectedSql = "Person.age IN ('2','','4','5')";
        List<String> args = Arrays.asList( "2", "", null, "4", "5" );

        SqlConditionBuilder condition = new SqlConditionBuilder();
        condition.and( "Person.age" ).in( args );

        assertEquals( expectedSql, condition.getSqlCondition() );
        assertNotNull( condition.getSqlParameters() );
        assertEquals( 0, condition.getSqlParameters().size() );
    }

    @Test
    public void likeTest() throws Exception {
        String expectedSql = "Person.age LIKE ?";
        String expectedArg = "%Vasya%";

        SqlConditionBuilder condition = new SqlConditionBuilder();
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

        SqlConditionBuilder condition = new SqlConditionBuilder();
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
