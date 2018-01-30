package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by michael on 01.04.16.
 */
public class CompanyDAO_Impl extends PortalBaseJdbcDAO<Company> implements CompanyDAO {

    @Override
    public List<Company> getListByQuery(CompanyQuery query) {
        return listByQuery(query);
    }

    @Override
    public Company getCompanyByName( String name ) {
        return getByCondition(" cname=? ", name);
    }


    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CompanyQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("cname like ?");
            args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));

            if (query.getGroupId() != null && query.getGroupId()> 0) {
                condition.append(" and groupId = ?");
                args.add(query.getGroupId());
            }

            if (query.getCategoryIds() != null && !query.getCategoryIds().isEmpty()) {
                condition.append(" and category_id in (")
                        .append(
                                query.getCategoryIds().stream().map(Object::toString).collect(Collectors.joining(","))
                        )
                        .append(")");
            }

            if ( query.isExcludeHomeCompanies() ) {
                condition.append( " and company.id not in ( select companyId from company_group_home )" );
            } else {
                condition.append( " and company.id not in ( select companyId from company_group_home where mainId is not null )" );
            }
        });
    }

}
