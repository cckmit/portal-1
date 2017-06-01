package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;

/**
 * Created by Mike on 01.05.2017.
 */
public class CaseControlServiceImpl implements CaseControlService {

    @Autowired
    ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    CaseMemberDAO caseMemberDAO;

    @Autowired
    CaseCommentDAO commentDAO;

    @Autowired
    CaseLocationDAO caseLocationDAO;

    @Override
    @Transactional
    public void deleteByExtAppId(String extAppId) {
        ExternalCaseAppData object = externalCaseAppDAO.getByExternalAppId(extAppId);
        if (object != null) {
            caseLocationDAO.removeByCondition("CASE_ID=?", object.getId());
            caseMemberDAO.removeByCondition("CASE_ID=?", object.getId());
            commentDAO.removeByCondition("CASE_ID=?", object.getId());
            externalCaseAppDAO.remove(object);
        }
    }
}
