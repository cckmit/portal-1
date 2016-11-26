package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Company;

import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public class CaseCommentDAO_Impl extends PortalBaseJdbcDAO<CaseComment> implements CaseCommentDAO {
    @Override
    public List<CaseComment> getCaseComments( long caseId ) {
        return getListByCondition(" case_id=? ", caseId);
    }
}
