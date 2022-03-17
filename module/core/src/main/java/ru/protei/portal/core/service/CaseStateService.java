package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.List;

public interface CaseStateService {
    @Privileged({ En_Privilege.CASE_STATES_VIEW })
    Result<List<CaseState>> getCaseStates(AuthToken authToken, En_CaseType type);

    Result<List<CaseState>> getCaseStatesOmitPrivileges(En_CaseType type);

    Result<List<CaseState>> getCaseStatesWithViewOrderOmitPrivileges(En_CaseType caseType);

    @Privileged({ En_Privilege.CASE_STATES_VIEW })
    Result<CaseState> getCaseState(AuthToken authToken, long id);

    Result<CaseState> getCaseStateWithoutCompaniesOmitPrivileges(long id);

    @Privileged({ En_Privilege.CASE_STATES_EDIT })
    Result<CaseState> createCaseState(AuthToken authToken, CaseState state);

    @Privileged({ En_Privilege.CASE_STATES_EDIT })
    Result<CaseState> updateCaseState(AuthToken authToken, CaseState state);

    Result<List<CaseState>> getCaseStatesForCompanyOmitPrivileges(Long companyId);

    Result<CaseState> getCaseStateByCaseIdOmitPrivileges(long caseId);

    Result<List<CaseState>> getCaseStatesByIds(List<Long> caseStatesIds);
}
