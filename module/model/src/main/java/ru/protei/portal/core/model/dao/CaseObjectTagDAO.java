package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseObjectTag;

import java.util.List;

public interface CaseObjectTagDAO extends PortalBaseDAO<CaseObjectTag> {

    int removeByCaseIdAndTagId(Long caseId, Long tagId);

    boolean checkExists(Long caseId, Long tagId);

    List<CaseObjectTag> list(List<Long> caseIds, List<Long> tagIds, List<Long> companyIds);
}
