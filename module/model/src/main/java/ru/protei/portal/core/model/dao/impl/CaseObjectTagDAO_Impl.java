package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseObjectTagDAO;
import ru.protei.portal.core.model.ent.CaseObjectTag;

public class CaseObjectTagDAO_Impl extends PortalBaseJdbcDAO<CaseObjectTag> implements CaseObjectTagDAO {

    @Override
    public int removeByCaseIdAndTagId(Long caseId, Long tagId) {
        return removeByCondition("case_id = ? and tag_id = ?", caseId, tagId);
    }

    @Override
    public boolean checkExists(Long caseId, Long tagId) {
        return checkExistsByCondition("case_id = ? and tag_id = ?", caseId, tagId);
    }
}
