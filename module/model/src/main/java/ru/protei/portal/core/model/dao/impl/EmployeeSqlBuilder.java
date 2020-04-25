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
            condition.append("person.company_id in (select companyId from company_group_home)");

            if (CollectionUtils.isNotEmpty(query.getIds())) {
                condition.append(" and person.id in " + HelperFunc.makeInArg(query.getIds()));
            }

            if (query.getFired() != null) {
                condition.append(" and person.isfired=?");
                args.add(query.getFired() ? 1 : 0);
            }


            if (query.getDeleted() != null) {
                condition.append(" and person.isdeleted=?");
                args.add(query.getDeleted() ? 1 : 0);
            }

            if (query.getFirstName() != null) {
                condition.append(" and person.firstname=?");
                args.add(query.getFirstName());
            }

            if (query.getLastName() != null) {
                condition.append(" and person.lastname=?");
                args.add(query.getLastName());
            }

            if (query.getBirthday() != null) {
                condition.append(" and person.birthday=?");
                args.add(query.getBirthday());
            }

            if (query.getSecondName() != null) {
                condition.append(" and person.secondname=?");
                args.add(query.getSecondName());
            }


            if (query.getOnlyPeople() != null) {
                condition.append(" and person.sex != ?");
                args.add(En_Gender.UNDEFINED.getCode());
            }

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                if (query.getSearchString().trim().contains(" ")) {
                    condition.append(" and person.displayname like ?");
                    args.add(HelperFunc.makeLikeArg(query.getSearchString().trim(), true));
                } else {
                    condition.append(" and (person.lastname like ?");
                    args.add(HelperFunc.makeLikeArg(query.getSearchString().trim(), true));
                    condition.append(" or person.firstname like ?)");
                    args.add(HelperFunc.makeLikeArg(query.getSearchString().trim(), true));
                }
            }

            if (HelperFunc.isLikeRequired(query.getIpAddress())) {
                condition.append(" and person.ipaddress like ?");
                args.add(HelperFunc.makeLikeArg(query.getIpAddress().trim(), true));
            }

            if (HelperFunc.isLikeRequired(query.getWorkPhone()) || HelperFunc.isLikeRequired(query.getMobilePhone()) || HelperFunc.isLikeRequired(query.getEmail())) {
                condition.append(" and info.a = 'PUBLIC' and (");

                List<String> orCondition = new ArrayList<>();

                if (HelperFunc.isLikeRequired(query.getWorkPhone())) {
                    orCondition.add("(info.t = 'GENERAL_PHONE' and info.v like ?)");
                    args.add(HelperFunc.makeLikeArg(query.getWorkPhone(), true));
                }

                if (HelperFunc.isLikeRequired(query.getMobilePhone())) {
                    orCondition.add("(info.t = 'MOBILE_PHONE' and info.v like ?)");
                    args.add(HelperFunc.makeLikeArg(query.getMobilePhone(), true));
                }

                if (HelperFunc.isLikeRequired(query.getEmail())) {
                    orCondition.add("(info.t = 'EMAIL' and info.v like ?)");
                    args.add(HelperFunc.makeLikeArg(query.getEmail().trim(), true));
                }

                condition.append(String.join(" or ", orCondition));
                condition.append(")");
            }

            if (HelperFunc.isLikeRequired(query.getDepartment())) {
                String helper = HelperFunc.makeLikeArg(query.getDepartment().trim(), true);

                condition
                        .append(" and person.id in (")
                        .append("select personId from company_dep cd " +
                                "left join company_dep cd2 on cd.parent_dep = cd2.id " +
                                "inner join worker_entry we on cd.id = we.dep_id")
                        .append(" where cd.dep_name like ? or cd2.dep_name like ?)");
                args.add(helper);
                args.add(helper);
            }

            if (CollectionUtils.isNotEmpty(query.getHomeCompanies())) {
                condition.append(" and person.id in ")
                        .append("(select personId from worker_entry where active > 0 and companyId in ")
                        .append(HelperFunc.makeInArg(query.getHomeCompanies(), s -> String.valueOf(s.getId())))
                        .append(")");
            }
        });
    }
}
