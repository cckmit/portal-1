package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseLocationDAO;
import ru.protei.portal.core.model.dao.CaseMemberDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseObject;

/**
 * Created by Mike on 01.05.2017.
 */
public class CaseControlServiceImpl implements CaseControlService {

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseMemberDAO caseMemberDAO;

    @Autowired
    CaseCommentDAO commentDAO;

    @Autowired
    CaseLocationDAO caseLocationDAO;

    @Override
    @Transactional
    public void deleteByExtAppId(String extAppId) {
        CaseObject object = caseObjectDAO.getByCondition("ext_app_id=?", extAppId);
        if (object != null) {
            caseLocationDAO.removeByCondition("CASE_ID=?", object.getId());
            caseMemberDAO.removeByCondition("CASE_ID=?", object.getId());
            commentDAO.removeByCondition("CASE_ID=?", object.getId());
            caseObjectDAO.remove(object);
        }
    }
}
