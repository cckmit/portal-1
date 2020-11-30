package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

public class CaseStateDAO_Impl extends PortalBaseJdbcDAO<CaseState> implements CaseStateDAO {

    @Override
    public List<CaseState> getAllByCaseType(En_CaseType caseType) {
        List<CaseState> caseStates = getList(new JdbcQueryParameters()
                .withJoins("RIGHT JOIN case_state_matrix mtx on mtx.CASE_STATE = case_state.ID ")
                .withCondition("mtx.CASE_TYPE=? ", caseType.getId())
                .withSort(new JdbcSort(JdbcSort.Direction.ASC, "VIEW_ORDER"))
        );

        return caseStates;
    }

    @Override
    public List<CaseState> getCaseStatesForCompany(Long companyId) {
        List<CaseState> caseStates = getList(new JdbcQueryParameters()
                .withJoins("JOIN case_state_to_company cstc on cstc.state_id = case_state.ID ")
                .withCondition("cstc.company_id=? ", companyId)
        );

        return caseStates;
    }

    @Override
    public CaseState getCaseStateByCaseId(Long caseId) {
        return getByCondition("id = (select STATE from case_object where id = ?)", caseId);
    }
}
