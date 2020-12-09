package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.HistoryDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.HistoryQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;

public class HistoryDAO_Impl extends PortalBaseJdbcDAO<History> implements HistoryDAO {

    @Override
    public List<History> getListByQuery(HistoryQuery query) {
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

    @Override
    public void removeByCaseId(Long caseId) {
        Condition condition = condition().and("case_object_id").equal(caseId);
        removeByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(HistoryQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (query.getInitiatorId() != null) {
                condition.append(" and history.initiator_id = ?");
                args.add(query.getInitiatorId());
            }

            if (query.getDateFrom() != null) {
                condition.append(" and history.date >= ?");
                args.add(query.getDateFrom());
            }

            if (query.getDateTo() != null) {
                condition.append(" and history.date <= ?");
                args.add(query.getDateTo());
            }

            if (query.getCaseObjectId() != null) {
                condition.append(" and history.case_object_id = ?");
                args.add(query.getCaseObjectId());
            }

            if (query.getCaseNumber() != null) {
                condition.append(" and history.case_object_id in (select case_object.ID from case_object where case_object.CASENO = ?");
                args.add(query.getCaseNumber());
            }

            if (isNotEmpty(query.getValueTypes())) {
                condition.append(" and history.value_type in ").append(makeInArg(query.getValueTypes(), false));
            }

            if (isNotEmpty(query.getHistoryActions())) {
                condition.append(" and history.action_type in ").append(makeInArg(query.getHistoryActions(), false));
            }

            if (query.getOldId() != null) {
                condition.append(" and history.old_id = ?");
                args.add(query.getOldId());
            }

            if (query.getNewId() != null) {
                condition.append(" and history.new_id = ?");
                args.add(query.getNewId());
            }

        }));
    }
}
