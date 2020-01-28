package ru.protei.portal.core.model.query;

import org.junit.Test;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.model.util.sqlcondition.SortField;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.dict.En_SortDir.DESC;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class SqlQueryBuilderTest {

    @Test
    public void constructorWithoutArgs() throws Exception {
        Collection nullCollection = null;
        String name = query().where( "name" ).in( nullCollection ).getSqlCondition();
        assertNull(name);

        Condition condition = condition();
        assertNull( condition.getSqlCondition() );
        assertNotNull( condition.getSqlParameters() );
        assertEquals( 0, condition.getSqlParameters().size() );
    }

    @Test
    public void forUpdate() throws Exception {
        String sql = "SELECT email_last_id FROM case_object WHERE id = ? FOR UPDATE";
        Object[] args = new Object[]{441016};

        Query query = query().
        select( "email_last_id" ).from( "case_object").where("id" ).equal( 441016 ).asQuery().forUpdate();
        assertEquals( sql, query.buildSql() );
        assertArrayEquals( args,  query.asCondition().getSqlParameters().toArray() );
    }

    @Test
    public void whereExpression() throws Exception {
        String sql = "Person.name = ? AND Person.age != ? AND Person.city = ?";
        Query query = query();
        query.whereExpression( "Person.name = ? AND Person.age != ?" ).attributes("Vasya", 2)
                .where( "Person.city").equal( "Moscow" );

        Object[] args = new Object[]{"Vasya", 2, "Moscow"};
        assertEquals( sql, query.asCondition().getSqlCondition() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void whereCondition() throws Exception {
        String sql = "Person.name = ? AND Person.age != ?";
        Query query = query();
        Condition condition = condition();
        condition.and( "Person.name" ).equal( "Vasya" );
        condition.and( "Person.age" ).not().equal( 2 );

        query.where( condition );

        Object[] args = new Object[]{"Vasya", 2};
        assertEquals( sql, query.asCondition().getSqlCondition() );
        assertArrayEquals( args, query.args() );
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
    public void firstAnd() throws Exception {
        String sql = "Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        Condition condition = condition();
        condition.and( "Person.name" ).equal( firstArg );

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void firstOr() throws Exception {
        String sql = "Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        Condition condition = condition();
        condition.or( "Person.name" ).equal( firstArg );

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void secondAnd() throws Exception {
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
    public void omitFirstAnd() throws Exception {
        String sql = "SELECT * FROM Person WHERE Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        Query query = query().select( "*" ).from( "Person" ).asCondition()
                .and( "Person.name" ).equal( firstArg ).asQuery();

        assertEquals( sql, query.buildSql() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void omitFirstOr() throws Exception {
        String sql = "Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        Condition condition = condition();
        condition.or( "Person.name" ).equal( firstArg );

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }


    @Test
    public void omitFirstAndCondition() throws Exception {
        String sql = "SELECT * FROM Person WHERE (Person.name = ?) AND (Person.age = ?)";
        String firstArg = "Vasya";
        int ageArg = 12;
        Object[] args = new Object[]{firstArg, ageArg};

        Query query = query().select( "*" ).from( "Person" ).asCondition()
                .and( condition().and( "Person.name" ).equal( firstArg ) )
                .and( condition().and( "Person.age" ).equal( ageArg ) ).asQuery();

        assertEquals( sql, query.buildSql() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void omitFirstOrCondition() throws Exception {
        String sql = "SELECT * FROM Person WHERE (Person.name = ?) OR (Person.age = ?)";
        String firstArg = "Vasya";
        int ageArg = 12;
        Object[] args = new Object[]{firstArg, ageArg};

        Query query = query().select( "*" ).from( "Person" ).asCondition()
                .or( condition().or( "Person.name" ).equal( firstArg ) )
                .or( condition().or( "Person.age" ).equal( ageArg ) ).asQuery();

        assertEquals( sql, query.buildSql() );
        assertArrayEquals( args, query.args() );
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
    public void regexpTest() throws Exception {
        String expectedSql = "Person.age REGEXP ?";
        String expectedArg = "^Vasya$";

        Condition condition = condition();
        condition.and( "Person.age" ).regexp( "^Vasya$" );

        Object[] args = new Object[]{expectedArg};
        assertEquals( expectedSql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void regexpNotTest() throws Exception {
        String expectedSql = "Person.age NOT REGEXP ?";
        String expectedArg = "^Vasya$";

        Condition condition = condition();
        condition.and( "Person.age" ).not().regexp( "^Vasya$" );

        Object[] args = new Object[]{expectedArg};
        assertEquals( expectedSql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void subQuery() throws Exception {
        String sql = "Person.name = ? OR Person.id IN (SELECT id FROM Person WHERE Person.age != ?) OR Person.id IN (SELECT id FROM Person)";
        Condition condition = condition();

        condition.and( "Person.name" ).equal( "Vasya" )
                .or( "Person.id" ).in( query()
                .select( "SELECT id" ).from( "FROM Person WHERE").where( "Person.age" ).not().equal( 7 ).asQuery() )
                .or( condition().and( "Person.city").equal( null ) ) // ignore
                .or( "Person.id" ).in( query()
                .select( "id" ).from( "Person").where( "Person.city").equal( null ).asQuery() )
        ;

        Object[] args = new Object[]{"Vasya", 7};

        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void selectColumnsStrings(  ) {
        String sql = "SELECT id, name, ANY_VALUE(lastName) FROM Person WHERE Person.age = ?";

        Query query = query();
        query.select("id", "name", "ANY_VALUE(lastName)").from( "Person" ).where( "Person.age" ).equal( 7 );

        Object[] args = new Object[]{ 7 };

        assertEquals( sql, query.buildSql() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void emptySubQuery() throws Exception {
        String sql = "Person.name = ? OR Person.id IN (SELECT id FROM Person WHERE Person.age != ?) AND Person.age <= ?";
        Condition condition = condition();

        condition.and( "Person.name" ).equal( "Vasya" )
                .or( "Person.id" ).in( query()
                    .select( "SELECT id" ).from( "FROM Person WHERE" ).where( "Person.age" ).not().equal( 7 ).asQuery()
                )
                .or( condition().and( "Person.city" ).equal( null ) ) // ignore
                .and( "Person.name" ).in( query() ) // ignore
                .and( query().where( "Person.secondName" ).equal( null ) ) // ignore
                .and( "Person.age" ).le(10)
        ;

        Object[] args = new Object[]{"Vasya", 7, 10};

        assertEquals( sql, condition.getSqlCondition() );
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
                .asQuery().sort( DESC, field );

        Object[] args = new Object[]{arg1};
        assertEquals( sql, condition.getSqlCondition() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
        assertTrue( compareJdbcSort( expectedSort, condition.asQuery().getSort() ) );
    }

    @Test
    public void whereExpression_SQL() throws Exception {
        String sql = "Person.name = ? AND Person.age != ? AND Person.city = ?";
        Query query = query();
        query.whereExpression( "Person.name = ? AND Person.age != ?" ).attributes("Vasya", 2)
                .where( "Person.city").equal( "Moscow" );

        Object[] args = new Object[]{"Vasya", 2, "Moscow"};
        assertEquals( sql, query.buildSql() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void whereReplaceExpression_SQL() throws Exception {
        String sql = "Person.name = ? AND Person.age != ? AND Person.city = ?";
        Query query = query();
        query.where( "Will be replaced" ).like( "deleted" );
        query.whereExpression( "Person.name = ? AND Person.age != ?" ).attributes( "Vasya", 2 )
                .where( "Person.city" ).equal( "Moscow" );

        Object[] args = new Object[]{"Vasya", 2, "Moscow"};
        assertEquals( sql, query.buildSql() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void equalNotEqual_SQL() throws Exception {
        String sql = "Person.name = ? AND Person.age != ?";
        Condition condition = condition();
        condition.and( "Person.name" ).equal( "Vasya" );
        condition.and( "Person.age" ).not().equal( 2 );

        Object[] args = new Object[]{"Vasya", 2};
        assertEquals( sql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void firstAndTest_SQL() throws Exception {
        String sql = "Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        Condition condition = condition();
        condition.and( "Person.name" ).equal( firstArg );

        assertEquals( sql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void firstOrTest_SQL() throws Exception {
        String sql = "Person.name = ?";
        String firstArg = "Vasya";
        Object[] args = new Object[]{firstArg};

        Condition condition = condition();
        condition.or( "Person.name" ).equal( firstArg );

        assertEquals( sql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void secondAndTest_SQL() throws Exception {
        String sql = "Person.name = ? AND Person.age != ?";
        String arg1 = "Vasya";
        int arg2 = 2;

        Condition condition = condition()
                .and( "Person.name" ).equal( arg1 )
                .and( "Person.age" ).not().equal( arg2 );

        Object[] args = new Object[]{arg1, arg2};

        assertEquals( sql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void andSqlConditionTest_SQL() throws Exception {
        String expectedSql = "Person.name = ? AND (Person.age < ? OR Person.age > ?)";
        String arg1 = "Vasya";
        int arg2 = 2;
        int arg3 = 3;

        Condition condition = condition()
                .and( "Person.name" ).equal( arg1 )
                .and( condition().and( "Person.age" ).lt( arg2 ).or( "Person.age" ).gt( arg3 ) );

        Object[] args = new Object[]{arg1, arg2, arg3};

        assertEquals( expectedSql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void equalSqlConditionTest_SQL() throws Exception {
        String expectedSql = "Person.name = ? AND (Person.age != ? OR Person.age = ?)";
        String arg1 = "Vasya";
        int arg2 = 2;
        int arg3 = 3;

        Object[] args = new Object[]{arg1, arg2, arg3};

        Condition condition = condition()
                .and( "Person.name" ).equal( arg1 )
                .and( condition().and( "Person.age" ).not().equal( arg2 ).or( "Person.age" ).equal( arg3 ) );

        assertEquals( expectedSql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void isNull_ConditionTest_SQL() throws Exception {
        Boolean isNameNull = true;
        Boolean isAgeNull = true;
        Boolean isCityNull = null;

        String expectedSql = "Person.isnamenull IS NULL OR Person.age IS NULL";

        Condition condition = condition()
                .and( "Person.isnamenull" ).isNull( isNameNull )
                .or( "Person.age" ).isNull( isAgeNull )
                .or( "Person.city" ).isNull( isCityNull )
                .and( "Person.city" ).isNull( isCityNull );

        assertEquals( expectedSql, condition.asQuery().buildSql() );

    }

    @Test
    public void orSqlConditionTest_SQL() throws Exception {
        String arg1 = "Vasya";
        int arg2 = 2;
        int arg3 = 3;

        String expectedSql = "Person.name = ? OR (Person.age < ? AND Person.age > ?)";

        Condition condition = condition()
                .or( "Person.name" ).equal( arg1 )
                .or( condition().and( "Person.age" ).lt( arg2 ).and( "Person.age" ).gt( arg3 ) );

        Object[] args = new Object[]{arg1, arg2, arg3};

        assertEquals( expectedSql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void orSqlQueryTest() throws Exception {
        String arg1 = "Vasya";
        int arg2 = 2;
        int arg3 = 3;

        String expectedSql = "SELECT * FROM TEST_TABLE WHERE Person.name = ? OR (Person.age < ? AND Person.age > ?)";

        Condition condition = query().select( "*" ).from("TEST_TABLE").asCondition()
                .or( "Person.name" ).equal( arg1 )
                .or( condition().and( "Person.age" ).lt( arg2 ).and( "Person.age" ).gt( arg3 ) );

        Object[] args = new Object[]{arg1, arg2, arg3};

        assertEquals( expectedSql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void inNumbersTest_SQL() throws Exception {
        String expectedSql = "Person.age IN (2,3,4,5)";
        List<Integer> args = Arrays.asList( 2, 3, null, 4, 5 );

        Condition condition = condition();
        condition.and( "Person.age" ).in( args );

        assertEquals( expectedSql, condition.asQuery().buildSql() );
        assertNotNull( condition.getSqlParameters() );
        assertEquals( 0, condition.getSqlParameters().size() );
    }

    @Test
    public void inStringsTest_SQL() throws Exception {
        String expectedSql = "Person.age IN ('2','','4','5')";
        List<String> args = Arrays.asList( "2", "", null, "4", "5" );

        Condition condition = condition();
        condition.and( "Person.age" ).in( args );

        assertEquals( expectedSql, condition.asQuery().buildSql() );
        assertNotNull( condition.getSqlParameters() );
        assertEquals( 0, condition.getSqlParameters().size() );
    }

    @Test
    public void likeTest_SQL() throws Exception {
        String expectedSql = "Person.age LIKE ?";
        String expectedArg = "%Vasya%";

        Condition condition = condition();
        condition.and( "Person.age" ).like( "Vasya" );

        Object[] args = new Object[]{expectedArg};
        assertEquals( expectedSql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void subQuery_SQL() throws Exception {
        String sql = "Person.name = ? OR Person.id IN (SELECT id FROM Person WHERE Person.age != ?) " +
                "OR Person.id IN (SELECT id FROM Person) OR (Person.city = ?)";
        Condition condition = condition();

        condition.and( "Person.name" ).equal( "Vasya" )
                .or( "Person.id" ).in( query()
                .select( "SELECT id" ).from( "FROM Person WHERE").where( "Person.age" ).not().equal( 7 ).asQuery() )
                .or( condition().and( "Person.city").equal( null ) ) // ignore
                .or( "Person.id" ).in( query()
                .select( "id" ).from( "Person" ).where( "Person.city" ).equal( null ).asQuery() )
                .or( condition().and( "Person.city").equal( "Moscow" ) )
        ;

        Object[] args = new Object[]{"Vasya", 7, "Moscow"};

        assertEquals( sql, condition.asQuery().buildSql() );
        assertArrayEquals( args, condition.getSqlParameters().toArray() );
    }

    @Test
    public void sortOrderTest_SQL() throws Exception {
        String sqlQuery = "Person.name = ? ORDER BY company DESC, city ASC, login ASC, email ASC";
        String arg1 = "Vasya";
        String field = "name";

        Query query = query().asCondition().
                and( "Person.name" ).equal( arg1 )
                .asQuery()
                .sort( DESC, field )
                // Затирает существующий набор сортировки
                .sort( new SortField( "company", DESC ), new SortField( "city", En_SortDir.ASC ) )
                // Не затирает существующий набор сортировки
                .sort( En_SortDir.ASC, "login", "email" );

        Object[] args = new Object[]{arg1};
        assertEquals( sqlQuery, query.buildSql() );
        assertArrayEquals( args, query.args() );

    }

    @Test
    public void limit_SQL() throws Exception {
        String expectedSql = "SELECT * FROM TT LIMIT 100";

        Query condition = query().select( "*" ).from( "TT" ).limit( 100 );

        assertEquals( expectedSql, condition.buildSql() );
    }

    @Test
    public void offsetLimit() throws Exception {
        String expectedSql = "SELECT * FROM TT LIMIT 10,20";

        Query condition = query().select( "*" ).from( "TT" ).offset( 10 ).limit( 20 );
        JdbcQueryParameters build = condition.build();

        assertEquals( 10, build.getOffset() );
        assertEquals( 20, build.getLimit() );
    }

    @Test
    public void offsetLimit_SQL() throws Exception {
        String expectedSql = "SELECT * FROM TT LIMIT 10,20";

        Query condition = query().select( "*" ).from( "TT" ).offset( 10 ).limit( 20 );

        assertEquals( expectedSql, condition.buildSql() );
    }

    @Test
    public void groupBy() throws Exception {
        String sqlQuery = "Person.name = ? GROUP BY name, secondName";
        String arg1 = "Vasya";
        String field = "name";
        String field2 = "secondName";

        Query query = query()
                .where( "Person.name" ).equal( arg1 ).asQuery().groupBy( field, field2 );

        Object[] args = new Object[]{arg1};
        assertEquals( sqlQuery, query.buildSql() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void groupByDESC() throws Exception {
        String sqlQuery = "Person.name = ? GROUP BY name DESC";
        String arg1 = "Vasya";
        String field = "name DESC";

        Query query = query()
                .where( "Person.name" ).equal( arg1 ).asQuery().groupBy( field );

        Object[] args = new Object[]{arg1};
        assertEquals( sqlQuery, query.buildSql() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void groupByNull() throws Exception {
        String sqlQuery = "Person.name = ? GROUP BY name";
        String arg1 = "Vasya";
        String field = null;
        String field1 = "name";
        String field2 = null;

        Query query = query()
                .where( "Person.name" ).equal( arg1 ).asQuery().groupBy( field, field1, field2 );

        Object[] args = new Object[]{arg1};
        assertEquals( sqlQuery, query.buildSql() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void union() throws Exception {
        String expectedSql = "SELECT * FROM TT WHERE Person.name = ? GROUP BY name DESC"
                + " UNION "
                + "SELECT * FROM TT WHERE Person.name = ? GROUP BY name DESC LIMIT 10,20";
        String arg1 = "Vasya";
        String field = "name DESC";

        Query query = query().select( "*" ).from( "TT" )
                .where( "Person.name" ).equal( arg1 ).asQuery().groupBy( field );
        Query query2 = query().select( "*" ).from( "TT" )
                .where( "Person.name" ).equal( arg1 ).asQuery().groupBy( field ).offset( 10 ).limit( 20 );
        query.union( query2 );

        Object[] args = new Object[]{arg1, arg1};
        assertEquals( expectedSql, query.buildSql() );
        assertArrayEquals( args, query.args() );
    }

    @Test
    public void unionAndLimit() throws Exception {
        String expectedSql = "(SELECT * FROM TT WHERE Person.name = ? GROUP BY name DESC LIMIT 10,20)"
                + " UNION "
                + "SELECT * FROM TT WHERE Person.name = ? GROUP BY name DESC LIMIT 10,20";
        String arg1 = "Vasya";
        String field = "name DESC";

        Query query = query().select( "*" ).from( "TT" )
                .where( "Person.name" ).equal( arg1 ).asQuery().groupBy( field ).offset( 10 ).limit( 20 );
        Query query2 = query().select( "*" ).from( "TT" )
                .where( "Person.name" ).equal( arg1 ).asQuery().groupBy( field ).offset( 10 ).limit( 20 );
        query.union( query2 );

        Object[] args = new Object[]{arg1, arg1};
        assertEquals( expectedSql, query.buildSql() );
        assertArrayEquals( args, query.args() );
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
