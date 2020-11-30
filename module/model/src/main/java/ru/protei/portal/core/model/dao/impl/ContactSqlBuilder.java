package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.CompanyHomeGroupItem;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class ContactSqlBuilder extends BaseSqlBuilder {

    @Autowired
    CompanyGroupHomeDAO groupHomeDAO;

    public JdbcQueryParameters makeParameters( ContactQuery query ) {
        return makeQuery( query )
                .offset( query.offset )
                .limit( query.limit )
                .sort( query.sortDir, query.getSortField().getFieldName() )
                .build();
    }

    public SqlCondition createPersonSqlCondition( ContactQuery personQuery ) {
        Query query = makeQuery( personQuery );
        return new SqlCondition( query.buildSql(), Arrays.asList( query.args() ) );
    }

    private Query makeQuery( ContactQuery query ) {
        Condition cnd = condition()
                .and( "person.company_id" ).not().in( buildHomeCompanyFilter() )
                .and( "person.company_id" ).equal( query.getCompanyId() )
                .and( "person.isfired" ).equal( booleanAsNumber( query.getFired() ) )
                .and( "person.isdeleted" ).equal( booleanAsNumber( query.getDeleted() ) )
                .and( condition()
                        .or( "person.displayName" ).like( query.getSearchString() )
                        .or( "person.displayName" ).like( query.getAlternativeSearchString() )
                );
        return cnd.asQuery();
    }

    private Query buildHomeCompanyFilter() {
        return query().select( CompanyHomeGroupItem.Columns.COMPANY_ID ).from( "company_group_home" );
    }

}
