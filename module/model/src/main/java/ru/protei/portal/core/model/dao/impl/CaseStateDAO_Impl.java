package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CaseStateMatrix;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.ArrayList;
import java.util.List;

public class CaseStateDAO_Impl extends PortalBaseJdbcDAO<CaseState> implements CaseStateDAO {

    String sql ="RIGHT JOIN portal_dev.case_state_matrix mtx on mtx.CASE_STATE = case_state.ID ";

    @Override
    public List<CaseState> getAllByCaseType(En_CaseType caseType) {
        List<CaseState> caseStates = getList(new JdbcQueryParameters()
                .withJoins(sql)
                .withCondition("mtx.CASE_TYPE=? ", caseType.getId())
                 .withSort(new JdbcSort(JdbcSort.Direction.ASC, "VIEW_ORDER"))
         );

        return caseStates;
    }
}
