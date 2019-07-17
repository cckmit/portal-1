package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class EmployeeSqlBuilder {

    public SqlCondition createSqlCondition(EmployeeQuery query) {
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

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append(" and Person.displayName like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString().trim(), true));
            }

            if (HelperFunc.isLikeRequired(query.getWorkPhone())) {
                condition.append(" and info.a = 'PUBLIC' and info.t = 'GENERAL_PHONE' and info.v like ?");
                args.add(HelperFunc.makeLikeArg(query.getWorkPhone(), true));
            }

            if (HelperFunc.isLikeRequired(query.getMobilePhone())) {
                condition.append(" and info.a = 'PUBLIC' and info.t = 'MOBILE_PHONE' and info.v like ?");
                args.add(HelperFunc.makeLikeArg(query.getMobilePhone(), true));
            }

            if (HelperFunc.isLikeRequired(query.getIpAddress())) {
                condition.append(" and Person.ipaddress like ?");
                args.add(HelperFunc.makeLikeArg(query.getIpAddress().trim(), true));
            }

            if (HelperFunc.isLikeRequired(query.getEmail())) {
                condition.append(" and info.a = 'PUBLIC' and info.t = 'EMAIL' and info.v like ?");
                args.add(HelperFunc.makeLikeArg(query.getEmail().trim(), true));
            }

            if (HelperFunc.isLikeRequired(query.getDepartment())) {
                String helper = HelperFunc.makeLikeArg(query.getDepartment().trim(), true);

                condition
                        .append(" and Person.id in (")
                        .append("select personId from company_dep cd " +
                                "left join company_dep cd2 on cd.parent_dep = cd2.id " +
                                "inner join worker_entry we on cd.id = we.dep_id")
                        .append(" where cd.dep_name like ? or cd2.dep_name like ?)");
                args.add(helper);
                args.add(helper);
            }
        });
    }
}
