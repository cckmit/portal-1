package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseObjectTagDAO;
import ru.protei.portal.core.model.ent.CaseObjectTag;

import java.util.List;

public class CaseObjectTagDAO_Impl extends PortalBaseJdbcDAO<CaseObjectTag> implements CaseObjectTagDAO {

    @Override
    public List<CaseObjectTag> getListByCaseId(long caseId) {
        return getListByCondition("case_id = ?", caseId);
    }
}
