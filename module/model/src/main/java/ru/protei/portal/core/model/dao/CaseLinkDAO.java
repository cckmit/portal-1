package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.query.CaseLinkQuery;

import java.util.List;

public interface CaseLinkDAO extends PortalBaseDAO<CaseLink> {

    List<CaseLink> getListByQuery(CaseLinkQuery query);
}
