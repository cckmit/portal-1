package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseMemberDAO;
import ru.protei.portal.core.model.ent.CaseMember;

import java.util.List;

/**
 * DAO для членов команды проекта
 */
public class CaseMemberDAO_Impl extends PortalBaseJdbcDAO<CaseMember> implements CaseMemberDAO {

    @Override
    public List<CaseMember> listByCaseId(Long caseId) {
        return getListByCondition("case_member.CASE_ID = ?", caseId);
    }
}
