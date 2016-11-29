package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;

import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public interface CaseCommentDAO extends PortalBaseDAO<CaseComment> {

    List<CaseComment> getCaseComments( long caseId );
}
