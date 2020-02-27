package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.query.CaseTagQuery;

import java.util.List;

public interface CaseTagDAO extends PortalBaseDAO<CaseTag> {

    List<CaseTag> getListByQuery(CaseTagQuery query);

    boolean isNameUniqueForTag( Long id, En_CaseType caseType, Long companyId, String name );
}
