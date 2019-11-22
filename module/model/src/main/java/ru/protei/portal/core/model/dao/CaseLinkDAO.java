package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.query.CaseLinkQuery;

import java.util.List;

public interface CaseLinkDAO extends PortalBaseDAO<CaseLink> {

    List<CaseLink> getListByQuery(CaseLinkQuery query);

    boolean checkExistLink(En_CaseLink link, String remoteId);

    Long getCrmLinkId(String remoteId);
}
