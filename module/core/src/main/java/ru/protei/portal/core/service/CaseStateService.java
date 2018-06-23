package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;

import java.util.List;

public interface CaseStateService {
    @Privileged({ En_Privilege.CASE_STATES_VIEW })
    CoreResponse<List<CaseState>> caseStateList(AuthToken authToken);

    @Privileged({ En_Privilege.CASE_STATES_VIEW })
    CoreResponse<CaseState> getCaseState(AuthToken authToken, long id);

    @Privileged({ En_Privilege.CASE_STATES_EDIT })
    CoreResponse<CaseState> saveCaseState(AuthToken authToken, CaseState state);

    @Privileged({ En_Privilege.CASE_STATES_EDIT })
    CoreResponse<CaseState> updateCaseState(AuthToken authToken, CaseState state);
}
