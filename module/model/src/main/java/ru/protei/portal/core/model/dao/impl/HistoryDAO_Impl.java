package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.HistoryDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.HistoryQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

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

            if (query.getValueType() != null) {
                condition.append(" and history.value_type = ?");
                args.add(query.getValueType().getId());
            }

            if (query.getOldValue() != null) {
                condition.append(" and history.old_value = ?");
                args.add(query.getOldValue());
            }

            if (query.getNewValue() != null) {
                condition.append(" and history.new_value = ?");
                args.add(query.getNewValue());
            }

        }));
    }
}
