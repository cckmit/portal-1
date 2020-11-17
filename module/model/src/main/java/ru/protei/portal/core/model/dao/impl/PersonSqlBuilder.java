package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.winter.jdbc.JdbcQueryParameters;

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

        if (query.getEmail() != null) {
            cnd.and( "person.id" ).in( query()
                    .select( "cip.person_id" ).from( "contact_item_person AS cip" )
                    .where( "cip.contact_item_id" ).in( query()
                            .select( "ci.id" ).from( "contact_item AS ci" )
                            .where( "ci.item_type" ).equal( En_ContactItemType.EMAIL.getId() )
                            .and( "ci.value" ).equal( query.getEmail() ).asQuery()
                    ).asQuery()
            );
        }

        return cnd.asQuery();
    }
}
