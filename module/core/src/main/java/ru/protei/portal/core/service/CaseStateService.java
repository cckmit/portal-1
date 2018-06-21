package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.List;

public interface CaseStateService {
    CoreResponse<List<CaseState>> caseStateList();

    CoreResponse<CaseState> getCaseState(long id);
}
