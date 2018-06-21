package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

public class CaseLinkDAO_Impl extends PortalBaseJdbcDAO<CaseLink> implements CaseLinkDAO {

    @Override
    public List<CaseLink> getByCaseId(long caseId) {
        return getListByCondition("case_id = ?", new JdbcSort(JdbcSort.Direction.ASC, "id"), caseId);
    }
}
