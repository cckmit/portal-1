package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.Arrays;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class PersonSqlBuilder extends BaseSqlBuilder {

    public JdbcQueryParameters makeParameters( PersonQuery personQuery ) {
        return makeQuery( personQuery )
                .offset( personQuery.offset )
                .limit( personQuery.limit )
                .sort( personQuery.sortDir, personQuery.getSortField().getFieldName() )
                .build();
    }

    public SqlCondition createPersonSqlCondition( PersonQuery personQuery ) {
        Query query = makeQuery( personQuery );
        return new SqlCondition( query.buildSql(), Arrays.asList( query.args() ) );
    }

    private Query makeQuery( PersonQuery query){
        Condition cnd = condition()
                .and( "person.id" ).in( query.getPersonIds() )
                .and( "person.company_id" ).in( query.getCompanyIds() )
                .and( condition()
                        .or( "person.displayName" ).like( query.getSearchString() )
                )
                .and( "person.isfired" ).equal( booleanAsNumber( query.getFired() ) )
                .and( "person.isdeleted" ).equal( booleanAsNumber( query.getDeleted() ) )
                .and( "person.sex" ).not( query.getPeople() ).equal( query.getPeople() == null ? null : En_Gender.UNDEFINED.getCode() );

        if (query.getHasCaseFilter() != null) {
            cnd.and( "person.id" ).in( query()
                    .select( "distinct(person_id)" ).from( "person_to_case_filter" ) );
        }

        return cnd.asQuery();
    }
}
