package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.DateRangeUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static ru.protei.portal.core.utils.DateUtils.resetSeconds;

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

            if (query.getBirthdayRange() != null) {
                Interval interval = DateRangeUtils.makeInterval(query.getBirthdayRange());

                boolean isOneDay = Objects.equals(interval.from, interval.to);
                boolean isSameYear = Objects.equals(interval.from.getYear(), interval.to.getYear());
                if (isOneDay) {
                    condition.append(" and person.birthday = ?");
                    args.add(interval.from);
                } else if (isSameYear) {
                    condition.append(" and person.birthday is not null")
                            .append(" and ")
                            .append("date_format(person.birthday, '%m%d') between date_format(?, '%m%d') and date_format(?, '%m%d')");
                    args.add(interval.from);
                    args.add(interval.to);
                } else {
                    condition.append(" and person.birthday is not null")
                            .append(" and ((")
                            .append("date_format(person.birthday, '%m%d') between date_format(?, '%m%d') and date_format(?, '%m%d')")
                            .append(") or (")
                            .append("date_format(person.birthday, '%m%d') between date_format(?, '%m%d') and date_format(?, '%m%d')")
                            .append("))");
                    Date from1 = interval.from;
                    Date until1 = Date.from(interval.from
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .with(lastDayOfYear())
                            .atZone(ZoneId.systemDefault())
                            .toInstant());
                    args.add(from1);
                    args.add(until1);
                    Date from2 = Date.from(interval.to
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .with(firstDayOfYear())
                            .atZone(ZoneId.systemDefault())
                            .toInstant());
                    Date until2 = interval.to;
                    args.add(from2);
                    args.add(until2);
                }
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

            if (query.getAbsent() != null && query.getAbsent()) {
                condition.append(" and person.id in ")
                        .append("(select person_id from person_absence where from_time <= ? and till_time >= ?)");
                args.add(resetSeconds(new Date()));
                args.add(resetSeconds(new Date()));
            }

            if (CollectionUtils.isNotEmpty(query.getDepartmentIds())) {
                condition.append(" and person.id in ")
                        .append("(select personId from worker_entry where worker_entry.dep_id in ")
                        .append(HelperFunc.makeInArg(query.getDepartmentIds(), String::valueOf))
                        .append(")");
            }

            if (query.getAbsent() != null && query.getAbsent()) {
                condition.append(" and person.id in ")
                        .append("(select person_id from person_absence where from_time <= ? and till_time >= ? and reason_id in " +
                                HelperFunc.makeInArg( Arrays.asList(En_AbsenceReason.values()).stream()
                                        .filter(En_AbsenceReason::isActual)
                                        .map(En_AbsenceReason::getId)
                                        .collect(Collectors.toSet()))).append(")");
                args.add(resetSeconds(new Date()));
                args.add(resetSeconds(new Date()));
            }
        });
    }
}
