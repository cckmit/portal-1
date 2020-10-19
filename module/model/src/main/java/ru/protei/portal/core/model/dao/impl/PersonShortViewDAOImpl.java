package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PersonShortViewDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class PersonShortViewDAOImpl extends PortalBaseJdbcDAO<PersonShortView> implements PersonShortViewDAO {

    @Override
    public List<PersonShortView> getPersonsShortView( PersonQuery query ) {
        return getList( makeQuery( query ).build() );
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

        cnd.asQuery()
                .offset( query.offset )
                .limit( query.limit )
                .sort( query.sortDir, query.getSortField().getFieldName() );


        return cnd.asQuery();
    }
}
