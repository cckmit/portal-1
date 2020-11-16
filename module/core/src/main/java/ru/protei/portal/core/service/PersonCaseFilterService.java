package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;

public interface PersonCaseFilterService {
    Result<Void> processMailNotification();

    Result<List<CaseFilterShortView>> getCaseFilterByPersonId(AuthToken authToken, Long personId);

    Result<Boolean> addPersonToCaseFilter(AuthToken authToken, Long personId, Long caseFilterId);
    Result<Long> removePersonToCaseFilter(AuthToken authToken, Long personId, Long caseFilterId);
    Result<Boolean> changePersonToCaseFilter(AuthToken authToken, Long personId, Long oldCaseFilterId, Long newCaseFilterId);
}
