package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseObjectTag;

import java.util.List;

public interface CaseObjectTagDAO extends PortalBaseDAO<CaseObjectTag> {

    List<CaseObjectTag> getListByCaseId(long caseId);
}
