package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseStateQuery;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

public class CaseStateDAO_Impl extends PortalBaseJdbcDAO<CaseState> implements CaseStateDAO {

    public static final String RIGHT_JOIN_CASE_STATE_MATRIX = "RIGHT JOIN case_state_matrix mtx on mtx.CASE_STATE = case_state.ID";
    public static final String JOIN_CASE_STATE_TO_COMPANY = "JOIN case_state_to_company cstc on cstc.state_id = case_state.ID";

    @Override
    public List<CaseState> getListByQuery(CaseStateQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getList(parameters);
    }


    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CaseStateQuery query) {
        return  new SqlCondition().build((condition, args) -> {
            condition.append("1=1");
            if (query.getType() != null) {
                condition.append(" and mtx.CASE_TYPE=?");
                args.add(query.getType().getId());
            }
            if (CollectionUtils.isNotEmpty(query.getIds())) {
                condition.append(" and case_state.ID in " + HelperFunc.makeInArg(query.getIds()));
                args.add(query.getType().getId());
            }
            if (query.getCompanyId() != null) {
                condition.append(" and cstc.company_id=?");
                args.add(query.getCompanyId());
            }
        });
    }

    private JdbcQueryParameters buildJdbcQueryParameters(CaseStateQuery query) {
        JdbcQueryParameters parameters = new JdbcQueryParameters();
        SqlCondition where = createSqlCondition(query);
        if (where.isConditionDefined()) {
            parameters.withCondition(where.condition, where.args);
        }
        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(TypeConverters.createSort( query ));
        if (query.getType() != null) {
            parameters.withJoins(RIGHT_JOIN_CASE_STATE_MATRIX);
        }
        if (query.getCompanyId() != null) {
            parameters.withJoins(JOIN_CASE_STATE_TO_COMPANY);
        }
        return parameters;
    }
}
