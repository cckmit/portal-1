package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSqlBuilder {
    public SqlCondition createSqlCondition(EmployeeQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("Person.company_id in (select companyId from company_group_home)");

            if (CollectionUtils.isNotEmpty(query.getIds())) {
                condition.append(" and Person.id in " + HelperFunc.makeInArg(query.getIds()));
            }

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
                if (query.getSearchString().trim().contains(" ")) {
                    condition.append(" and Person.displayname like ?");
                    args.add(HelperFunc.makeLikeArg(query.getSearchString().trim(), true));
                } else {
                    condition.append(" and (Person.lastname like ?");
                    args.add(HelperFunc.makeLikeArg(query.getSearchString().trim(), true));
                    condition.append(" or Person.firstname like ?)");
                    args.add(HelperFunc.makeLikeArg(query.getSearchString().trim(), true));
                }
            }

            if (HelperFunc.isLikeRequired(query.getIpAddress())) {
                condition.append(" and Person.ipaddress like ?");
                args.add(HelperFunc.makeLikeArg(query.getIpAddress().trim(), true));
            }

            if (HelperFunc.isLikeRequired(query.getEmail())) {
                condition.append(" and id IN (SELECT id FROM person_email WHERE access = 'PUBLIC' AND value LIKE ?)");
                args.add(HelperFunc.makeLikeArg(query.getEmail().trim(), true));
            }

            if (HelperFunc.isLikeRequired(query.getWorkPhone())) {
                condition.append(" and id IN (SELECT id FROM person_phone WHERE access = 'PUBLIC' AND type = 'GENERAL_PHONE' AND value LIKE ?)");
                args.add(HelperFunc.makeLikeArg(query.getWorkPhone(), true));
            }

            if (HelperFunc.isLikeRequired(query.getMobilePhone())) {
                condition.append(" and id IN (SELECT id FROM person_phone WHERE access = 'PUBLIC' AND type = 'MOBILE_PHONE' AND value LIKE ?)");
                args.add(HelperFunc.makeLikeArg(query.getMobilePhone(), true));
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
