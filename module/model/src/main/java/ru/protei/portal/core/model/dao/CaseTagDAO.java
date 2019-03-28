package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.query.CaseTagQuery;

import java.util.List;

public interface CaseTagDAO extends PortalBaseDAO<CaseTag> {

    List<CaseTag> getListByQuery(CaseTagQuery query);
}
