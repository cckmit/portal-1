package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.PlanDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class PlanDAO_Impl extends PortalBaseJdbcDAO<Plan> implements PlanDAO {

    @Override
    public List<Plan> getListByQuery(PlanQuery query) {
        if (query.getSortDir() == null) {
            query.setSortField(En_SortField.id);
            query.setSortDir(En_SortDir.ASC);
        }

        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query))
        );
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(PlanQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (query.getName() != null) {
                condition.append(" and plan.name = ?");
                args.add(query.getName());
            }

            if (query.getCreated() != null) {
                condition.append(" and plan.created = ?");
                args.add(query.getCreated());
            }

            if (query.getCreatorId() != null) {
                condition.append(" and plan.creator_id = ?");
                args.add(query.getCreatorId());
            }

            if (query.getStartDateFrom() != null) {
                condition.append(" and plan.start_date >= ?");
                args.add(query.getStartDateFrom());
            }

            if (query.getStartDateTo() != null) {
                condition.append(" and plan.start_date <= ?");
                args.add(query.getStartDateTo());
            }

            if (query.getFinishDateFrom() != null) {
                condition.append(" and plan.finish_date >= ?");
                args.add(query.getFinishDateFrom());
            }

            if (query.getFinishDateTo() != null) {
                condition.append(" and plan.finish_date <= ?");
                args.add(query.getFinishDateTo());
            }

            if (query.getIssueId() != null) {
                condition.append(" and plan.id in (select plan_id from plan_to_case_object where case_object_id=?)");
                args.add(query.getIssueId());
            }

            if (query.getIssueNumber() != null) {
                condition.append(" and plan.id in (select plan_id from plan_to_case_object " +
                        "inner join case_object on plan_to_case_object.case_object_id = case_object.id" +
                        " where case_object.caseno=?)");
                args.add(query.getIssueNumber());
            }
        }));
    }
}
