package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;

import java.util.List;

public interface CaseStateService {
    CoreResponse<List<CaseState>> caseStateList(AuthToken authToken);

    CoreResponse<CaseState> getCaseState(AuthToken authToken, long id);

    CoreResponse<CaseState> saveCaseState(AuthToken authToken, CaseState state);

    CoreResponse<CaseState> updateCaseState(AuthToken authToken, CaseState state);
}
