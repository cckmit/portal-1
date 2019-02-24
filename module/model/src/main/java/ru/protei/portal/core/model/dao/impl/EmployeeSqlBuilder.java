package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.collections.CollectionUtils;

import java.util.stream.Collectors;

public class EmployeeSqlBuilder {

    public SqlCondition createSqlCondition( EmployeeQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("Person.company_id in (select companyId from company_group_home)");

            if (query.getFired() != null) {
                condition.append(" and Person.isfired=?");
                args.add(query.getFired() ? 1 : 0);
            }

            if (query.getDeleted() != null) {
                condition.append(" and Person.isdeleted=?");
                args.add(query.getDeleted() ? 1 : 0);
            }

            if (query.getOnlyPeople() != null) {
                condition.append(" and Person.sex != ?");
                args.add(En_Gender.UNDEFINED.getCode());
            }

            if (!CollectionUtils.isEmpty(query.getHomeCompanies())) {
                condition.append(" and WE.companyId in (")
                        .append(query.getHomeCompanies().stream().map(option -> option.getId().toString()).collect( Collectors.joining(",")))
                        .append(")");
            }

            if ( HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append(" and (Person.displayName like ? or JSON_EXTRACT(Person.contactInfo, '$.items[*].v') like ? or Person.ipaddress like ?)");
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
                args.add(likeArg);
            }
        });
    }
}
