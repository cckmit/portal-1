package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.List;

public interface CaseStateDAO extends PortalBaseDAO<CaseState> {

    List<CaseState> getAllByCaseType(En_CaseType caseType);

    List<CaseState> getCaseStatesForCompany(Long companyId);
}
