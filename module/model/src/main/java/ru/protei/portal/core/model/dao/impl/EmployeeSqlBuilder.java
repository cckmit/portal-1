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

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append(" and Person.displayName like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }

            if (HelperFunc.isLikeRequired(query.getWorkPhone())) {
                condition.append(" and JSON_SEARCH(Person.contactInfo, 'one', ?, '', substr(JSON_UNQUOTE(JSON_SEARCH(person.contactInfo, 'one','Рабочий')),1,10)) is not null");
                args.add(HelperFunc.makeLikeArg(query.getWorkPhone(), true));
            }

            if (HelperFunc.isLikeRequired(query.getMobilePhone())) {
                condition.append(" and JSON_SEARCH(Person.contactInfo, 'one', ?, '', substr(JSON_UNQUOTE(JSON_SEARCH(person.contactInfo, 'one','MOBILE_PHONE')),1,10)) is not null");
                args.add(HelperFunc.makeLikeArg(query.getMobilePhone(), true));
            }

            if (HelperFunc.isLikeRequired(query.getIpAddress())) {
                condition.append(" and Person.ipaddress like ?");
                args.add(HelperFunc.makeLikeArg(query.getIpAddress(), true));
            }
        });
    }
}
